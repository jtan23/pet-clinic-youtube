package com.bw.pet.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "pets")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    private String name;

    @Column(name = "birth_date")
    @NotNull
    private LocalDate birthDate;

    @Column(name = "owner_id")
    private Integer ownerId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pet_type_id")
    private PetType petType;

}
