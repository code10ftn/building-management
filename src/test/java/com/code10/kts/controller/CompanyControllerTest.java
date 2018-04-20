package com.code10.kts.controller;

import com.code10.kts.data.CompanyData;
import com.code10.kts.model.domain.user.Company;
import com.code10.kts.model.dto.CompanyRegisterDto;
import com.code10.kts.repository.UserRepository;
import com.code10.kts.util.DataUtil;
import com.code10.kts.util.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests {@link CompanyControllerTest} methods.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
@AutoConfigureMockMvc
@Transactional
public class CompanyControllerTest {

    private static final String BASE_URL = "/api/companies/";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void findAllUnassignedShouldReturnAllUnassigned() throws Exception {
        // Arrange
        final Company assignedCompany = (Company) userRepository.findById(DataUtil.EXISTING_COMPANY_ID).get();

        // Act
        final MvcResult result = mockMvc.perform(get(BASE_URL)
                .param("buildingId", DataUtil.EXISTING_BUILDING_ID.toString()))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        final TypeReference type = new TypeReference<List<Company>>() {
        };
        final List<Company> companies = JsonUtil.pojo(result.getResponse().getContentAsString(), type);
        assertEquals(2, companies.size());
        assertFalse(companies.contains(assignedCompany));
    }

    /**
     * Test create company with invalid data.
     * This should return bad request.
     */
    @Test
    public void createShouldReturnBadRequestWhenInvalid() throws Exception {
        // Arrange
        final CompanyRegisterDto companyRegisterDto = new CompanyRegisterDto();

        // Act
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(companyRegisterDto)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test create company with valid data.
     * This should return created.
     */
    @Test
    public void createShouldReturnCreatedWhenValid() throws Exception {
        // Arrange
        final CompanyRegisterDto companyRegisterDto = CompanyData.getCompanyRegisterDto();

        // Act
        final MvcResult mvcResult = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(companyRegisterDto)))
                .andExpect(status().isCreated())
                .andReturn();

        // Assert
        final Long companyId = JsonUtil.pojo(mvcResult.getResponse().getContentAsString(), Long.class);
        final Company company = (Company) userRepository.findById(companyId).orElseGet(null);
        assertNotNull(company);
        assertEquals(companyRegisterDto.getUsername(), company.getUsername());
        assertTrue(passwordEncoder.matches(companyRegisterDto.getPassword(), company.getPassword()));
        assertEquals(companyRegisterDto.getEmail(), company.getEmail());
        assertEquals(companyRegisterDto.getPhoneNumber(), company.getPhoneNumber());
        assertEquals(companyRegisterDto.getDescription(), company.getDescription());
        assertEquals(companyRegisterDto.getAddress(), company.getAddress());
        assertEquals(companyRegisterDto.getName(), company.getName());
        assertEquals(companyRegisterDto.getWorkArea(), company.getWorkArea());
    }
}