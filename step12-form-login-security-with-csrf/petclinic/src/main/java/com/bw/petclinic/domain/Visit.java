package com.bw.petclinic.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Visit {

    private Integer id;

    @NotNull(message = "Visit date must not be null")
    private LocalDate visitDate;

    @NotBlank(message = "Visit description must not be blank")
    private String description;

    private Integer petId;

}
