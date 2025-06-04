package com.bw.petclinic.controller;

import com.bw.petclinic.domain.Vet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class VetControllerIntTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testFindAllDefault() throws Exception {
        ModelAndView mav = mockMvc
                .perform(get("/vets"))
                .andExpect(status().isOk())
                .andExpect(view().name("vetList"))
                .andExpect(model().attributeExists("vets"))
                .andReturn().getModelAndView();
        assertNotNull(mav);
        @SuppressWarnings("unchecked")
        Page<Vet> vetPage = (Page<Vet>) mav.getModel().get("vets");
        assertEquals(3, vetPage.getContent().size());
        assertEquals(6, vetPage.getTotalElements());
        assertEquals(0, vetPage.getNumber());
        assertEquals(3, vetPage.getSize());
    }

    @Test
    public void testFindAllCustom() throws Exception {
        ModelAndView mav = mockMvc
                .perform(get("/vets?pageNumber=2&pageSize=2"))
                .andExpect(status().isOk())
                .andExpect(view().name("vetList"))
                .andExpect(model().attributeExists("vets"))
                .andReturn().getModelAndView();
        assertNotNull(mav);
        @SuppressWarnings("unchecked")
        Page<Vet> vetPage = (Page<Vet>) mav.getModel().get("vets");
        assertEquals(2, vetPage.getContent().size());
        assertEquals(6, vetPage.getTotalElements());
        assertEquals(1, vetPage.getNumber());
        assertEquals(2, vetPage.getSize());
    }

}
