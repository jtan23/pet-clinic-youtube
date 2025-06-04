package com.bw.visit.controller;

import com.bw.visit.domain.Visit;
import com.bw.visit.repository.VisitRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VisitController.class)
public class VisitControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VisitRepository visitRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testFindAll() throws Exception {
        when(visitRepository.findAll()).thenReturn(List.of(new Visit(), new Visit(), new Visit(), new Visit()));
        mockMvc
                .perform(get("/visits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)));
    }

    @Test
    public void testFindByPetId() throws Exception {
        when(visitRepository.findByPetId(7)).thenReturn(List.of(new Visit(), new Visit()));
        mockMvc
                .perform(get("/visits?petId=7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testAdd() throws Exception {
        objectMapper.findAndRegisterModules();
        Visit visit = new Visit(LocalDate.now(), "Test", 8);
        when(visitRepository.save(visit)).thenReturn(visit);
        mockMvc
                .perform(post("/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(visit)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description", is("Test")));
    }

}
