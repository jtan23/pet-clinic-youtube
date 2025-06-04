package com.bw.pet.controller;

import com.bw.pet.domain.Pet;
import com.bw.pet.domain.PetType;
import com.bw.pet.repository.PetRepository;
import com.bw.pet.repository.PetTypeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(PetController.class)
public class PetControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PetRepository petRepository;

    @MockBean
    private PetTypeRepository petTypeRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testFindAllDefault() throws Exception {
        when(petRepository.findAll()).thenReturn(List.of(new Pet(), new Pet(), new Pet()));
        mockMvc
                .perform(get("/pets").with(user("user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    public void testFindByOwnerId() throws Exception {
        when(petRepository.findByOwnerId(1)).thenReturn(List.of(new Pet(), new Pet()));
        mockMvc
                .perform(get("/pets?ownerId=1").with(user("user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testFindAllPetTypes() throws Exception {
        when(petTypeRepository.findAll()).thenReturn(List.of(new PetType(), new PetType(), new PetType()));
        mockMvc
                .perform(get("/pet-types").with(user("user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    public void testAdd() throws Exception {
        objectMapper.findAndRegisterModules();
        Pet pet = new Pet("Test", LocalDate.now(), 1, new PetType(1, "Cat"));
        when(petRepository.save(pet)).thenReturn(pet);
        mockMvc
                .perform(post("/pets")
                        .with(user("admin").roles("USER", "ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(pet)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Test")))
                .andExpect(jsonPath("$.ownerId", is(1)));
    }

    @Test
    public void testFindById() throws Exception {
        Pet pet = new Pet("Test", LocalDate.now(), 1, new PetType(1, "Cat"));
        pet.setId(1);
        when(petRepository.findById(1)).thenReturn(Optional.of(pet));
        mockMvc
                .perform(get("/pets/1").with(user("user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    public void testFindByIdNotFound() throws Exception {
        mockMvc
                .perform(get("/pets/10000").with(user("user")))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Not Found [Pet [10000] not found]"));
    }

    @Test
    public void testUpdate() throws Exception {
        objectMapper.findAndRegisterModules();
        Pet pet = new Pet("Jason", LocalDate.of(2000, 9, 7), 1, new PetType(1, "Cat"));
        pet.setId(1);
        when(petRepository.findById(1)).thenReturn(Optional.of(pet));
        when(petRepository.save(pet)).thenReturn(pet);
        mockMvc
                .perform(put("/pets/1")
                        .with(user("admin").roles("USER", "ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(pet)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Jason")));
    }

    @Test
    public void testUpdateNotFound() throws Exception {
        objectMapper.findAndRegisterModules();
        Pet pet = new Pet("Jason", LocalDate.of(2000, 9, 7), 1, new PetType(1, "Cat"));
        pet.setId(10000);
        mockMvc
                .perform(put("/pets/10000")
                        .with(user("admin").roles("USER", "ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(pet)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Not Found [Pet [10000] not found]"));
    }

}
