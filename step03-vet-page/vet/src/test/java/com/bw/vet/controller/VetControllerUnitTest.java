package com.bw.vet.controller;

import com.bw.vet.domain.Vet;
import com.bw.vet.repository.VetRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VetController.class)
public class VetControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VetRepository vetRepository;

    @Test
    public void testFindAll() throws Exception {
        Pageable pageable = PageRequest.of(0, 3);
        Page<Vet> vetPage = new PageImpl<>(List.of(new Vet(), new Vet(), new Vet()), pageable, 6);
        when(vetRepository.findAll(pageable)).thenReturn(vetPage);
        mockMvc
                .perform(get("/vets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.totalElements", is(6)))
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.size", is(3)));
    }

    @Test
    public void testFindAllCustom() throws Exception {
        Pageable pageable = PageRequest.of(1, 2);
        Page<Vet> vetPage = new PageImpl<>(List.of(new Vet(), new Vet()), pageable, 6);
        when(vetRepository.findAll(pageable)).thenReturn(vetPage);
        mockMvc
                .perform(get("/vets?pageNumber=1&pageSize=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(6)))
                .andExpect(jsonPath("$.number", is(1)))
                .andExpect(jsonPath("$.size", is(2)));
    }

    @Test
    public void testFindAllTypeMismatch() throws Exception {
        mockMvc
                .perform(get("/vets?pageNumber=1&pageSize=abc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request [Failed to convert value of type 'java.lang.String' to required type 'int'; For input string: \"abc\"]"));
    }
}
