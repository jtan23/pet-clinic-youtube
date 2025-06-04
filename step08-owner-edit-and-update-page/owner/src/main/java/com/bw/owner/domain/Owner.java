package com.bw.owner.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "owners")
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name")
    @NotBlank(message = "Owner first name must not be blank")
    private String firstName;

    @Column(name = "last_name")
    @NotBlank(message = "Owner last name must not be blank")
    private String lastName;

    @NotBlank(message = "Owner address must not be blank")
    private String address;

    @NotBlank(message = "Owner city must not be blank")
    private String city;

    @NotBlank(message = "Owner telephone must not be blank")
    @Digits(fraction = 0, integer = 10, message = "Owner telephone must be 10 digits")
    private String telephone;

    public Owner(String firstName, String lastName, String address, String city, String telephone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
        this.telephone = telephone;
    }

}
