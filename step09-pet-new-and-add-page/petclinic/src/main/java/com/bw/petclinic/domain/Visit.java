package com.bw.petclinic.domain;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Visit {

    private Integer id;
    private LocalDate visitDate;
    private String description;

}
