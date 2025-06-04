package com.bw.visit.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class VisitControllerIntTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testFindAll() throws Exception {
        mockMvc
                .perform(get("/visits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)));
    }

    @Test
    public void testFindByPetId() throws Exception {
        mockMvc
                .perform(get("/visits?petId=7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

}
