package com.bw.dbinit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.*;

public class Handler {

    private static final String REGION= System.getenv("REGION");
    private static final String DB_ENDPOINT = System.getenv("DB_ENDPOINT");
    private static final String DB_NAME = System.getenv("DB_NAME");
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASS = System.getenv("DB_PASSWORD");
    private static final String SQL_SCHEME_PARAM = System.getenv("SQL_SCHEME_PARAM");
    private static final String SQL_DATA_PARAM = System.getenv("SQL_DATA_PARAM");
    private static final String MYSQL_DRIVER_NAME = "com.mysql.cj.jdbc.Driver";
    private static final String HTTP_SERVICE_NAME = "software.amazon.awssdk.http.service.impl";
    private static final String HTTP_SERVICE_VALUE = "software.amazon.awssdk.http.urlconnection.UrlConnectionSdkHttpService";

    private static final Logger log = LoggerFactory.getLogger(Handler.class);

    public String handle(Object event) {
        log.info("Handle MySQL initialisation request event Object type [" + event.getClass() + "]");
        System.setProperty(HTTP_SERVICE_NAME, HTTP_SERVICE_VALUE);

        @SuppressWarnings("unchecked")
        Map<String, String> eventMap = (Map<String, String>) event;
        log.info("Event [" + eventMap + "]");

        if (StringUtils.equals(eventMap.get("RequestType"), "Create")) {
            try {
                handleCreate();
                sendResponse(eventMap, "SUCCESS");
            } catch (Exception e) {
                log.error("Handler Create Failed", e);
                sendResponse(eventMap, "FAILED");
            }
        } else {
            sendResponse(eventMap, "SUCCESS");
        }
        return "JobFinished";
    }

    private void handleCreate() throws Exception {
        log.info("Handle Create");
        List<String> queries = getSqlQueries();
        Class.forName(MYSQL_DRIVER_NAME);
        try (Connection connection = DriverManager
                .getConnection("jdbc:mysql://" + DB_ENDPOINT + ":3306/" + DB_NAME, DB_USER, DB_PASS);
             Statement statement = connection.createStatement()) {
            StringBuilder queryBuilder = new StringBuilder();
            for (String query : queries) {
                queryBuilder.append(" ").append(query);
                if (query.endsWith(";")) {
                    String queryString = queryBuilder.toString();
                    log.info("Executing [" + queryString + "]");
                    statement.execute(queryString);
                    queryBuilder = new StringBuilder();
                }
            }
        }
    }

    private List<String> getSqlQueries() {
        log.info("Getting queries from Parameter Store");
        List<String> queries = new ArrayList<>();
        Region region = Region.of(REGION);
        try (SsmClient ssmClient = SsmClient
                .builder()
                .region(region)
                .httpClientBuilder(UrlConnectionHttpClient.builder())
                .build()) {
            queries.addAll(getQueriesFromParameterStore(ssmClient, SQL_SCHEME_PARAM));
            queries.addAll(getMoreQueriesFromParameterStore(ssmClient, SQL_SCHEME_PARAM));
            queries.addAll(getQueriesFromParameterStore(ssmClient, SQL_DATA_PARAM));
            queries.addAll(getMoreQueriesFromParameterStore(ssmClient, SQL_DATA_PARAM));
        }
        return queries;
    }

    private List<String> getMoreQueriesFromParameterStore(SsmClient ssmClient, String parameterName) {
        List<String> moreQueries = new ArrayList<>();
        int count = 1;
        while (true) {
            List<String> queries = getQueriesFromParameterStore(ssmClient, parameterName + "-" + count);
            if (queries.isEmpty()) {
                break;
            }
            moreQueries.addAll(queries);
            count++;
        }
        return moreQueries;
    }

    private List<String> getQueriesFromParameterStore(SsmClient ssmClient, String parameterName) {
        log.info("Getting queries from parameter store [" + parameterName + "]");
        try {
            GetParameterResponse response = ssmClient.getParameter(GetParameterRequest.builder().name(parameterName).build());
            return Arrays.asList(StringUtils.split(response.parameter().value(), "\n"));
        } catch (Exception ex) {
            log.info("Get parameter [" + parameterName + "] failed [" + ex.getMessage() + "]");
            return new ArrayList<>();
        }
    }

    public void sendResponse(Map<String, String> eventMap, String status) {
        String responseJson = createResponseJson(eventMap, status);
        log.info("Sending Response [" + responseJson + "]");

        var request = HttpRequest.newBuilder()
                .uri(URI.create(eventMap.get("ResponseURL")))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(responseJson))
                .build();
        var client = HttpClient.newHttpClient();
        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("PUT result code [" + response.statusCode() + "], body [" + response.body() + "]");
        } catch (Exception e) {
            log.error("Send Response Failed", e);
        }
        log.info("Response sent");
    }

    private String createResponseJson(Map<String, String> eventMap, String status) {
        return "{" +
                "\"Status\":\"" + status + "\"," +
                "\"Reason\":\"" + "Initialise MySQL Database" + "\"," +
                "\"PhysicalResourceId\":\"" + "CustomResourcePhysicalID" + "\"," +
                "\"StackId\":\"" + eventMap.get("StackId") + "\"," +
                "\"RequestId\":\"" + eventMap.get("RequestId") + "\"," +
                "\"LogicalResourceId\":\"" + eventMap.get("LogicalResourceId") + "\"," +
                "\"NoEcho\":false," +
                "\"Data\":{\"Key\":\"Value\"}" +
                "}";
    }

    /**
     * Manual run to push the CloudFormation Resource out of waiting state.
     *
     * @param args command line args
     */
    public static void main(String[] args) {
        Map<String, String> event = new HashMap<>();
        event.put("ResponseURL", "");
        event.put("StackId", "");
        event.put("RequestId", "");
        event.put("LogicalResourceId", "");
        new Handler().sendResponse(event, "SUCCESS");
    }

}
