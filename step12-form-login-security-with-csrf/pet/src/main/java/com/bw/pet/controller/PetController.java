package com.bw.pet.controller;

import com.bw.pet.domain.Pet;
import com.bw.pet.domain.PetType;
import com.bw.pet.exception.PetNotFoundException;
import com.bw.pet.repository.PetRepository;
import com.bw.pet.repository.PetTypeRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @GetMapping("/pets/{id}")
    public Pet findById(@PathVariable("id") int id) {
        log.info("GET findById id [" + id + "]");
        Optional<Pet> petOptional = petRepository.findById(id);
        if (petOptional.isPresent()) {
            return petOptional.get();
        } else {
            throw new PetNotFoundException("Pet [" + id + "] not found");
        }
    }

    @PutMapping("/pets/{id}")
    public Pet update(@PathVariable("id") int id, @RequestBody @Valid Pet pet) {
        log.info("PUT update id [" + id + "], " + pet);
        Optional<Pet> petOptional = petRepository.findById(id);
        if (petOptional.isPresent()) {
            Pet savedPet = petOptional.get();
            savedPet.setName(pet.getName());
            savedPet.setBirthDate(pet.getBirthDate());
            savedPet.setPetType(pet.getPetType());
            return petRepository.save(savedPet);
        } else {
            throw new PetNotFoundException("Pet [" + id + "] not found");
        }
    }

    @ExceptionHandler(PetNotFoundException.class)
    public ResponseEntity<String> handleNotFound(PetNotFoundException ex) {
        log.info("Caught not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found [" + ex.getMessage() + "]");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        log.error("Caught internal error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Error [" + ex.getMessage() + "]");
    }

}
