package com.bw.petclinic.controller;

import com.bw.petclinic.domain.Owner;
import com.bw.petclinic.domain.Pet;
import com.bw.petclinic.domain.PetType;
import com.bw.petclinic.domain.Visit;
import com.bw.petclinic.service.OwnerService;
import com.bw.petclinic.service.PetService;
import com.bw.petclinic.service.VisitService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VisitController.class)
public class VisitControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnerService ownerService;

    @MockBean
    private PetService petService;

    @MockBean
    private VisitService visitService;

    @Test
    public void testNewVisit() throws Exception {
        when(ownerService.findById(1)).thenReturn(
                new Owner("First", "Last", "Address", "City", "Telephone"));
        when(petService.findById(1)).thenReturn(
                new Pet("Test", LocalDate.now(), 1, new PetType("Cat")));
        mockMvc
                .perform(get("/visits/new?ownerId=1&petId=1"))
                .andExpect(status().isOk())
                .andExpect(view().name("visitForm"))
                .andExpect(model().attributeExists("owner", "pet", "visit"));
    }

    @Test
    public void testAddVisit() throws Exception {
        Visit visit = new Visit();
        visit.setId(1);
        when(visitService.add(any(Visit.class))).thenReturn(visit);
        mockMvc
                .perform(post("/visits/new?ownerId=1&petId=1")
                        .param("visitDate", "2024-02-14")
                        .param("description", "Test"))
                .andExpect(status().is3xxRedirection());
    }

}
