package com.bw.petclinic.controller;

import com.bw.petclinic.service.VetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
public class VetController {

    private final VetService vetService;

    public VetController(VetService vetService) {
        this.vetService = vetService;
    }

    @GetMapping("/vets")
    public String findAll(@RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                          @RequestParam(value = "pageSize", defaultValue = "3") int pageSize,
                          Model model) {
        log.info("GET findAll pageNumber [" + pageNumber + "], pageSize [" + pageSize + "]");
        model.addAttribute("vets", vetService.findAll(pageNumber - 1, pageSize));
        return "vetList";
    }

}
