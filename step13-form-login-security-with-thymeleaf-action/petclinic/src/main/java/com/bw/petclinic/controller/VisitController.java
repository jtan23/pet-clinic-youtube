package com.bw.petclinic.controller;

import com.bw.petclinic.domain.Pet;
import com.bw.petclinic.domain.Visit;
import com.bw.petclinic.service.OwnerService;
import com.bw.petclinic.service.PetService;
import com.bw.petclinic.service.VisitService;
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
public class VisitController {

    private final OwnerService ownerService;

    private final PetService petService;

    private final VisitService visitService;

    public VisitController(OwnerService ownerService, PetService petService, VisitService visitService) {
        this.ownerService = ownerService;
        this.petService = petService;
        this.visitService = visitService;
    }

    @GetMapping("/visits/new")
    public String newVisit(@RequestParam("ownerId") int ownerId,
                           @RequestParam("petId") int petId, Model model) {
        log.info("GET newVisit ownerId [" + ownerId + "], petId [" + petId + "]");
        model.addAttribute("action", "/visits/new?ownerId=" + ownerId + "&petId=" + petId);
        model.addAttribute("owner", ownerService.findById(ownerId));
        Pet pet = petService.findById(petId);
        pet.setVisits(visitService.findByPetId(petId));
        model.addAttribute("pet", pet);
        model.addAttribute("visit", new Visit());
        return "visitForm";
    }

    @PostMapping("/visits/new")
    public String addVisit(@RequestParam("ownerId") int ownerId,
                           @RequestParam("petId") int petId,
                           @Valid Visit visit, BindingResult bindingResult,
                           Model model) {
        log.info("POST addVisit ownerId [" + ownerId + "], petId [" + petId + "], " + visit);
        if (bindingResult.hasErrors()) {
            model.addAttribute("action", "/visits/new?ownerId=" + ownerId + "&petId=" + petId);
            model.addAttribute("owner", ownerService.findById(ownerId));
            Pet pet = petService.findById(petId);
            pet.setVisits(visitService.findByPetId(petId));
            model.addAttribute("pet", pet);
            return "visitForm";
        } else {
            visit.setPetId(petId);
            Visit savedVisit = visitService.add(visit);
            return "redirect:/owners/" + ownerId + "?visitId=" + savedVisit.getId();
        }
    }

}
