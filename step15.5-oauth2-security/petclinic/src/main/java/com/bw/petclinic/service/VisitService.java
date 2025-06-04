package com.bw.petclinic.service;

import com.bw.petclinic.domain.Visit;
import com.bw.petclinic.exception.PetClinicServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
@Slf4j
public class VisitService extends RestAccessTokenService {

    @Value("${service.url.visit}")
    private String visitServiceUrl;

    public VisitService(RestTemplateBuilder builder, OAuth2AuthorizedClientService clientService) {
        super(builder, clientService);
    }

    public List<Visit> findByPetId(int petId) {
        String uri = UriComponentsBuilder
                .fromHttpUrl(visitServiceUrl)
                .queryParam("petId", petId)
                .build()
                .toUriString();
        log.info("findByPetId uri [" + uri + "]");
        try {
            return restTemplate
                    .exchange(uri,
                            HttpMethod.GET,
                            createBearerAuthWithAccessToken(""),
                            new ParameterizedTypeReference<List<Visit>>() {})
                    .getBody();
        } catch (HttpClientErrorException ex) {
            throw new PetClinicServiceException("VisitService.findByPetId failed [" + ex.getMessage() + "]");
        }
    }

    public Visit add(Visit visit) {
        log.info("add " + visit);
        try {
            return restTemplate
                    .exchange(visitServiceUrl,
                            HttpMethod.POST,
                            createBearerAuthWithAccessToken(toJsonString(visit)),
                            Visit.class)
                    .getBody();
        } catch (HttpClientErrorException ex) {
            throw new PetClinicServiceException("VisitService.add failed [" + ex.getMessage() + "]");
        }
    }

}
