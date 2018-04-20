package com.code10.kts.controller;

import com.code10.kts.data.BuildingData;
import com.code10.kts.data.CustomPageImpl;
import com.code10.kts.model.domain.Building;
import com.code10.kts.model.dto.BuildingCreateDto;
import com.code10.kts.model.dto.BuildingUpdateDto;
import com.code10.kts.model.dto.SupervisorResponse;
import com.code10.kts.model.security.UserDetailsImpl;
import com.code10.kts.repository.BuildingRepository;
import com.code10.kts.security.TokenUtils;
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
 * Tests {@link BuildingController} methods.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
@AutoConfigureMockMvc
@Transactional
public class BuildingControllerTest {

    private static final String BASE_URL = "/api/buildings";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private BuildingRepository buildingRepository;

    /**
     * Test find all buildings when user is authenticated as a tenant.
     * This should return Ok and list of all buildings.
     */
    @Test
    public void findAllWithValidTokenShouldReturnAllBuildings() throws Exception {
        // Act
        final MvcResult result = mockMvc.perform(get(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .param("p", "1")
                .param("s", "100")
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        final CustomPageImpl buildings = JsonUtil.pojo(result.getResponse().getContentAsString(), CustomPageImpl.class);
        assertNotNull(buildings);
        assertTrue(buildings.getTotalElements() > 0);
    }

    /**
     * Test find all buildings when user is not authenticated.
     * This should return Unauthorized.
     */
    @Test
    public void findAllWithoutTokenShouldReturnUnauthorized() throws Exception {
        // Act
        mockMvc.perform(get(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Test find building by id with authenticated tenant and valid building id.
     * This should return Ok and building with said id.
     */
    @Test
    public void findByIdShouldReturnBuildingWithValidId() throws Exception {
        // Act
        final MvcResult result = mockMvc.perform(get(BASE_URL + "/" + DataUtil.EXISTING_BUILDING_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        final Building building = JsonUtil.pojo(result.getResponse().getContentAsString(), Building.class);
        assertNotNull(building);
        assertEquals(DataUtil.EXISTING_BUILDING_ID, building.getId());
    }

    /**
     * Test find building by id with invalid id.
     * This should return NotFound.
     */
    @Test
    public void findByIdShouldReturnNotFoundWithInvalidId() throws Exception {
        // Act
        mockMvc.perform(get(BASE_URL + "/" + BuildingData.NON_EXISTENT_BUILDING_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isNotFound());
    }

    /**
     * Test create building with valid data and user authenticated as admin.
     * This should return Created.
     */
    @Test
    public void createShouldReturnBuildingIdWhenAdminIsLoggedIn() throws Exception {
        // Arrange
        final BuildingCreateDto buildingCreateDto = BuildingData.getCreateDto();

        // Act
        final MvcResult mvcResult = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(buildingCreateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_ADMIN_USERNAME))))
                .andExpect(status().isCreated())
                .andReturn();

        // Assert
        final Long buildingId = JsonUtil.pojo(mvcResult.getResponse().getContentAsString(), Long.class);
        final Building building = buildingRepository.findById(buildingId).orElseGet(null);
        assertNotNull(building);
        assertEquals(buildingCreateDto.getAddress(), building.getAddress());
        assertEquals(buildingCreateDto.getApartmentCount(), building.getApartments().size());
    }

    /**
     * Test create building with valid data when user is not authenticated as admin.
     * This should return Forbidden since non admin user cannot create building.
     */
    @Test
    public void createShouldReturnForbiddenWhenUserIsNotAdmin() throws Exception {
        // Arrange
        final BuildingCreateDto buildingCreateDto = BuildingData.getCreateDto();

        // Act
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(buildingCreateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isForbidden());
    }

    /**
     * Test create building when building apartment count is negative.
     * This should return BadRequest since apartment count must be greater than 1.
     */
    @Test
    public void createShouldReturnBadRequestWhenBuildingInvalid() throws Exception {
        // Arrange
        final BuildingCreateDto buildingCreateDto = BuildingData.getCreateDto();
        buildingCreateDto.setApartmentCount(-1);

        // Act
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(buildingCreateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_ADMIN_USERNAME))))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test update building with valid building id and user authenticated as admin.
     * This should return Ok.
     */
    @Test
    public void updateShouldReturnOkWhenValid() throws Exception {
        // Arrange
        final BuildingUpdateDto buildingUpdateDto = BuildingData.getUpdateDto();

        // Act
        final MvcResult mvcResult = mockMvc.perform(put(BASE_URL + "/" + DataUtil.EXISTING_BUILDING_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(buildingUpdateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_ADMIN_USERNAME))))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        final Building updated = JsonUtil.pojo(mvcResult.getResponse().getContentAsString(), Building.class);
        assertNotNull(updated);
        assertEquals(BuildingData.BUILDING_UPDATED_ADDRESS, updated.getAddress());
    }

    /**
     * Test update building with invalid supervisor id.
     * This should return NotFound.
     */
    @Test
    public void updateShouldReturnNotFoundWhenSupervisorIdIsNonexistent() throws Exception {
        // Arrange
        final BuildingUpdateDto buildingUpdateDto = BuildingData.getUpdateDto();
        buildingUpdateDto.setSupervisorId(DataUtil.NON_EXISTENT_USER_ID);

        // Act
        mockMvc.perform(put(BASE_URL + "/" + DataUtil.EXISTING_BUILDING_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(buildingUpdateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_ADMIN_USERNAME))))
                .andExpect(status().isNotFound());
    }

    /**
     * Test update building when assigned supervisor doesn't have authority to supervise.
     * This should return BadRequest.
     */
    @Test
    public void updateShouldReturnBadRequestWhenUserCannotBeSupervisor() throws Exception {
        // Arrange
        final BuildingUpdateDto buildingUpdateDto = BuildingData.getUpdateDto();
        buildingUpdateDto.setSupervisorId(DataUtil.EXISTING_BUILDING_NON_TENANT_ID);

        // Act
        mockMvc.perform(put(BASE_URL + "/" + DataUtil.EXISTING_BUILDING_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(buildingUpdateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_ADMIN_USERNAME))))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test update building when id is non existent.
     * This should return NotFound.
     */
    @Test
    public void updateShouldReturnNotFoundWhenIdIsNonExistent() throws Exception {
        // Arrange
        final BuildingUpdateDto buildingUpdateDto = BuildingData.getUpdateDto();

        // Act
        mockMvc.perform(put(BASE_URL + "/" + BuildingData.NON_EXISTENT_BUILDING_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(buildingUpdateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_ADMIN_USERNAME))))
                .andExpect(status().isNotFound());
    }

