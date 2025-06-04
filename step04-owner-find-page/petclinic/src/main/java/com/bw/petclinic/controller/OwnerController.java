package com.bw.petclinic.controller;

import com.bw.petclinic.domain.Owner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class OwnerController {

    @GetMapping("/owners/find")
    public String findOwner(Model model) {
        log.info("GET findOwner");
        model.addAttribute("owner", new Owner());
        return "ownerFind";
    }

}
