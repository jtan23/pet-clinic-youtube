package com.bw.petclinic.service;

import com.bw.petclinic.domain.Visit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
@Slf4j
public class VisitService {

    @Value("${service.url.visit}")
    private String visitServiceUrl;

    private final RestTemplate restTemplate;

    public VisitService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public List<Visit> findByPetId(int petId) {
        String uri = UriComponentsBuilder
                .fromHttpUrl(visitServiceUrl)
                .queryParam("petId", petId)
                .build()
                .toUriString();
        log.info("findByPetId uri [" + uri + "]");
        return restTemplate
                .exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<List<Visit>>() {})
                .getBody();
    }

    public Visit add(Visit visit) {
        log.info("add " + visit);
        return restTemplate.postForObject(visitServiceUrl, visit, Visit.class);
    }

}
