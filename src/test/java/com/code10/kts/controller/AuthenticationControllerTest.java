package com.code10.kts.controller;

import com.code10.kts.model.domain.user.User;
import com.code10.kts.model.dto.AuthenticationRequest;
import com.code10.kts.model.security.UserDetailsImpl;
import com.code10.kts.repository.UserRepository;
import com.code10.kts.security.TokenUtils;
import com.code10.kts.util.DataUtil;
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

import java.util.Map;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests {@link AuthenticationController} methods.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
@AutoConfigureMockMvc
@Transactional
public class AuthenticationControllerTest {

    private static final String BASE_URL = "/api/auth";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Tests authenticate with valid data.
     * Should return valid token.
     */
    @Test
    public void authenticateShouldReturnTokenWhenValid() throws Exception {
        // Arrange
        final String username = "test";
        final String password = "test";
        final User user = new User(username, passwordEncoder.encode(password), "a@a", "123");
        userRepository.save(user);

        final AuthenticationRequest authenticationRequest = new AuthenticationRequest(username, password);

        // Act
        final MvcResult result = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(authenticationRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        final Map json = JsonUtil.pojo(result.getResponse().getContentAsString(), Map.class);
        assertEquals(username, tokenUtils.getUsernameFromToken(json.get("token").toString()));
    }

    /**
     * Tests authenticate with wrong credentials.
     * Should return unauthorized.
     */
    @Test
    public void authenticateShouldReturnUnauthorizedWhenWrongCredentials() throws Exception {
        // Arrange
        final String username = "test";
        final String password = "test";
        final User user = new User(username, password, "a@a", "123");
        userRepository.save(user);

        final AuthenticationRequest authenticationRequest = new AuthenticationRequest(username, password + "test");

        // Act
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(authenticationRequest)))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests authenticate with invalid data.
     * Should return bad request.
     */
    @Test
    public void authenticateShouldReturnBadRequestWhenInvalid() throws Exception {
        // Arrange
        final String username = null;
        final String password = "test";

        final AuthenticationRequest authenticationRequest = new AuthenticationRequest(username, password);

        // Act
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(authenticationRequest)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test username taken with taken username.
     * Should return true.
     */
    @Test
    public void usernameTakenShouldReturnTrueWhenTaken() throws Exception {
        // Arrange
        final String username = "luka";

        // Act
        final MvcResult result = mockMvc.perform(get(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .param("username", username))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        final boolean taken = JsonUtil.pojo(result.getResponse().getContentAsString(), Boolean.class);
        assertTrue(taken);
    }

    /**
     * Test username taken with available username.
     * Should return false.
     */
    @Test
    public void usernameTakenShouldReturnFalseWhenNotTaken() throws Exception {
        // Arrange
        final String username = "newUsername";

        // Act
        final MvcResult result = mockMvc.perform(get(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .param("username", username))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        final boolean taken = JsonUtil.pojo(result.getResponse().getContentAsString(), Boolean.class);
        assertFalse(taken);
    }

    @Test
    public void findCurrentUserShouldReturnUserWhenValid() throws Exception {
        // Act
        final MvcResult result = mockMvc.perform(get(BASE_URL + "/me")
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        final User user = JsonUtil.pojo(result.getResponse().getContentAsString(), User.class);
        assertEquals(DataUtil.EXISTING_BUILDING_TENANT_USERNAME, user.getUsername());
    }
}
