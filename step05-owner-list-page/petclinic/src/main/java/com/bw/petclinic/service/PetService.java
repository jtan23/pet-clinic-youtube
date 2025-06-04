package com.bw.petclinic.service;

import com.bw.petclinic.domain.Pet;
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
public class PetService {

    @Value("${service.url.pet}")
    private String petServiceUrl;

    private final RestTemplate restTemplate;

    public PetService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public List<Pet> findByOwnerId(int ownerId) {
        String uri = UriComponentsBuilder
                .fromHttpUrl(petServiceUrl)
                .queryParam("ownerId", ownerId)
                .build().toUriString();
        log.info("findByOwnerId uri [" + uri + "]");
        return restTemplate
                .exchange(uri, HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<Pet>>() {})
                .getBody();
    }

}
