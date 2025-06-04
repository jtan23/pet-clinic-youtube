package com.bw.owner.controller;

import com.bw.owner.domain.Owner;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class OwnerControllerIntTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testFindAllDefault() throws Exception {
        mockMvc
                .perform(get("/owners").with(user("user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.totalElements", is(10)))
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.size", is(5)));
    }

    @Test
    public void testFindAllDavis() throws Exception {
        mockMvc
                .perform(get("/owners?lastName=Davis").with(user("user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.size", is(5)));
    }

    @Test
    public void testFindById() throws Exception {
        mockMvc
                .perform(get("/owners/1").with(user("user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    public void testFindByIdNotFound() throws Exception {
        mockMvc
                .perform(get("/owners/10000").with(user("user")))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Not found [Owner [10000] not found]"));
    }

    @Test
    public void testFindByIdTypeMismatch() throws Exception {
        mockMvc
                .perform(get("/owners/abc").with(user("user")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request [Failed to convert value of type 'java.lang.String' to required type 'int'; For input string: \"abc\"]"));
    }

    @Test
    public void testAdd() throws Exception {
        Owner owner = new Owner("First", "Last", "Address", "City", "0123456789");
        String content = mockMvc
                .perform(post("/owners")
                        .with(user("admin").roles("USER", "ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(owner)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is("First")))
                .andExpect(jsonPath("$.lastName", is("Last")))
                .andExpect(jsonPath("$.address", is("Address")))
                .andExpect(jsonPath("$.city", is("City")))
                .andExpect(jsonPath("$.telephone", is("0123456789")))
                .andReturn().getResponse().getContentAsString();
        Owner savedOwner = objectMapper.readValue(content, Owner.class);
        jdbcTemplate.update("delete from owners where id = "+ savedOwner.getId());
    }

    @Test
    public void testAddInvalidCity() throws Exception {
        Owner owner = new Owner("First", "Last", "Address", "", "0123456789");
        mockMvc
                .perform(post("/owners")
                        .with(user("admin").roles("USER", "ADMIN"))
                        .with(csrf())
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
                        .with(user("admin").roles("USER", "ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(owner)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request [Owner telephone must be 10 digits]"));
    }

    @Test
    public void testUpdate() throws Exception {
        Owner owner = new Owner("George", "Franklin", "110 W. Liberty St.", "Randwick", "6085551023");
        owner.setId(1);
        mockMvc
                .perform(put("/owners/1")
                        .with(user("admin").roles("USER", "ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(owner)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city", is("Randwick")));
        jdbcTemplate.update("update owners set city = 'Madison' where id = 1");
    }

    @Test
    public void testUpdateNotFound() throws Exception {
        Owner owner = new Owner("George", "Franklin", "110 W. Liberty St.", "Randwick", "6085551023");
        owner.setId(10000);
        mockMvc
                .perform(put("/owners/10000")
                        .with(user("admin").roles("USER", "ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(owner)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Not found [Owner [10000] not found]"));
    }

}
