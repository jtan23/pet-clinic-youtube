package com.bw.pet.controller;

import com.bw.pet.domain.Pet;
import com.bw.pet.domain.PetType;
import com.bw.pet.repository.PetRepository;
import com.bw.pet.repository.PetTypeRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class PetController {

    private final PetRepository petRepository;

    private final PetTypeRepository petTypeRepository;

    public PetController(PetRepository petRepository, PetTypeRepository petTypeRepository) {
        this.petRepository = petRepository;
        this.petTypeRepository = petTypeRepository;
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

    @GetMapping("/pet-types")
    public List<PetType> findAllPetTypes() {
        log.info("GET findAllPetTypes");
        return petTypeRepository.findAll();
    }

    @PostMapping("/pets")
    @ResponseStatus(HttpStatus.CREATED)
    public Pet add(@RequestBody @Valid Pet pet) {
        log.info("POST add " + pet);
        return petRepository.save(pet);
    }

}
