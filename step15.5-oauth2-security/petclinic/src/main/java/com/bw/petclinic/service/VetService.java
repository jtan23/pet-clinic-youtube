package com.bw.petclinic.service;

import com.bw.petclinic.domain.CustomPageImpl;
import com.bw.petclinic.domain.Vet;
import com.bw.petclinic.exception.PetClinicServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
public class VetService extends RestAccessTokenService {

    @Value("${service.url.vet}")
    private String vetServiceUrl;

    public VetService(RestTemplateBuilder builder, OAuth2AuthorizedClientService clientService) {
        super(builder, clientService);
    }

    public Page<Vet> findAll(int pageNumber, int pageSize) {
        String uri = UriComponentsBuilder
                .fromHttpUrl(vetServiceUrl)
                .queryParam("pageNumber", pageNumber)
                .queryParam("pageSize", pageSize)
                .build()
                .toUriString();
        log.info("findAll uri [" + uri + "]");
        try {
            return restTemplate
                    .exchange(uri,
                            HttpMethod.GET,
                            createBearerAuthWithAccessToken(""),
                            new ParameterizedTypeReference<CustomPageImpl<Vet>>() {})
                    .getBody();
        } catch (HttpClientErrorException ex) {
            throw new PetClinicServiceException("VetService.findAll failed [" + ex.getMessage() + "]");
        }
    }

}
