package com.bw.owner.controller;

import com.bw.owner.domain.Owner;
import com.bw.owner.repository.OwnerRepository;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

}
