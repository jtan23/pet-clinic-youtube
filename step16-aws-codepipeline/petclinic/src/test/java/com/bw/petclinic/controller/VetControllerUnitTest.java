package com.bw.petclinic.controller;

import com.bw.petclinic.domain.Vet;
import com.bw.petclinic.service.VetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@WebMvcTest(VetController.class)
public class VetControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VetService vetService;

    @Test
    public void testFindAllDefault() throws Exception {
        Pageable pageable = PageRequest.of(0, 3);
        Page<Vet> vets = new PageImpl<>(List.of(new Vet(), new Vet(), new Vet()), pageable, 6);
        when(vetService.findAll(0, 3)).thenReturn(vets);
        ModelAndView mav = mockMvc
                .perform(get("/vets")
                        .with(SecurityMockMvcRequestPostProcessors.user("user")))
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
        Pageable pageable = PageRequest.of(1, 2);
        Page<Vet> vets = new PageImpl<>(List.of(new Vet(), new Vet()), pageable, 6);
        when(vetService.findAll(1, 2)).thenReturn(vets);
        ModelAndView mav = mockMvc
                .perform(get("/vets?pageNumber=2&pageSize=2")
                        .with(SecurityMockMvcRequestPostProcessors.user("user")))
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
