package com.bw.petclinic.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class Pet {

    private Integer id;

    @NotBlank(message = "Pet name must not be blank")
    private String name;

    @NotNull(message = "Pet birth date must not be null")
    private LocalDate birthDate;

    private Integer ownerId;

    @NotNull(message = "Pet type must not be null")
    private PetType petType;

    private List<Visit> visits;

    public Pet(String name, LocalDate birthDate, Integer ownerId, PetType petType) {
        this.name = name;
        this.birthDate = birthDate;
        this.ownerId = ownerId;
        this.petType = petType;
    }
}
