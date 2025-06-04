package com.bw.vet.controller;

import com.bw.vet.domain.Vet;
import com.bw.vet.repository.VetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestController
@Slf4j
public class VetController {

    private final VetRepository vetRepository;

    public VetController(VetRepository vetRepository) {
        this.vetRepository = vetRepository;
    }

    @GetMapping("/vets")
    public Page<Vet> findAll(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                             @RequestParam(value = "pageSize", defaultValue = "3") int pageSize) {
        log.info("GET findAll pageNumber [" + pageNumber + "], pageSize [" + pageSize + "]");
        return vetRepository.findAll(PageRequest.of(pageNumber, pageSize));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.info("Caught type mismatch exception");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request [" + ex.getMessage() + "]");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        log.error("Caught internal error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error [" + ex.getMessage() + "]");
    }

}
