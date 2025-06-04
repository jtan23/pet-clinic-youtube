package com.bw.petclinic.service;

import com.bw.petclinic.domain.CustomPageImpl;
import com.bw.petclinic.domain.Owner;
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
public class OwnerService extends RestAccessTokenService {

    @Value("${service.url.owner}")
    private String ownerServiceUrl;

    public OwnerService(RestTemplateBuilder builder, OAuth2AuthorizedClientService clientService) {
        super(builder, clientService);
    }

    public Page<Owner> findAll(int pageNumber, int pageSize) {
        String uri = UriComponentsBuilder
                .fromHttpUrl(ownerServiceUrl)
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
                            new ParameterizedTypeReference<CustomPageImpl<Owner>>() {
                            })
                    .getBody();
        } catch (HttpClientErrorException ex) {
            throw new PetClinicServiceException("OwnerService.findAll failed [" + ex.getMessage() + "]");
        }
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
        try {
            return restTemplate
                    .exchange(uri,
                            HttpMethod.GET,
                            createBearerAuthWithAccessToken(""),
                            new ParameterizedTypeReference<CustomPageImpl<Owner>>() {
                            })
                    .getBody();
        } catch (HttpClientErrorException ex) {
            throw new PetClinicServiceException("OwnerService.findByLastName failed [" + ex.getMessage() + "]");
        }
    }

    public Owner findById(int id) {
        log.info("findById id [" + id + "]");
        try {
            return restTemplate
                    .exchange(ownerServiceUrl + "/" + id,
                            HttpMethod.GET,
                            createBearerAuthWithAccessToken(""),
                            Owner.class)
                    .getBody();
        } catch (HttpClientErrorException ex) {
            throw new PetClinicServiceException("OwnerService.findById failed [" + ex.getMessage() + "]");
        }
    }

    public Owner add(Owner owner) {
        log.info("add " + owner);
        try {
            return restTemplate
                    .exchange(ownerServiceUrl,
                            HttpMethod.POST,
                            createBearerAuthWithAccessToken(toJsonString(owner)),
                            Owner.class)
                    .getBody();
        } catch (HttpClientErrorException ex) {
            throw new PetClinicServiceException("OwnerService.add failed [" + ex.getMessage() + "]");
        }
    }

    public void update(Owner owner) {
        log.info("update " + owner);
        try {
            restTemplate
                    .exchange(ownerServiceUrl + "/" + owner.getId(),
                            HttpMethod.PUT,
                            createBearerAuthWithAccessToken(toJsonString(owner)),
                            Owner.class);
        } catch (HttpClientErrorException ex) {
            throw new PetClinicServiceException("OwnerService.update failed [" + ex.getMessage() + "]");
        }
    }

}
