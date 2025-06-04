package com.bw.petclinic.controller;

import com.bw.petclinic.domain.Owner;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class OwnerControllerIntTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
    public void testListOwnerDefault() throws Exception {
        ModelAndView mav = mockMvc
                .perform(get("/owners")
                        .with(SecurityMockMvcRequestPostProcessors.user("user")))
                .andExpect(status().isOk())
                .andExpect(view().name("ownerList"))
                .andExpect(model().attributeExists("owners"))
                .andReturn().getModelAndView();
        assertNotNull(mav);
        @SuppressWarnings("unchecked")
        Page<Owner> ownerPage = (Page<Owner>) mav.getModel().get("owners");
        assertEquals(5, ownerPage.getContent().size());
        assertEquals(10, ownerPage.getTotalElements());
        assertEquals(0, ownerPage.getNumber());
        assertEquals(5, ownerPage.getSize());
    }

    @Test
    public void testListOwnerDavis() throws Exception {
        ModelAndView mav = mockMvc
                .perform(get("/owners?lastName=Davis")
                        .with(SecurityMockMvcRequestPostProcessors.user("user")))
                .andExpect(status().isOk())
                .andExpect(view().name("ownerList"))
                .andExpect(model().attributeExists("owners"))
                .andReturn().getModelAndView();
        assertNotNull(mav);
        @SuppressWarnings("unchecked")
        Page<Owner> ownerPage = (Page<Owner>) mav.getModel().get("owners");
        assertEquals(2, ownerPage.getContent().size());
        assertEquals(2, ownerPage.getTotalElements());
        assertEquals(0, ownerPage.getNumber());
        assertEquals(5, ownerPage.getSize());
    }

    @Test
    public void testListOwnerNotFound() throws Exception {
        ModelAndView mav = mockMvc
                .perform(get("/owners?lastName=Dav")
                        .with(SecurityMockMvcRequestPostProcessors.user("user")))
                .andExpect(status().isOk())
                .andExpect(view().name("ownerFind"))
                .andExpect(model().attributeExists("owner"))
                .andReturn()
                .getModelAndView();
        assertNotNull(mav);
        Owner owner = (Owner) mav.getModel().get("owner");
        assertEquals("Dav", owner.getLastName());
    }

    @Test
    public void testShowOwner() throws Exception {
        ModelAndView mav = mockMvc
                .perform(get("/owners/6")
                        .with(SecurityMockMvcRequestPostProcessors.user("user")))
                .andExpect(status().isOk())
                .andExpect(view().name("ownerDetails"))
                .andExpect(model().attributeExists("owner"))
                .andReturn()
                .getModelAndView();
        assertNotNull(mav);
        Owner owner = (Owner) mav.getModel().get("owner");
        assertEquals(6, owner.getId());
        assertEquals(2, owner.getPets().size());
        assertEquals(2, owner.getPets().get(0).getVisits().size());
    }

    @Test
    public void testNewOwner() throws Exception {
        mockMvc
                .perform(get("/owners/new")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("ownerForm"))
                .andExpect(model().attributeExists("owner"));
    }

    @Test
    public void testAddOwner() throws Exception {
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
        String savedOwnerId = viewName.substring(viewName.lastIndexOf("/") + 1);
        jdbcTemplate.update("delete from owners where id = " + savedOwnerId);
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

    @Test
    public void testEditOwner() throws Exception {
        Owner owner = new Owner("George", "Franklin", "110 W. Liberty St.", "Madison", "6085551023");
        owner.setId(1);
        mockMvc
                .perform(get("/owners/edit?ownerId=1")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("ownerForm"))
                .andExpect(model().attribute("owner", owner));
    }

    @Test
    public void testUpdateOwner() throws Exception {
        mockMvc
                .perform(post("/owners/edit?ownerId=1")
                        .param("id", "1")
                        .param("firstName", "George")
                        .param("lastName", "Franklin")
                        .param("address", "110 W. Liberty St.")
                        .param("city", "Randwick")
                        .param("telephone", "6085551023")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/owners/1"));
        assertEquals("Randwick", jdbcTemplate.queryForObject("select city from owners where id = 1", String.class));
        jdbcTemplate.update("update owners set city = 'Madison' where id = 1");
    }

}
