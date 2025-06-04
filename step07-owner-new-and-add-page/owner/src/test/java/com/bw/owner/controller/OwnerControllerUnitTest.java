package com.bw.owner.controller;

import com.bw.owner.domain.Owner;
import com.bw.owner.repository.OwnerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(OwnerController.class)
public class OwnerControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnerRepository ownerRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testFindAllDefault() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Owner> ownerPage = new PageImpl<>(
                List.of(new Owner(), new Owner(), new Owner(), new Owner(), new Owner()),
                pageable, 10);
        when(ownerRepository.findAll(pageable)).thenReturn(ownerPage);
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
        Pageable pageable = PageRequest.of(0, 5);
        Page<Owner> ownerPage = new PageImpl<>(
                List.of(new Owner(), new Owner()),
                pageable, 2);
        when(ownerRepository.findByLastName(pageable, "Davis")).thenReturn(ownerPage);
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
        Owner owner = new Owner();
        owner.setId(1);
        when(ownerRepository.findById(1)).thenReturn(Optional.of(owner));
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

    @Test
    public void testAdd() throws Exception {
        Owner owner = new Owner("First", "Last", "Address", "City", "0123456789");
        when(ownerRepository.save(owner)).thenReturn(owner);
        mockMvc
                .perform(post("/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(owner)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is("First")))
                .andExpect(jsonPath("$.lastName", is("Last")))
                .andExpect(jsonPath("$.address", is("Address")))
                .andExpect(jsonPath("$.city", is("City")))
                .andExpect(jsonPath("$.telephone", is("0123456789")));
    }

    @Test
    public void testAddInvalidCity() throws Exception {
        Owner owner = new Owner("First", "Last", "Address", "", "0123456789");
        mockMvc
                .perform(post("/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(owner)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request [Owner city must not be blank]"));
    }

    @Test
    public void testAddInvalidTelephone() throws Exception {
        Owner owner = new Owner("First", "Last", "Address", "City", "Telephone");
        mockMvc
                .perform(post("/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(owner)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request [Owner telephone must be 10 digits]"));
    }

}
