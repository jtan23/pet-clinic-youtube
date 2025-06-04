package com.bw.vet.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "specialties")
public class Specialty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

}
