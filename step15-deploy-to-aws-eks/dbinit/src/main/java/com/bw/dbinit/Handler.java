package com.bw.dbinit;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
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

    public String handle(Object event) {
        System.out.println("Handle MySQL initialisation request: " + event);
        System.setProperty(HTTP_SERVICE_NAME, HTTP_SERVICE_VALUE);

        Map<String, String> eventMap = getEventMap(event);
        if (StringUtils.equals(eventMap.get("RequestType"), "Create")) {
            try {
                handleCreate(eventMap);
                sendResponse(eventMap, "SUCCESS");
            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(eventMap, "FAILED");
            }
        } else {
            sendResponse(eventMap, "SUCCESS");
        }
        return "JobFinished";
    }

    private Map<String, String> getEventMap(Object event) {
        Gson gson = new Gson();
        String gsonString = gson.toJson(event);
        System.out.println("Gson string for event [" + gsonString + "]");
        return gson.fromJson(gsonString, Map.class);
    }

    private void handleCreate(Map<String, String> eventMap) throws Exception {
        System.out.println("Handle Create");
        List<String> queries = getSqlQueries();
        Class.forName(MYSQL_DRIVER_NAME);
        try (Connection connection = DriverManager
                .getConnection("jdbc:mysql://" + DB_ENDPOINT + ":3306/" + DB_NAME, DB_USER, DB_PASS);
             Statement statement = connection.createStatement()) {
            for (String query : queries) {
                System.out.println("Executing: " + query);
                statement.execute(query);
            }
        }
    }

    private List<String> getSqlQueries() {
        System.out.println("Getting queries from Parameter Store");
        List<String> queries = new ArrayList<>();
        Region region = Region.of(REGION);
        try (SsmClient ssmClient = SsmClient
                .builder()
                .region(region)
                .httpClientBuilder(UrlConnectionHttpClient.builder())
                .build()) {
            queries.addAll(getQueriesFromParameterStore(ssmClient, SQL_SCHEME_PARAM));
            queries.addAll(getQueriesFromParameterStore(ssmClient, SQL_DATA_PARAM));
        }
        return queries;
    }

    private List<String> getQueriesFromParameterStore(SsmClient ssmClient, String parameterName) {
        GetParameterResponse response = ssmClient.getParameter(GetParameterRequest.builder().name(parameterName).build());
        return Arrays.asList(StringUtils.split(response.parameter().value(), "\n"));
    }

    public void sendResponse(Map<String, String> eventMap, String status) {
        System.out.println("Send Response status: " + status);
        System.out.println("Send Response event: " + eventMap);

        String responseJson = createResponseJson(eventMap, status);
        System.out.println("Response JSON: " + responseJson);

        var request = HttpRequest.newBuilder()
                .uri(URI.create(eventMap.get("ResponseURL")))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(responseJson))
                .build();

        System.out.println("Sending Response to stack");
        var client = HttpClient.newHttpClient();
        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response code: " + response.statusCode());
            System.out.println("Response body: " + response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Finish sending response");
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
     * @param args
     */
    public static void main(String[] args) {
        Map<String, String> event = new HashMap<>();
        event.put("ResponseURL", "");
        event.put("StackId", "");
        event.put("RequestId", "");
        event.put("LogicalResourceId", "");
        try {
            new Handler().sendResponse(event, "SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
