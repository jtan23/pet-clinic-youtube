package com.bw.petclinic.service;

import com.bw.petclinic.domain.Pet;
import com.bw.petclinic.domain.PetType;
import com.bw.petclinic.exception.PetClinicServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
@Slf4j
public class PetService {

    @Value("${service.url.pet}")
    private String petServiceUrl;

    @Value("${service.url.pet-type}")
    private String petTypeServiceUrl;

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

    public List<PetType> findAllPetTypes() {
        log.info("findAllPetTypes");
        return restTemplate
                .exchange(petTypeServiceUrl, HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<PetType>>() {})
                .getBody();
    }

    public Pet add(Pet pet) {
        log.info("add " + pet);
        try {
            return restTemplate.postForObject(petServiceUrl, pet, Pet.class);
        } catch (HttpClientErrorException ex) {
            throw new PetClinicServiceException("PetService.add failed [" + ex.getMessage() + "]");
        }
    }

    public Pet findById(int id) {
        log.info("findById id [" + id + "]");
        try {
            return restTemplate.getForObject(petServiceUrl + "/" + id, Pet.class);
        } catch (HttpClientErrorException ex) {
            throw new PetClinicServiceException("PetService.findById failed [" + ex.getMessage() + "]");
        }
    }

    public void update(Pet pet) {
        log.info("updatePet " + pet);
        try {
            restTemplate.put(petServiceUrl + "/" + pet.getId(), pet);
        } catch (HttpClientErrorException ex) {
            throw new PetClinicServiceException("PetService.update failed [" + ex.getMessage() + "]");
        }
    }

}
