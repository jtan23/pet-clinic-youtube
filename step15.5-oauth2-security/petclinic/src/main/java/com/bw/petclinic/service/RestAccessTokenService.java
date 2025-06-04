package com.bw.petclinic.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.client.RestTemplate;

@Slf4j
public abstract class RestAccessTokenService {

    protected final RestTemplate restTemplate;

    private final OAuth2AuthorizedClientService clientService;

    private final ObjectMapper objectMapper;

    public RestAccessTokenService(RestTemplateBuilder builder, OAuth2AuthorizedClientService clientService) {
        this.restTemplate = builder.build();
        this.clientService = clientService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    protected HttpEntity<String> createBearerAuthWithAccessToken(String body) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String clientRegistrationId = oauthToken.getAuthorizedClientRegistrationId();
        String principalName = oauthToken.getName();
        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(clientRegistrationId, principalName);
        log.info("AccessToken [" + client.getAccessToken().getTokenValue() + "]");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(client.getAccessToken().getTokenValue());
        return new HttpEntity<>(body, headers);
    }

    protected String toJsonString(Object source) {
        try {
            String jsonString = new String(objectMapper.writeValueAsBytes(source));
            log.info("JSON String [" + jsonString + "]");
            return jsonString;
        } catch (Exception ex) {
            throw new RuntimeException("TO JSON String failed", ex);
        }
    }

}
