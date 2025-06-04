package com.bw.petclinic.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class VisitControllerIntTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testNewVisit() throws Exception {
        mockMvc
                .perform(get("/visits/new?ownerId=1&petId=1").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("visitForm"))
                .andExpect(model().attributeExists("owner", "pet", "visit"));
    }

    @Test
    public void testAddVisit() throws Exception {
        ModelAndView mav = mockMvc
                .perform(post("/visits/new?ownerId=1&petId=1")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .param("visitDate", "2024-02-14")
                        .param("description", "Test"))
                .andExpect(status().is3xxRedirection())
                .andReturn().getModelAndView();
        assertNotNull(mav);
        String viewName = mav.getViewName();
        assertNotNull(viewName);
        assertTrue(viewName.startsWith("redirect:/owners/1?visitId="));
        String visitId = viewName.substring(viewName.lastIndexOf("=") + 1);
        jdbcTemplate.update("delete from visits where id = " + visitId);
    }

}
