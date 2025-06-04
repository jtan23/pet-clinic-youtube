package com.bw.vet.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class VetControllerIntTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testFindAllDefault() throws Exception {
        mockMvc
                .perform(get("/vets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.totalElements", is(6)))
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.size", is(3)));
    }

    @Test
    public void testFindAllCustom() throws Exception {
        mockMvc
                .perform(get("/vets?pageNumber=1&pageSize=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(6)))
                .andExpect(jsonPath("$.number", is(1)))
                .andExpect(jsonPath("$.size", is(2)));
    }

    @Test
    public void testFindAllTypeMismatch() throws Exception {
        mockMvc
                .perform(get("/vets?pageNumber=1&pageSize=abc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request [Failed to convert value of type 'java.lang.String' to required type 'int'; For input string: \"abc\"]"));
    }
}
