package com.bw.visit.controller;

import com.bw.visit.domain.Visit;
import com.bw.visit.repository.VisitRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class VisitController {

    private final VisitRepository visitRepository;

    public VisitController(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    @GetMapping("/visits")
    public List<Visit> findAll(@RequestParam(value = "petId", required = false) Integer petId) {
        log.info("GET finaAll petId [" + petId + "]");
        if (petId == null) {
            return visitRepository.findAll();
        } else {
            return visitRepository.findByPetId(petId);
        }
    }

    @PostMapping("/visits")
    @ResponseStatus(HttpStatus.CREATED)
    public Visit add(@RequestBody @Valid Visit visit) {
        log.info("POST add " + visit);
        return visitRepository.save(visit);
    }

}
