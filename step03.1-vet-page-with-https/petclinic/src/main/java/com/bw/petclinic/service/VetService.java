package com.bw.petclinic.service;

import com.bw.petclinic.domain.CustomPageImpl;
import com.bw.petclinic.domain.Vet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
public class VetService {

    @Value("${service.url.vet}")
    private String vetServiceUrl;

    private final RestTemplate restTemplate;

    public VetService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public Page<Vet> findAll(int pageNumber, int pageSize) {
        String uri = UriComponentsBuilder
                .fromHttpUrl(vetServiceUrl)
                .queryParam("pageNumber", pageNumber)
                .queryParam("pageSize", pageSize)
                .build()
                .toUriString();
        log.info("findAll uri [" + uri + "]");
        return restTemplate
                .exchange(uri, HttpMethod.GET, null,
                        new ParameterizedTypeReference<CustomPageImpl<Vet>>() {})
                .getBody();
    }

}
