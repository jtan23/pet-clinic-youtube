package com.bw.petclinic.controller;

import com.bw.petclinic.domain.Owner;
import com.bw.petclinic.domain.Pet;
import com.bw.petclinic.domain.Visit;
import com.bw.petclinic.exception.PetClinicServiceException;
import com.bw.petclinic.service.OwnerService;
import com.bw.petclinic.service.PetService;
import com.bw.petclinic.service.VisitService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OwnerController.class)
public class OwnerControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnerService ownerService;

    @MockBean
    private PetService petService;

    @MockBean
    private VisitService visitService;

    @Test
    public void testFindOwner() throws Exception {
        mockMvc
                .perform(get("/owners/find")
                        .with(SecurityMockMvcRequestPostProcessors.user("user")))
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
                .perform(get("/owners")
                        .with(SecurityMockMvcRequestPostProcessors.user("user")))
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
                .perform(get("/owners?lastName=Davis")
                        .with(SecurityMockMvcRequestPostProcessors.user("user")))
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
                .perform(get("/owners?lastName=Dav")
                        .with(SecurityMockMvcRequestPostProcessors.user("user")))
                .andExpect(status().isOk())
                .andExpect(view().name("ownerFind"))
                .andExpect(model().attributeExists("owner"))
                .andReturn().getModelAndView();
        assertNotNull(mav);
        Owner owner = (Owner) mav.getModel().get("owner");
        assertEquals("Dav", owner.getLastName());
    }

    @Test
    public void testShowOwner() throws Exception {
        Pet pet = new Pet();
        pet.setId(7);
        Owner owner = new Owner();
        owner.setId(6);
        when(ownerService.findById(6)).thenReturn(owner);
        when(petService.findByOwnerId(6)).thenReturn(List.of(pet));
        when(visitService.findByPetId(7)).thenReturn(List.of(new Visit(), new Visit()));
        ModelAndView mav = mockMvc
                .perform(get("/owners/6")
                        .with(SecurityMockMvcRequestPostProcessors.user("user")))
                .andExpect(status().isOk())
                .andExpect(view().name("ownerDetails"))
                .andExpect(model().attributeExists("owner"))
                .andReturn()
                .getModelAndView();
        assertNotNull(mav);
        Owner loadedOwner = (Owner) mav.getModel().get("owner");
        assertEquals(6, loadedOwner.getId());
        assertEquals(1, loadedOwner.getPets().size());
        assertEquals(2, loadedOwner.getPets().get(0).getVisits().size());
    }

    @Test
    public void testNewOwner() throws Exception {
        mockMvc
                .perform(get("/owners/new")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("ownerForm"))
                .andExpect(model().attributeExists("action"))
                .andExpect(model().attributeExists("owner"));
    }

    @Test
    public void testAddOwner() throws Exception {
        Owner owner = new Owner("First", "Last", "Address", "City", "0123456789");
        when(ownerService.add(owner)).thenReturn(owner);
        ModelAndView mav = mockMvc
                .perform(post("/owners/new")
                        .param("firstName", "First")
                        .param("lastName", "Last")
                        .param("address", "Address")
                        .param("city", "City")
                        .param("telephone", "0123456789")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andReturn().getModelAndView();
        assertNotNull(mav);
        String viewName = mav.getViewName();
        assertNotNull(viewName);
        assertTrue(viewName.startsWith("redirect:/owners/"));
    }

    @Test
    public void testAddOwnerInvalidCity() throws Exception {
        mockMvc
                .perform(post("/owners/new")
                        .param("firstName", "First")
                        .param("lastName", "Last")
                        .param("address", "Address")
                        .param("city", "")
                        .param("telephone", "0123456789")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("ownerForm"));
    }

    @Test
    public void testAddOwnerInvalidTelephone() throws Exception {
        Owner owner = new Owner("First", "Last", "Address", "City", "0123456789");
        when(ownerService.add(any(Owner.class))).thenThrow(new PetClinicServiceException(
                "OwnerService.add failed [400 : \"Bad request [Owner telephone must be 10 digits]\"]"));
        mockMvc
                .perform(post("/owners/new")
                        .param("firstName", "First")
                        .param("lastName", "Last")
                        .param("address", "Address")
                        .param("city", "City")
                        .param("telephone", "Telephone")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("error", "OwnerService.add failed [400 : \"Bad request [Owner telephone must be 10 digits]\"]"));
    }

}
