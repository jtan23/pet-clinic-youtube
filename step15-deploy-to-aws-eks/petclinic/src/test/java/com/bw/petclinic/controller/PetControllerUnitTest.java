package com.bw.petclinic.controller;

import com.bw.petclinic.domain.Owner;
import com.bw.petclinic.domain.Pet;
import com.bw.petclinic.domain.PetType;
import com.bw.petclinic.service.OwnerService;
import com.bw.petclinic.service.PetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PetController.class)
public class PetControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnerService ownerService;

    @MockBean
    private PetService petService;

    @Test
    public void testNewPet() throws Exception {
        Owner owner = new Owner("First", "Last", "Address", "City", "Telephone");
        owner.setId(1);
        when(ownerService.findById(1)).thenReturn(owner);
        when(petService.findAllPetTypes()).thenReturn(
                List.of(new PetType("Cat"), new PetType("Dog"), new PetType("Bird")));
        ModelAndView mav = mockMvc
                .perform(get("/pets/new?ownerId=1")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("petForm"))
                .andExpect(model().attribute("pet", new Pet()))
                .andReturn()
                .getModelAndView();
        assertNotNull(mav);
        assertEquals(1, ((Owner) mav.getModel().get("owner")).getId());
        @SuppressWarnings("unchecked")
        List<PetType> petTypes = (List<PetType>) mav.getModel().get("types");
        assertEquals(3, petTypes.size());
    }

    @Test
    public void testAddPet() throws Exception {
        Pet pet = new Pet("Test", LocalDate.now(), 1, new PetType("Cat"));
        pet.setId(1);
        when(petService.add(any(Pet.class))).thenReturn(pet);
        mockMvc
                .perform(post("/pets/new?ownerId=1")
                        .param("name", "Test")
                        .param("birthDate", "2024-02-14")
                        .param("petType", "Cat")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/owners/1?petId=1"));
    }

    @Test
    public void testEditPet() throws Exception {
        Owner owner = new Owner("First", "Last", "Address", "City", "Telephone");
        owner.setId(1);
        Pet pet = new Pet("Test", LocalDate.now(), 1, new PetType("Cat"));
        pet.setId(1);
        List<PetType> petTypes = List.of(new PetType("Cat"), new PetType("Dog"), new PetType("Bird"));
        when(petService.findById(1)).thenReturn(pet);
        when(ownerService.findById(1)).thenReturn(owner);
        when(petService.findAllPetTypes()).thenReturn(petTypes);
        mockMvc
                .perform(get("/pets/edit?ownerId=1&petId=1")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("petForm"))
                .andExpect(model().attribute("owner", owner))
                .andExpect(model().attribute("pet", pet))
                .andExpect(model().attribute("types", petTypes));
    }

    @Test
    public void testUpdatePet() throws Exception {
        mockMvc
                .perform(post("/pets/edit?ownerId=1&petId=1")
                        .param("name", "Test")
                        .param("birthDate", "2000-09-07")
                        .param("petType", "Cat")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/owners/1"));
    }

}
