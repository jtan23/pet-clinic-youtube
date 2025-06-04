package com.bw.owner.controller;

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
public class OwnerControllerIntTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testFindAllDefault() throws Exception {
        mockMvc
                .perform(get("/owners"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.totalElements", is(10)))
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.size", is(5)));
    }

    @Test
    public void testFindAllDavis() throws Exception {
        mockMvc
                .perform(get("/owners?lastName=Davis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.size", is(5)));
    }

    @Test
    public void testFindById() throws Exception {
        mockMvc
                .perform(get("/owners/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    public void testFindByIdNotFound() throws Exception {
        mockMvc
                .perform(get("/owners/10000"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Not found [Owner [10000] not found]"));
    }

    @Test
    public void testFindByIdTypeMismatch() throws Exception {
        mockMvc
                .perform(get("/owners/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request [Failed to convert value of type 'java.lang.String' to required type 'int'; For input string: \"abc\"]"));
    }

}
