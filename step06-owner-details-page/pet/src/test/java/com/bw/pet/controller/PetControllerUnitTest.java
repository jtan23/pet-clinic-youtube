package com.bw.pet.controller;

import com.bw.pet.domain.Pet;
import com.bw.pet.repository.PetRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PetController.class)
public class PetControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PetRepository petRepository;

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

}
