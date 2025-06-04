package com.bw.petclinic.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OopsController.class)
public class OopsControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testOops() throws Exception {
        mockMvc
                .perform(get("/oops"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("error", "Expected OOPS Error"));
    }
}
