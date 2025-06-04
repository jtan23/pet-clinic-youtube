package com.bw.petclinic.controller;

import com.bw.petclinic.exception.OopsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class OopsController {

    @GetMapping("/oops")
    public String oops() {
        log.info("GET oops");
        throw new OopsException("Expected OOPS Error");
    }

}
