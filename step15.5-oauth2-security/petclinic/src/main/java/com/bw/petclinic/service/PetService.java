package com.bw.petclinic.service;

import com.bw.petclinic.domain.Pet;
import com.bw.petclinic.domain.PetType;
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
public class PetService extends RestAccessTokenService {

    @Value("${service.url.pet}")
    private String petServiceUrl;

    @Value("${service.url.pet-type}")
    private String petTypeServiceUrl;

    public PetService(RestTemplateBuilder builder, OAuth2AuthorizedClientService clientService) {
        super(builder, clientService);
    }

    public List<Pet> findByOwnerId(int ownerId) {
        String uri = UriComponentsBuilder
                .fromHttpUrl(petServiceUrl)
                .queryParam("ownerId", ownerId)
                .build().toUriString();
        log.info("findByOwnerId uri [" + uri + "]");
        try {
            return restTemplate
                    .exchange(uri,
                            HttpMethod.GET,
                            createBearerAuthWithAccessToken(""),
                            new ParameterizedTypeReference<List<Pet>>() {
                            })
                    .getBody();
        } catch (HttpClientErrorException ex) {
            throw new PetClinicServiceException("PetService.findByOwnerId failed [" + ex.getMessage() + "]");
        }
    }

    public List<PetType> findAllPetTypes() {
        log.info("findAllPetTypes");
        try {
            return restTemplate
                    .exchange(petTypeServiceUrl,
                            HttpMethod.GET,
                            createBearerAuthWithAccessToken(""),
                            new ParameterizedTypeReference<List<PetType>>() {
                            })
                    .getBody();
        } catch (HttpClientErrorException ex) {
            throw new PetClinicServiceException("PetService.findAllPetTypes failed [" + ex.getMessage() + "]");
        }
    }

    public Pet add(Pet pet) {
        log.info("add " + pet);
        try {
            return restTemplate
                    .exchange(petServiceUrl,
                            HttpMethod.POST,
                            createBearerAuthWithAccessToken(toJsonString(pet)),
                            Pet.class)
                    .getBody();
        } catch (HttpClientErrorException ex) {
            throw new PetClinicServiceException("PetService.add failed [" + ex.getMessage() + "]");
        }
    }

    public Pet findById(int id) {
        log.info("findById id [" + id + "]");
        try {
            return restTemplate
                    .exchange(petServiceUrl + "/" + id,
                            HttpMethod.GET,
                            createBearerAuthWithAccessToken(""),
                            Pet.class)
                    .getBody();
        } catch (HttpClientErrorException ex) {
            throw new PetClinicServiceException("PetService.findById failed [" + ex.getMessage() + "]");
        }
    }

    public void update(Pet pet) {
        log.info("updatePet " + pet);
        try {
            restTemplate
                    .exchange(petServiceUrl + "/" + pet.getId(),
                            HttpMethod.PUT,
                            createBearerAuthWithAccessToken(toJsonString(pet)),
                            Pet.class);
        } catch (HttpClientErrorException ex) {
            throw new PetClinicServiceException("PetService.update failed [" + ex.getMessage() + "]");
        }
    }

}
