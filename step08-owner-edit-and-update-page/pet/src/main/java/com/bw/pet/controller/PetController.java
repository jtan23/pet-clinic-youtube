package com.bw.pet.controller;

import com.bw.pet.domain.Pet;
import com.bw.pet.repository.PetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class PetController {

    private final PetRepository petRepository;

    public PetController(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    @GetMapping("/pets")
    public List<Pet> findAll(@RequestParam(value = "ownerId", required = false) Integer ownerId) {
        log.info("GET findAll ownerId [" + ownerId + "]");
        if (ownerId == null) {
            return petRepository.findAll();
        } else {
            return petRepository.findByOwnerId(ownerId);
        }
    }

}
