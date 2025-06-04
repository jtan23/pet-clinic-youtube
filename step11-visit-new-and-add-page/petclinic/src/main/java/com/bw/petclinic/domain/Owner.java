package com.bw.petclinic.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Owner {

    private Integer id;

    @NotBlank(message = "Owner first name must not be blank")
    private String firstName;

    @NotBlank(message = "Owner last name must not be blank")
    private String lastName;

    @NotBlank(message = "Owner address must not be blank")
    private String address;

    @NotBlank(message = "Owner city must not be blank")
    private String city;

    /**
     * We will leave the digits check for the backend Owner service.
     */
    @NotBlank(message = "Owner telephone must not be blank")
    private String telephone;

    private String petNames;

    private List<Pet> pets;

    public Owner(String firstName, String lastName, String address, String city, String telephone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
        this.telephone = telephone;
    }
}
