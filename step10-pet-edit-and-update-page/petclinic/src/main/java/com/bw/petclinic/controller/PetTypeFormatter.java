package com.bw.petclinic.controller;

import com.bw.petclinic.domain.PetType;
import com.bw.petclinic.service.PetService;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.List;
import java.util.Locale;

@Component
public class PetTypeFormatter implements Formatter<PetType> {

    private final PetService petService;

    private List<PetType> petTypes;

    public PetTypeFormatter(PetService petService) {
        this.petService = petService;
    }

    @Override
    public PetType parse(String text, Locale locale) throws ParseException {
        if (petTypes == null) {
            petTypes = petService.findAllPetTypes();
        }
        for (PetType petType : petTypes) {
            if (petType.getName().equals(text)) {
                return petType;
            }
        }
        return null;
    }

    @Override
    public String print(PetType object, Locale locale) {
        return object.getName();
    }

}
