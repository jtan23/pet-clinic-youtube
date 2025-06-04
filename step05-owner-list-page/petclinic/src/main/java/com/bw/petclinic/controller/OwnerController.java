package com.bw.petclinic.controller;

import com.bw.petclinic.domain.Owner;
import com.bw.petclinic.domain.Pet;
import com.bw.petclinic.service.OwnerService;
import com.bw.petclinic.service.PetService;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.stream.Collectors;

@Controller
@Slf4j
public class OwnerController {

    private final OwnerService ownerService;

    private final PetService petService;

    public OwnerController(OwnerService ownerService, PetService petService) {
        this.ownerService = ownerService;
        this.petService = petService;
    }

    @GetMapping("/owners/find")
    public String findOwner(Model model) {
        log.info("GET findOwner");
        model.addAttribute("owner", new Owner());
        return "ownerFind";
    }

    @GetMapping("/owners")
    public String listOwner(@RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                            @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
                            Owner owner, BindingResult bindingResult, Model model) {
        log.info("GET listOwner pageNumber [" + pageNumber + "], pageSize [" + pageSize + "], " + owner);
        Page<Owner> ownerPage = null;
        if (StringUtils.isBlank(owner.getLastName())) {
            ownerPage = ownerService.findAll(pageNumber - 1, pageSize);
        } else {
            ownerPage = ownerService.findByLastName(pageNumber - 1, pageSize, owner.getLastName());
            if (ownerPage.getContent().isEmpty()) {
                bindingResult.addError(new ObjectError("lastName",
                        "Owner not found for lastName [" + owner.getLastName() + "]"));
                return "ownerFind";
            }
        }
        ownerPage.getContent().forEach(owner1 -> owner1
                .setPetNames(petService
                        .findByOwnerId(owner1.getId())
                        .stream()
                        .map(Pet::getName)
                        .collect(Collectors.joining(","))));
        model.addAttribute("owners", ownerPage);
        return "ownerList";
    }

}
