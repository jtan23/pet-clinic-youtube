package com.bw.petclinic.domain;

import lombok.Data;

import java.util.List;

@Data
public class Vet {

    private Integer id;
    private String firstName;
    private String lastName;
    private List<Specialty> specialties;

}
