package com.bw.petclinic.controller;

import com.bw.petclinic.domain.Owner;
import com.bw.petclinic.domain.Pet;
import com.bw.petclinic.service.OwnerService;
import com.bw.petclinic.service.PetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OwnerController.class)
public class OwnerControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnerService ownerService;

    @MockBean
    private PetService petService;

    @Test
    public void testFindOwner() throws Exception {
        mockMvc
                .perform(get("/owners/find"))
                .andExpect(status().isOk())
                .andExpect(view().name("ownerFind"))
                .andExpect(model().attribute("owner", new Owner()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testListOwnerDefault() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        Owner owner = new Owner();
        owner.setId(1);
        Page<Owner> ownerPage = new PageImpl<>(List.of(owner, owner, owner, owner, owner), pageable, 10);
        when(ownerService.findAll(0, 5)).thenReturn(ownerPage);
        when(petService.findByOwnerId(1)).thenReturn(List.of(new Pet()));
        ModelAndView mav = mockMvc
                .perform(get("/owners"))
                .andExpect(status().isOk())
                .andExpect(view().name("ownerList"))
                .andExpect(model().attributeExists("owners"))
                .andReturn().getModelAndView();
        assertNotNull(mav);
        ownerPage = (Page<Owner>) mav.getModel().get("owners");
        assertEquals(5, ownerPage.getContent().size());
        assertEquals(10, ownerPage.getTotalElements());
        assertEquals(0, ownerPage.getNumber());
        assertEquals(5, ownerPage.getSize());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testListOwnerDavis() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        Owner owner = new Owner();
        owner.setId(1);
        Page<Owner> ownerPage = new PageImpl<>(List.of(owner, owner), pageable, 2);
        when(ownerService.findByLastName(0, 5, "Davis")).thenReturn(ownerPage);
        when(petService.findByOwnerId(1)).thenReturn(List.of(new Pet()));
        ModelAndView mav = mockMvc
                .perform(get("/owners?lastName=Davis"))
                .andExpect(status().isOk())
                .andExpect(view().name("ownerList"))
                .andExpect(model().attributeExists("owners"))
                .andReturn().getModelAndView();
        assertNotNull(mav);
        ownerPage = (Page<Owner>) mav.getModel().get("owners");
        assertEquals(2, ownerPage.getContent().size());
        assertEquals(2, ownerPage.getTotalElements());
        assertEquals(0, ownerPage.getNumber());
        assertEquals(5, ownerPage.getSize());
    }

    @Test
    public void testListOwnerNotFound() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Owner> ownerPage = new PageImpl<>(List.of(), pageable, 0);
        when(ownerService.findByLastName(0, 5, "Dav")).thenReturn(ownerPage);
        ModelAndView mav = mockMvc
                .perform(get("/owners?lastName=Dav"))
                .andExpect(status().isOk())
                .andExpect(view().name("ownerFind"))
                .andExpect(model().attributeExists("owner"))
                .andReturn().getModelAndView();
        assertNotNull(mav);
        Owner owner = (Owner) mav.getModel().get("owner");
        assertEquals("Dav", owner.getLastName());
    }

}
