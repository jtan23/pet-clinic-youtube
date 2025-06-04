package com.bw.visit.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "visits")
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "visit_date")
    @NotNull(message = "Visit date must not be null")
    private LocalDate visitDate;

    @NotBlank(message = "Visit description must not be blank")
    private String description;

    @Column(name = "pet_id")
    @NotNull(message = "Pet id must not be null")
    private Integer petId;

    public Visit(LocalDate visitDate, String description, Integer petId) {
        this.visitDate = visitDate;
        this.description = description;
        this.petId = petId;
    }

}
