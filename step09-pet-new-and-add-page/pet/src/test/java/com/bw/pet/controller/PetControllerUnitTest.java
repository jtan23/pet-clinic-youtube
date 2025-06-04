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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                .perform(get("/pets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    public void testFindByOwnerId() throws Exception {
        when(petRepository.findByOwnerId(1)).thenReturn(List.of(new Pet(), new Pet()));
        mockMvc
                .perform(get("/pets?ownerId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testFindAllPetTypes() throws Exception {
        when(petTypeRepository.findAll()).thenReturn(List.of(new PetType(), new PetType(), new PetType()));
        mockMvc
                .perform(get("/pet-types"))
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(pet)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Test")))
                .andExpect(jsonPath("$.ownerId", is(1)));
    }

}
