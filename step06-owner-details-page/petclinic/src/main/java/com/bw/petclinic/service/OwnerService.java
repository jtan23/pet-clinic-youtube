package com.bw.petclinic.service;

import com.bw.petclinic.domain.CustomPageImpl;
import com.bw.petclinic.domain.Owner;
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
public class OwnerService {

    @Value("${service.url.owner}")
    private String ownerServiceUrl;

    private final RestTemplate restTemplate;

    public OwnerService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public Page<Owner> findAll(int pageNumber, int pageSize) {
        String uri = UriComponentsBuilder
                .fromHttpUrl(ownerServiceUrl)
                .queryParam("pageNumber", pageNumber)
                .queryParam("pageSize", pageSize)
                .build()
                .toUriString();
        log.info("findAll uri [" + uri + "]");
        return restTemplate
                .exchange(uri, HttpMethod.GET, null,
                        new ParameterizedTypeReference<CustomPageImpl<Owner>>() {})
                .getBody();
    }

    public Page<Owner> findByLastName(int pageNumber, int pageSize, String lastName) {
        String uri = UriComponentsBuilder
                .fromHttpUrl(ownerServiceUrl)
                .queryParam("pageNumber", pageNumber)
                .queryParam("pageSize", pageSize)
                .queryParam("lastName", lastName)
                .build()
                .toUriString();
        log.info("findByLastName uri [" + uri + "]");
        return restTemplate
                .exchange(uri, HttpMethod.GET, null,
                        new ParameterizedTypeReference<CustomPageImpl<Owner>>() {})
                .getBody();
    }

    public Owner findById(int id) {
        log.info("findById id [" + id + "]");
        return restTemplate
                .exchange(ownerServiceUrl + "/" + id, HttpMethod.GET, null, Owner.class)
                .getBody();
    }

}
