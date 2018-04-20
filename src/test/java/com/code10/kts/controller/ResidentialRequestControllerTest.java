package com.code10.kts.controller;

import com.code10.kts.data.ResidentialRequestData;
import com.code10.kts.model.domain.Apartment;
import com.code10.kts.model.domain.ResidentialRequest;
import com.code10.kts.model.domain.user.Tenant;
import com.code10.kts.model.dto.TenantResidenceDto;
import com.code10.kts.model.security.UserDetailsImpl;
import com.code10.kts.repository.ResidentialRequestRepository;
import com.code10.kts.security.TokenUtils;
import com.code10.kts.service.ApartmentService;
import com.code10.kts.service.UserService;
import com.code10.kts.util.DataUtil;
import com.code10.kts.util.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests {@link ResidentialRequestController} methods.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
@AutoConfigureMockMvc
@Transactional
public class ResidentialRequestControllerTest {

    private static final String BASE_URL = "/api/buildings/" + DataUtil.EXISTING_BUILDING_ID + "/residentialRequests";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private UserService userService;

    @Autowired
    private ApartmentService apartmentService;

    @Autowired
    private ResidentialRequestRepository residentialRequestRepository;

    /**
     * Test find all when user is authenticated as a supervisor.
     * This should return Ok and all residential requests.
     */
    @Test
    public void findAllShouldReturnAllWhenUserIsAuthenticatedAsSupervisor() throws Exception {
        // Arrange
        final Tenant tenant = userService.findTenantById(DataUtil.EXISTING_BUILDING_NON_TENANT_ID);
        final Apartment apartment = apartmentService.findById(DataUtil.EXISTING_APARTMENT_ID);
        final ResidentialRequest residentialRequest = ResidentialRequestData.getResidentialRequest(tenant, apartment);
        residentialRequestRepository.save(residentialRequest);

        // Act
        final MvcResult result = mockMvc.perform(get(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .param("p", "1")
                .param("s", "100")
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_SUPERVISOR_USERNAME))))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        final TypeReference type = new TypeReference<List<ResidentialRequest>>() {
        };
        final List<ResidentialRequest> requests = JsonUtil.pojo(result.getResponse().getContentAsString(), type);
        assertNotNull(requests);
        assertTrue(requests.size() > 0);
    }

    /**
     * Test find all when user is not authenticated as a supervisor (e.g. user is authenticated as a tenant).
     * This should return Forbidden.
     */
    @Test
    public void findAllShouldReturnForbiddenWhenUserIsNotSupervisor() throws Exception {
        // Arrange
        final Tenant tenant = userService.findTenantById(DataUtil.EXISTING_TENANT_ID);
        final Apartment apartment = apartmentService.findById(DataUtil.EXISTING_APARTMENT_ID);
        final ResidentialRequest residentialRequest = ResidentialRequestData.getResidentialRequest(tenant, apartment);
        residentialRequestRepository.save(residentialRequest);

        // Act
        mockMvc.perform(get(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isForbidden());
    }

    /**
     * Test find by id when user is authenticated as a tenant.
     * This should return Ok and a residential request.
     */
    @Test
    public void findByIdShouldReturnResidentialRequestWhenTenantIsAuthenticated() throws Exception {
        // Arrange
        final Tenant tenant = userService.findTenantById(DataUtil.EXISTING_TENANT_ID);
        final Apartment apartment = apartmentService.findById(DataUtil.EXISTING_APARTMENT_ID);
        final ResidentialRequest residentialRequest = ResidentialRequestData.getResidentialRequest(tenant, apartment);
        residentialRequestRepository.save(residentialRequest);

        // Act
        final MvcResult result = mockMvc.perform(get(BASE_URL + "/my")
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        final ResidentialRequest request = JsonUtil.pojo(result.getResponse().getContentAsString(), ResidentialRequest.class);
        assertNotNull(request);
        assertEquals(tenant.getId(), request.getTenant().getId());
    }

    /**
     * Test find by id when user is not authenticated as a tenant.
     * This should return Forbidden.
     */
    @Test
    public void findByIdShouldReturnForbiddenWhenAuthenticatedUserIsNotTenant() throws Exception {
        // Arrange
        final Tenant tenant = userService.findTenantById(DataUtil.EXISTING_TENANT_ID);
        final Apartment apartment = apartmentService.findById(DataUtil.EXISTING_APARTMENT_ID);
        final ResidentialRequest residentialRequest = ResidentialRequestData.getResidentialRequest(tenant, apartment);
        residentialRequestRepository.save(residentialRequest);

        // Act
        mockMvc.perform(get(BASE_URL + "/my")
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_COMPANY_USERNAME))))
                .andExpect(status().isForbidden());
    }

    /**
     * Test create residential request with valid data.
     * This should return Created.
     */
    @Test
    public void createShouldReturnCreatedWhenValid() throws Exception {
        // Arrange
        final TenantResidenceDto tenantResidenceDto = ResidentialRequestData.getTenantResidenceDto();

        // Act
        final MvcResult mvcResult = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(tenantResidenceDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isCreated())
                .andReturn();

        // Assert
        final Long requestId = JsonUtil.pojo(mvcResult.getResponse().getContentAsString(), Long.class);
        final ResidentialRequest residentialRequest = residentialRequestRepository.findById(requestId).orElseGet(null);
        assertNotNull(residentialRequest);
    }

    /**
     * Test create residential request when apartment id is non existent.
     * This should return NotFound.
     */
    @Test
    public void createShouldReturnNotFoundWhenApartmentIdIsInvalid() throws Exception {
        // Arrange
        final TenantResidenceDto tenantResidenceDto = ResidentialRequestData.getTenantResidenceDto();
        tenantResidenceDto.setApartmentNumber(ResidentialRequestData.NON_EXISTENT_APARTMENT_NUMBER);

        // Act
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(tenantResidenceDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isNotFound());
    }

    /**
     * Test create when user already lives in this apartment.
     * This should return BadRequest.
     */
    @Test
    public void createShouldReturnBadRequestWhenUserAlreadyLivesInApartment() throws Exception {
        // Arrange
        final TenantResidenceDto tenantResidenceDto = ResidentialRequestData.getExistingTenantResidenceDto();

        // Act
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(tenantResidenceDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test create when user has already submitted a residential request.
     * This should return BadRequest.
     */
    @Test
    public void createShouldReturnBadRequestWhenUserAlreadySubmittedResidentialRequest() throws Exception {
        // Arrange
        final Tenant tenant = userService.findTenantById(DataUtil.EXISTING_BUILDING_NON_TENANT_ID);
        final Apartment apartment = apartmentService.findById(DataUtil.EXISTING_APARTMENT_ID);
        final ResidentialRequest residentialRequest = ResidentialRequestData.getResidentialRequest(tenant, apartment);
        residentialRequestRepository.save(residentialRequest);

        final TenantResidenceDto tenantResidenceDto = ResidentialRequestData.getTenantResidenceDto();

        // Act
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(tenantResidenceDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_NON_TENANT_USERNAME))))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test delete when user is authenticated as admin.
     * This should return Ok.
     */
    @Test
    public void deleteShouldReturnOkWhenUserIsAdmin() throws Exception {
        // Arrange
        final Tenant tenant = userService.findTenantById(DataUtil.EXISTING_BUILDING_NON_TENANT_ID);
        final Apartment apartment = apartmentService.findById(DataUtil.EXISTING_APARTMENT_ID);
        ResidentialRequest residentialRequest = ResidentialRequestData.getResidentialRequest(tenant, apartment);
        residentialRequest = residentialRequestRepository.save(residentialRequest);

        // Act
        mockMvc.perform(delete(BASE_URL + "/" + residentialRequest.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_ADMIN_USERNAME))))
                .andExpect(status().isOk());
    }

    /**
     * Test delete when user is a tenant that created the residential request.
     * This should return Ok.
     */
    @Test
    public void deleteShouldReturnOkWhenUserIsCreatorOfRequest() throws Exception {
        // Arrange
        final Tenant tenant = userService.findTenantById(DataUtil.EXISTING_BUILDING_NON_TENANT_ID);
        final Apartment apartment = apartmentService.findById(DataUtil.EXISTING_APARTMENT_ID);
        ResidentialRequest residentialRequest = ResidentialRequestData.getResidentialRequest(tenant, apartment);
        residentialRequest = residentialRequestRepository.save(residentialRequest);

        // Act
        mockMvc.perform(delete(BASE_URL + "/" + residentialRequest.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_NON_TENANT_USERNAME))))
                .andExpect(status().isOk());
    }

    /**
     * Test delete when user is authenticated as a tenant who is not a creator of this residential request.
     * This should return Forbidden.
     */
    @Test
    public void deleteShouldReturnForbiddenWhenTenantTriesToDelete() throws Exception {
        // Arrange
        final Tenant tenant = userService.findTenantById(DataUtil.EXISTING_BUILDING_NON_TENANT_ID);
        final Apartment apartment = apartmentService.findById(DataUtil.EXISTING_APARTMENT_ID);
        ResidentialRequest residentialRequest = ResidentialRequestData.getResidentialRequest(tenant, apartment);
        residentialRequest = residentialRequestRepository.save(residentialRequest);

        // Act
        mockMvc.perform(delete(BASE_URL + "/" + residentialRequest.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isForbidden());
    }

    /**
     * Test accept residential request when user is authenticated as admin.
     * This should return Ok.
     */
    @Test
    public void acceptShouldReturnOkWhenUserIsAdmin() throws Exception {
        // Arrange
        final Tenant tenant = userService.findTenantById(DataUtil.EXISTING_BUILDING_NON_TENANT_ID);
        final Apartment apartment = apartmentService.findById(DataUtil.EXISTING_APARTMENT_ID);
        ResidentialRequest residentialRequest = ResidentialRequestData.getResidentialRequest(tenant, apartment);
        residentialRequest = residentialRequestRepository.save(residentialRequest);

        // Act
        mockMvc.perform(put(BASE_URL + "/" + residentialRequest.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_ADMIN_USERNAME))))
                .andExpect(status().isOk());
    }

    /**
     * Test accept residential request when user is authenticated as admin.
     * This should return Forbidden.
     */
    @Test
    public void acceptShouldReturnForbiddenWhenUserIsTenant() throws Exception {
        // Arrange
        final Tenant tenant = userService.findTenantById(DataUtil.EXISTING_BUILDING_NON_TENANT_ID);
        final Apartment apartment = apartmentService.findById(DataUtil.EXISTING_APARTMENT_ID);
        ResidentialRequest residentialRequest = ResidentialRequestData.getResidentialRequest(tenant, apartment);
        residentialRequest = residentialRequestRepository.save(residentialRequest);

        // Act
        mockMvc.perform(put(BASE_URL + "/" + residentialRequest.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isForbidden());
    }
}
