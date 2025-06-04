package com.bw.petclinic.domain;

import lombok.Data;

@Data
public class Specialty {

    private Integer id;
    private String name;

    @Override
    public String toString() {
        return name;
    }
}
