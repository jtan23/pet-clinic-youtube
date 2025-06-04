package com.bw.petclinic.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class AccessDeniedController {

    @GetMapping("/access-denied")
    public String accessDenied() {
        log.info("GET accessDenied");
        return "access-denied";
    }
}
