package com.bw.pet.controller;

import com.bw.pet.domain.Pet;
import com.bw.pet.domain.PetType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PetControllerIntTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testFindAllDefault() throws Exception {
        mockMvc
                .perform(get("/pets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(13)));
    }

    @Test
    public void testFindByOwnerId() throws Exception {
        mockMvc
                .perform(get("/pets?ownerId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void testFindAllPetTypes() throws Exception {
        mockMvc
                .perform(get("/pet-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)));
    }

    @Test
    public void testAdd() throws Exception {
        objectMapper.findAndRegisterModules();
        Pet pet = new Pet("Test", LocalDate.now(), 1, new PetType(1, "Cat"));
        String content = mockMvc
                .perform(post("/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(pet)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Test")))
                .andExpect(jsonPath("$.ownerId", is(1)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        Pet savedPet = objectMapper.readValue(content, Pet.class);
        assertNotNull(savedPet.getId());
        jdbcTemplate.update("delete from pets where id = " + savedPet.getId());
    }

    @Test
    public void testFindById() throws Exception {
        mockMvc
                .perform(get("/pets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    public void testFindByIdNotFound() throws Exception {
        mockMvc
                .perform(get("/pets/10000"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Not Found [Pet [10000] not found]"));
    }

    @Test
    public void testUpdate() throws Exception {
        objectMapper.findAndRegisterModules();
        Pet pet = new Pet("Jason", LocalDate.of(2000, 9, 7), 1, new PetType(1, "Cat"));
        pet.setId(1);
        mockMvc
                .perform(put("/pets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(pet)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Jason")));
        jdbcTemplate.update("update pets set name = 'Leo' where id = 1");
    }

    @Test
    public void testUpdateNotFound() throws Exception {
        objectMapper.findAndRegisterModules();
        Pet pet = new Pet("Jason", LocalDate.of(2000, 9, 7), 1, new PetType(1, "Cat"));
        pet.setId(10000);
        mockMvc
                .perform(put("/pets/10000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(pet)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Not Found [Pet [10000] not found]"));
    }

}
