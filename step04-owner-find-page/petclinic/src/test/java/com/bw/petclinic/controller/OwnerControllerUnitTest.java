package com.bw.petclinic.controller;

import com.bw.petclinic.domain.Owner;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OwnerController.class)
public class OwnerControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testFindOwner() throws Exception {
        mockMvc
                .perform(get("/owners/find"))
                .andExpect(status().isOk())
                .andExpect(view().name("ownerFind"))
                .andExpect(model().attribute("owner", new Owner()));
    }

}
