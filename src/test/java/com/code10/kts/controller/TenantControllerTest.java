package com.code10.kts.controller;

import com.code10.kts.model.domain.user.Tenant;
import com.code10.kts.model.dto.TenantRegisterDto;
import com.code10.kts.repository.UserRepository;
import com.code10.kts.util.JsonUtil;
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

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests {@link TenantController} methods.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
@AutoConfigureMockMvc
@Transactional
public class TenantControllerTest {

    private static final String BASE_URL = "/api/tenants";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Test tenant registration with valid data.
     * This should return created.
     */
    @Test
    public void createShouldReturnCreatedWhenDataIsValid() throws Exception {
        // Arrange
        final TenantRegisterDto tenantRegisterDto = new TenantRegisterDto("test", "pass",
                "a@a", "1234", "name", "lastName");
        final int beforeUserCount = userRepository.findAll().size();

        // Act
        final MvcResult mvcResult = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(tenantRegisterDto)))
                .andExpect(status().isCreated())
                .andReturn();

        // Assert
        final int afterUserCount = userRepository.findAll().size();
        assertEquals(beforeUserCount + 1, afterUserCount);

        final Long tenantId = JsonUtil.pojo(mvcResult.getResponse().getContentAsString(), Long.class);
        final Tenant tenant = (Tenant) userRepository.findById(tenantId).orElseGet(null);
        assertNotNull(tenant);
        assertEquals(tenantRegisterDto.getUsername(), tenant.getUsername());
        assertTrue(passwordEncoder.matches(tenantRegisterDto.getPassword(), tenant.getPassword()));
        assertEquals(tenantRegisterDto.getEmail(), tenant.getEmail());
        assertEquals(tenantRegisterDto.getPhoneNumber(), tenant.getPhoneNumber());
        assertEquals(tenantRegisterDto.getFirstName(), tenant.getFirstName());
        assertEquals(tenantRegisterDto.getLastName(), tenant.getLastName());
    }

    /**
     * Test tenant registration with existing username.
     * This should return bad request.
     */
    @Test
    public void createShouldReturnBadRequestWhenTenantExists() throws Exception {
        // Arrange
        final Tenant tenant = new Tenant("test", "pass",
                "a@a", "1234", "name", "lastName");
        userRepository.save(tenant);
        final int beforeUserCount = userRepository.findAll().size();

        final TenantRegisterDto tenantRegisterDto = new TenantRegisterDto("test", "pass",
                "a@a", "1234", "name", "lastName");

        // Act
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(tenantRegisterDto)))
                .andExpect(status().isBadRequest());

        // Assert
        final int afterUserCount = userRepository.findAll().size();
        assertEquals(beforeUserCount, afterUserCount);
    }

    /**
     * Test tenant registration with invalid data.
     * This should return bad request.
     */
    @Test
    public void createShouldReturnBadRequestWhenDataIsInvalid() throws Exception {
        // Arrange
        final TenantRegisterDto tenantRegisterDto = new TenantRegisterDto("test", "pass",
                "a@a", "1234", "name", "lastName");
        tenantRegisterDto.setFirstName("");
        final int beforeUserCount = userRepository.findAll().size();

        // Act
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(tenantRegisterDto)))
                .andExpect(status().isBadRequest());

        // Assert
        final int afterUserCount = userRepository.findAll().size();
        assertEquals(beforeUserCount, afterUserCount);
    }
}
