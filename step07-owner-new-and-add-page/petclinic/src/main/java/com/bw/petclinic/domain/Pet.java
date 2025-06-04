package com.bw.petclinic.domain;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class Pet {

    private Integer id;
    private String name;
    private LocalDate birthDate;
    private Integer ownerId;
    private PetType petType;
    private List<Visit> visits;

}
