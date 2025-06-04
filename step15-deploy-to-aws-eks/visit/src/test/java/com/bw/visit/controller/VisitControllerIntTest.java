package com.bw.visit.controller;

import com.bw.visit.domain.Visit;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class VisitControllerIntTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private JdbcTemplate jdbcTemplate;

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

    @Test
    public void testAdd() throws Exception {
        objectMapper.findAndRegisterModules();
        Visit visit = new Visit(LocalDate.now(), "Test", 8);
        String content = mockMvc
                .perform(post("/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(visit)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description", is("Test")))
                .andReturn()
                .getResponse().getContentAsString();
        Visit savedVisit = objectMapper.readValue(content, Visit.class);
        assertNotNull(savedVisit.getId());
        jdbcTemplate.update("delete from visits where id = " + savedVisit.getId());
    }

}
