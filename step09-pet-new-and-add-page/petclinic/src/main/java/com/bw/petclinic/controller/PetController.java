package com.bw.petclinic.controller;

import com.bw.petclinic.domain.Pet;
import com.bw.petclinic.service.OwnerService;
import com.bw.petclinic.service.PetService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
public class PetController {

    private final OwnerService ownerService;

    private final PetService petService;

    public PetController(OwnerService ownerService, PetService petService) {
        this.ownerService = ownerService;
        this.petService = petService;
    }

    @GetMapping("/pets/new")
    public String newPet(@RequestParam("ownerId") int ownerId, Model model) {
        log.info("GET newPet ownerId [" + ownerId + "]");
        model.addAttribute("owner", ownerService.findById(ownerId));
        model.addAttribute("pet", new Pet());
        model.addAttribute("types", petService.findAllPetTypes());
        return "petForm";
    }

    @PostMapping("/pets/new")
    public String addPet(@RequestParam("ownerId") int ownerId,
                         @Valid Pet pet, BindingResult bindingResult,
                         Model model) {
        log.info("POST addPet ownerId [" + ownerId + "], " + pet);
        if (bindingResult.hasErrors()) {
            model.addAttribute("owner", ownerService.findById(ownerId));
            model.addAttribute("types", petService.findAllPetTypes());
            return "petForm";
        }
        pet.setOwnerId(ownerId);
        Pet savedPet = petService.add(pet);
        return "redirect:/owners/" + ownerId + "?petId=" + savedPet.getId();
    }

}