    /**
     * Test delete building with valid id.
     * This should return Ok.
     */
    @Test
    public void deleteShouldReturnOkWhenValid() throws Exception {
        // Act
        mockMvc.perform(delete(BASE_URL + "/" + BuildingData.DELETE_BUILDING_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_ADMIN_USERNAME))))
                .andExpect(status().isOk());
    }

    /**
     * Test delete building with non existent building id.
     * This should return NotFound.
     */
    @Test
    public void deleteShouldReturnNotFoundWithNonExistentBuildingId() throws Exception {
        // Act
        mockMvc.perform(delete(BASE_URL + "/" + BuildingData.NON_EXISTENT_BUILDING_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_ADMIN_USERNAME))))
                .andExpect(status().isNotFound());
    }

    /**
     * Test assign company with valid building id and non assigned company id.
     * This should return Ok.
     */
    @Test
    public void assignCompanyShouldReturnOkWhenValid() throws Exception {
        // Act
        mockMvc.perform(put(BASE_URL + "/" + DataUtil.EXISTING_BUILDING_ID + "/assignCompany")
                .contentType(MediaType.APPLICATION_JSON)
                .param("companyId", String.valueOf(BuildingData.NON_ASSIGNED_COMPANY_ID))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(BuildingData.EXISTING_BUILDING_SUPERVISOR_USERNAME))))
                .andExpect(status().isOk());
    }

    /**
     * Test assign company when authenticated user is not supervisor.
     * This should return Forbidden.
     */
    @Test
    public void assignCompanyShouldReturnForbiddenWhenUserIsNotSupervisor() throws Exception {
        // Act
        mockMvc.perform(put(BASE_URL + "/" + DataUtil.EXISTING_BUILDING_ID + "/assignCompany")
                .contentType(MediaType.APPLICATION_JSON)
                .param("companyId", String.valueOf(BuildingData.NON_ASSIGNED_COMPANY_ID))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_NON_TENANT_USERNAME))))
                .andExpect(status().isForbidden());
    }

    /**
     * Test assign company with non existent building id.
     * This should return NotFound.
     */
    @Test
    public void assignCompanyShouldReturnNotFoundWhenBuildingIdIsInvalid() throws Exception {
        // Act
        mockMvc.perform(put(BASE_URL + "/" + BuildingData.NON_EXISTENT_BUILDING_ID + "/assignCompany")
                .contentType(MediaType.APPLICATION_JSON)
                .param("companyId", String.valueOf(BuildingData.NON_ASSIGNED_COMPANY_ID))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(BuildingData.EXISTING_BUILDING_SUPERVISOR_USERNAME))))
                .andExpect(status().isNotFound());
    }

    /**
     * Test assign company with invalid company id. Tenant id is passed as company id.
     * This should return Forbidden since Tenant cannot be cast to Company.
     */
    @Test
    public void assignCompanyShouldReturnForbiddenWhenCompanyIdIsTenant() throws Exception {
        // Act
        mockMvc.perform(put(BASE_URL + "/" + DataUtil.EXISTING_BUILDING_ID + "/assignCompany")
                .contentType(MediaType.APPLICATION_JSON)
                .param("companyId", String.valueOf(DataUtil.EXISTING_TENANT_ID))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(BuildingData.EXISTING_BUILDING_SUPERVISOR_USERNAME))))
                .andExpect(status().isForbidden());
    }

    /**
     * Test assign company when company is already assigned.
     * This should return BadRequest.
     */
    @Test
    public void assignCompanyShouldReturnBadRequestWhenCompanyIdIsAlreadyAssigned() throws Exception {
        // Act
        mockMvc.perform(put(BASE_URL + "/" + DataUtil.EXISTING_BUILDING_ID + "/assignCompany")
                .contentType(MediaType.APPLICATION_JSON)
                .param("companyId", String.valueOf(BuildingData.COMPANY_ID))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(BuildingData.EXISTING_BUILDING_SUPERVISOR_USERNAME))))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test revoke company with valid building id and non assigned company id.
     * This should return Ok.
     */
    @Test
    public void revokeCompanyShouldReturnOkWhenValid() throws Exception {
        // Act
        mockMvc.perform(put(BASE_URL + "/" + DataUtil.EXISTING_BUILDING_ID + "/revokeCompany")
                .contentType(MediaType.APPLICATION_JSON)
                .param("companyId", String.valueOf(BuildingData.COMPANY_ID))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(BuildingData.EXISTING_BUILDING_SUPERVISOR_USERNAME))))
                .andExpect(status().isOk());
    }

    /**
     * Test revoke company when company is not assigned.
     * This should return Ok since the outcome is the same whether the company was previously assigned or not.
     */
    @Test
    public void revokeCompanyShouldReturnOkWhenCompanyIdIsNotAssigned() throws Exception {
        // Act
        mockMvc.perform(put(BASE_URL + "/" + DataUtil.EXISTING_BUILDING_ID + "/revokeCompany")
                .contentType(MediaType.APPLICATION_JSON)
                .param("companyId", String.valueOf(BuildingData.NON_ASSIGNED_COMPANY_ID))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(BuildingData.EXISTING_BUILDING_SUPERVISOR_USERNAME))))
                .andExpect(status().isOk());
    }

    /**
     * Test revoke company with invalid building id.
     * This should return NotFound.
     */
    @Test
    public void revokeCompanyShouldReturnNotFoundWhenBuildingIdIsInvalid() throws Exception {
        // Act
        mockMvc.perform(put(BASE_URL + "/" + BuildingData.NON_EXISTENT_BUILDING_ID + "/revokeCompany")
                .contentType(MediaType.APPLICATION_JSON)
                .param("companyId", String.valueOf(BuildingData.COMPANY_ID))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(BuildingData.EXISTING_BUILDING_SUPERVISOR_USERNAME))))
                .andExpect(status().isNotFound());
    }

    /**
     * Test revoke company with invalid company id. Tenant id is passed as company id.
     * This should return Forbidden since Tenant cannot be cast to Company.
     */
    @Test
    public void revokeCompanyShouldReturnForbiddenWhenCompanyIdIsInvalid() throws Exception {
        // Act
        mockMvc.perform(put(BASE_URL + "/" + DataUtil.EXISTING_BUILDING_ID + "/revokeCompany")
                .contentType(MediaType.APPLICATION_JSON)
                .param("companyId", String.valueOf(DataUtil.EXISTING_TENANT_ID))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(BuildingData.EXISTING_BUILDING_SUPERVISOR_USERNAME))))
                .andExpect(status().isForbidden());
    }

    /**
     * Test revoke company when user in not authenticated as supervisor.
     * This should return Forbidden.
     */
    @Test
    public void revokeCompanyShouldReturnForbiddenWhenUserIsNotSupervisor() throws Exception {
        // Act
        mockMvc.perform(put(BASE_URL + "/" + DataUtil.EXISTING_BUILDING_ID + "/revokeCompany")
                .contentType(MediaType.APPLICATION_JSON)
                .param("companyId", String.valueOf(BuildingData.COMPANY_ID))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_NON_TENANT_USERNAME))))
                .andExpect(status().isForbidden());
    }

    /**
     * Test address taken with available address.
     * Should return false.
     */
    @Test
    public void addressTakenShouldReturnFalseWhenAddressIsUnique() throws Exception {
        // Act
        final MvcResult result = mockMvc.perform(get(BASE_URL + "/addressTaken")
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_ADMIN_USERNAME)))
                .param("address", BuildingData.BUILDING_NON_EXISTENT_ADDRESS))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        final boolean taken = JsonUtil.pojo(result.getResponse().getContentAsString(), Boolean.class);
        assertFalse(taken);
    }

    /**
     * Test address taken with taken address.
     * Should return true.
     */
    @Test
    public void addressTakenShouldReturnTrueWhenAddressIsTaken() throws Exception {
        // Act
        final MvcResult result = mockMvc.perform(get(BASE_URL + "/addressTaken")
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_ADMIN_USERNAME)))
                .param("address", BuildingData.BUILDING_EXISTING_ADDRESS))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        final boolean taken = JsonUtil.pojo(result.getResponse().getContentAsString(), Boolean.class);
        assertTrue(taken);
    }

    /**
     * Test get all potential supervisors.
     * This should pass and return correct number of potential supervisors.
     */
    @Test
    public void potentialSupervisorsShouldPassWhenAdminIsLoggedIn() throws Exception {
        // Act
        final MvcResult result = mockMvc.perform(get(BASE_URL + "/" + DataUtil.EXISTING_BUILDING_ID + "/potentialSupervisors")
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_ADMIN_USERNAME))))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        final TypeReference type = new TypeReference<List<SupervisorResponse>>() {
        };
        final List<SupervisorResponse> supervisors = JsonUtil.pojo(result.getResponse().getContentAsString(), type);
        assertEquals(3, supervisors.size());
    }

    /**
     * Test get all potential supervisors.
     * This should throw exception when logged in user is not admin.
     */
    @Test
    public void potentialSupervisorsShouldFailWhenUserIsNotSupervisor() throws Exception {
        // Act
        mockMvc.perform(get(BASE_URL + "/" + DataUtil.EXISTING_BUILDING_ID + "/potentialSupervisors")
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_SUPERVISOR_USERNAME))))
                .andExpect(status().isForbidden());
    }
}
