package com.bw.petclinic.controller;

import com.bw.petclinic.domain.Owner;
import com.bw.petclinic.domain.Pet;
import com.bw.petclinic.domain.Visit;
import com.bw.petclinic.service.OwnerService;
import com.bw.petclinic.service.PetService;
import com.bw.petclinic.service.VisitService;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.stream.Collectors;

@Controller
@Slf4j
public class OwnerController {

    private final OwnerService ownerService;

    private final PetService petService;

    private final VisitService visitService;

    public OwnerController(OwnerService ownerService, PetService petService, VisitService visitService) {
        this.ownerService = ownerService;
        this.petService = petService;
        this.visitService = visitService;
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

    @GetMapping("/owners/{id}")
    public String showOwner(@PathVariable("id") int id, Model model) {
        log.info("GET showOwner id [" + id + "]");
        Owner owner = ownerService.findById(id);
        owner.setPets(petService.findByOwnerId(id));
        owner.getPets().forEach(pet -> pet.setVisits(visitService.findByPetId(pet.getId())));
        model.addAttribute("owner", owner);
        return "ownerDetails";
    }

    @GetMapping("/owners/new")
    public String newOwner(Model model) {
        log.info("GET newOwner");
        model.addAttribute("owner", new Owner());
        return "ownerForm";
    }

    @PostMapping("/owners/new")
    public String addOwner(@Valid Owner owner, BindingResult bindingResult, Model model) {
        log.info("POST addOwner " + owner);
        if (bindingResult.hasErrors()) {
            return "ownerForm";
        } else {
            Owner savedOwner = ownerService.add(owner);
            return "redirect:/owners/" + savedOwner.getId();
        }
    }

}
