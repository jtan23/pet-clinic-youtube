package com.bw.petclinic.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PetType {

    private Integer id;
    private String name;

    public PetType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
