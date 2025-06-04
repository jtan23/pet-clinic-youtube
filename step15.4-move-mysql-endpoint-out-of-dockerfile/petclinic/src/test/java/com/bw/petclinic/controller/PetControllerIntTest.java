package com.bw.petclinic.controller;

import com.bw.petclinic.domain.Owner;
import com.bw.petclinic.domain.Pet;
import com.bw.petclinic.domain.PetType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
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
                .perform(get("/pets/new?ownerId=1")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
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
                        .param("petType", "Cat")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
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

    @Test
    public void testEditPet() throws Exception {
        ModelAndView mav = mockMvc
                .perform(get("/pets/edit?ownerId=1&petId=1")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("petForm"))
                .andReturn()
                .getModelAndView();
        assertNotNull(mav);
        assertEquals(1, ((Owner) mav.getModel().get("owner")).getId());
        assertEquals(1, ((Pet) mav.getModel().get("pet")).getId());
        @SuppressWarnings("unchecked")
        List<PetType> petTypes = (List<PetType>) mav.getModel().get("types");
        assertEquals(6, petTypes.size());
    }

    @Test
    public void testUpdatePet() throws Exception {
        mockMvc
                .perform(post("/pets/edit?ownerId=1&petId=1")
                        .param("name", "Test")
                        .param("birthDate", "2000-09-07")
                        .param("petType", "Cat")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/owners/1"));
        assertEquals("Test", jdbcTemplate.queryForObject("select name from pets where id = 1", String.class));
        jdbcTemplate.update("update pets set name = 'Leo' where id = 1");
    }

}
