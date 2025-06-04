package com.bw.petclinic.controller;

import com.bw.petclinic.exception.OopsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionController {

    @ExceptionHandler(OopsException.class)
    public String handleOopsException(Exception ex, Model model) {
        log.info("Caught OOPS error");
        model.addAttribute("error", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {
        log.error("Caught internal error", ex);
        model.addAttribute("error", ex.getMessage());
        return "error";
    }

}
