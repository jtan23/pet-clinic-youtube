package com.bw.owner.controller;

import com.bw.owner.OwnerApplication;
import com.bw.owner.domain.Owner;
import com.bw.owner.exception.OwnerNotFoundException;
import com.bw.owner.repository.OwnerRepository;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Optional;

@RestController
@Slf4j
public class OwnerController {

    private final OwnerRepository ownerRepository;

    public OwnerController(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @GetMapping("/owners")
    public Page<Owner> findAll(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                               @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
                               @RequestParam(value = "lastName", required = false) String lastName) {
        log.info("GET findAll pageNumber [" + pageNumber + "], pageSize [" + pageSize + "], lastName [" + lastName + "]");
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        if (StringUtils.isBlank(lastName)) {
            return ownerRepository.findAll(pageable);
        } else {
            return ownerRepository.findByLastName(pageable, lastName);
        }
    }

    @GetMapping("/owners/{id}")
    public Owner findById(@PathVariable("id") int id) {
        log.info("GET findById id [" + id + "]");
        Optional<Owner> ownerOptional = ownerRepository.findById(id);
        if (ownerOptional.isPresent()) {
            return ownerOptional.get();
        }
        throw new OwnerNotFoundException("Owner [" + id + "] not found");
    }

    @ExceptionHandler(OwnerNotFoundException.class)
    public ResponseEntity<String> handleNotFound(OwnerNotFoundException ex) {
        log.info("Caught owner not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found [" + ex.getMessage() + "]");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.info("Caught type mismatch");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request [" + ex.getMessage() + "]");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        log.error("Caught internal error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Error [" + ex.getMessage() + "]");
    }

}
