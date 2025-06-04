package com.bw.pet.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "pet_types")
public class PetType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

}
