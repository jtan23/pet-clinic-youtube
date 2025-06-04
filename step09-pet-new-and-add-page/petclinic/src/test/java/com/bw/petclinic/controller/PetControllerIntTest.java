package com.bw.petclinic.controller;

import com.bw.petclinic.domain.Owner;
import com.bw.petclinic.domain.Pet;
import com.bw.petclinic.domain.PetType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PetControllerIntTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testNewPet() throws Exception {
        ModelAndView mav = mockMvc
                .perform(get("/pets/new?ownerId=1"))
                .andExpect(status().isOk())
                .andExpect(view().name("petForm"))
                .andExpect(model().attribute("pet", new Pet()))
                .andReturn()
                .getModelAndView();
        assertNotNull(mav);
        Owner owner = (Owner) mav.getModel().get("owner");
        assertEquals(1, owner.getId());
        @SuppressWarnings("unchecked")
        List<PetType> petTypes = (List<PetType>) mav.getModel().get("types");
        assertEquals(6, petTypes.size());
    }

    @Test
    public void testAddPet() throws Exception {
        ModelAndView mav = mockMvc
                .perform(post("/pets/new?ownerId=1")
                        .param("name", "Test")
                        .param("birthDate", "2024-02-20")
                        .param("petType", "Cat"))
                .andExpect(status().is3xxRedirection())
                .andReturn()
                .getModelAndView();
        assertNotNull(mav);
        String viewName = mav.getViewName();
        assertNotNull(viewName);
        assertTrue(viewName.startsWith("redirect:/owners/1?petId="));
        String petId = viewName.substring(viewName.lastIndexOf("=") + 1);
        jdbcTemplate.update("delete from pets where id = " + petId);
    }

}
