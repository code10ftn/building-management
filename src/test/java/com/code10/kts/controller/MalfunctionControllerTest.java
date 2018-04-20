package com.code10.kts.controller;

import com.code10.kts.data.CustomPageImpl;
import com.code10.kts.model.domain.Malfunction;
import com.code10.kts.model.domain.WorkArea;
import com.code10.kts.model.domain.user.Tenant;
import com.code10.kts.model.dto.AssigneeUpdateDto;
import com.code10.kts.model.dto.MalfunctionCreateDto;
import com.code10.kts.model.dto.MalfunctionUpdateDto;
import com.code10.kts.model.dto.RepairmentCreateDto;
import com.code10.kts.model.security.UserDetailsImpl;
import com.code10.kts.repository.MalfunctionRepository;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests {@link MalfunctionController} methods.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
@AutoConfigureMockMvc
@Transactional
public class MalfunctionControllerTest {

    private static final String BASE_URL = String.format("/api/buildings/%s/malfunctions", DataUtil.EXISTING_BUILDING_ID);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private MalfunctionRepository malfunctionRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Tests find all malfunctions with authorized user.
     * This should return ok.
     */
    @Test
    public void findAllShouldReturnAllMalfunctions() throws Exception {
        // Act
        final MvcResult result = mockMvc.perform(get(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .param("p", "1")
                .param("s", "100")
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        final CustomPageImpl malfunctions = JsonUtil.pojo(result.getResponse().getContentAsString(), CustomPageImpl.class);
        assertTrue(malfunctions.getTotalElements() > 0);
    }

    /**
     * Tests find all malfunction with unauthorized user.
     * This should return forbidden.
     */
    @Test
    public void findAllShouldReturnForbiddenWhenUserHasNoAccessToBuilding() throws Exception {
        // Act
        mockMvc.perform(get(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_NON_COMPANY_USERNAME))))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests find  malfunction by id with authorized user.
     * This should return ok.
     */
    @Test
    public void findByIdShouldReturnMalfunction() throws Exception {
        // Act
        final MvcResult result = mockMvc.perform(get(BASE_URL + '/' + DataUtil.EXISTING_MALFUNCTION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        final Malfunction malfunction = JsonUtil.pojo(result.getResponse().getContentAsString(), Malfunction.class);
        assertEquals(DataUtil.EXISTING_MALFUNCTION_ID, malfunction.getId());
    }

    /**
     * Tests create malfunction with unauthorized user.
     * This should return forbidden.
     */
    @Test
    public void createShouldReturnForbiddenWhenUserHasNoAccessToBuilding() throws Exception {
        // Arrange
        final MalfunctionCreateDto malfunctionCreateDto = new MalfunctionCreateDto();
        malfunctionCreateDto.setDescription("test");
        malfunctionCreateDto.setWorkArea(WorkArea.HOUSEKEEPING);

        // Act
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(malfunctionCreateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_NON_COMPANY_USERNAME))))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests create malfunction with valid data and authorized user.
     * This should return created.
     */
    @Test
    public void createShouldReturnCreatedWhenValid() throws Exception {
        // Arrange
        final MalfunctionCreateDto malfunctionCreateDto = new MalfunctionCreateDto();
        malfunctionCreateDto.setDescription("test");
        malfunctionCreateDto.setWorkArea(WorkArea.HOUSEKEEPING);

        // Act
        final MvcResult mvcResult = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(malfunctionCreateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isCreated())
                .andReturn();

        // Assert
        final Long malfunctionId = JsonUtil.pojo(mvcResult.getResponse().getContentAsString(), Long.class);
        final Malfunction malfunction = malfunctionRepository.findById(malfunctionId).orElseGet(null);
        assertNotNull(malfunction);
        assertEquals(malfunctionCreateDto.getDescription(), malfunction.getDescription());
        assertEquals(malfunctionCreateDto.getWorkArea(), malfunction.getWorkArea());
    }

    /**
     * Tests create malfunction with invalid data and authorized user.
     * This should return bad request.
     */
    @Test
    public void createShouldReturnBadRequestWhenInvalid() throws Exception {
        // Arrange
        final MalfunctionCreateDto malfunctionCreateDto = new MalfunctionCreateDto();

        // Act
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(malfunctionCreateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests update malfunction with valid data and authorized user.
     * This should return ok.
     */
    @Test
    public void updateShouldReturnOkWhenValid() throws Exception {
        // Arrange
        final MalfunctionUpdateDto malfunctionUpdateDto = new MalfunctionUpdateDto();
        malfunctionUpdateDto.setDescription("test");
        malfunctionUpdateDto.setWorkArea(WorkArea.DOORS);

        // Act
        final MvcResult result = mockMvc.perform(put(BASE_URL + '/' + DataUtil.EXISTING_MALFUNCTION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(malfunctionUpdateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        final Malfunction updated = JsonUtil.pojo(result.getResponse().getContentAsString(), Malfunction.class);
        assertEquals(WorkArea.DOORS, updated.getWorkArea());
    }

    /**
     * Tests update malfunction with unauthorized user.
     * This should return forbidden.
     */
    @Test
    public void updateShouldReturnForbiddenWhenUserHasNoAccessToMalfunction() throws Exception {
        // Arrange
        final MalfunctionUpdateDto malfunctionUpdateDto = new MalfunctionUpdateDto();
        malfunctionUpdateDto.setDescription("test");
        malfunctionUpdateDto.setWorkArea(WorkArea.DOORS);

        // Act
        mockMvc.perform(put(BASE_URL + '/' + DataUtil.EXISTING_MALFUNCTION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(malfunctionUpdateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_COMPANY_USERNAME))))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests forward malfunction with valid data and authorized user.
     * This should return ok.
     */
    @Test
    public void forwardShouldReturnOkWhenValid() throws Exception {
        // Arrange
        final AssigneeUpdateDto assigneeUpdateDto = new AssigneeUpdateDto();
        assigneeUpdateDto.setAssigneeId(DataUtil.EXISTING_TENANT_SUPERVISOR_ID);

        // Act
        final MvcResult result = mockMvc.perform(put(BASE_URL + '/' + DataUtil.EXISTING_MALFUNCTION_ID + "/forward")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(assigneeUpdateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        final Malfunction updated = JsonUtil.pojo(result.getResponse().getContentAsString(), Malfunction.class);
        assertEquals(DataUtil.EXISTING_TENANT_SUPERVISOR_ID, updated.getAssignee().getId());
    }

    /**
     * Tests forward malfunction with unauthorized user.
     * This should return forbidden.
     */
    @Test
    public void forwardShouldReturnForbiddenWhenUserHasNoAccessToMalfunction() throws Exception {
        // Arrange
        final Malfunction malfunction = malfunctionRepository.findById(DataUtil.EXISTING_MALFUNCTION_ID).orElseGet(null);
        malfunction.setCreator((Tenant) userRepository.findById(DataUtil.EXISTING_TENANT_SUPERVISOR_ID).orElseGet(null));
        malfunction.setAssignee(userRepository.findById(DataUtil.EXISTING_TENANT_SUPERVISOR_ID).orElseGet(null));
        malfunctionRepository.save(malfunction);

        final AssigneeUpdateDto assigneeUpdateDto = new AssigneeUpdateDto();
        assigneeUpdateDto.setAssigneeId(DataUtil.EXISTING_TENANT_SUPERVISOR_ID);

        // Act
        mockMvc.perform(put(BASE_URL + '/' + DataUtil.EXISTING_MALFUNCTION_ID + "/forward")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(assigneeUpdateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests create repairment with valid data and authorized user.
     * This should return created.
     */
    @Test
    public void createRepairmentShouldReturnCreatedWhenValid() throws Exception {
        // Arrange
        final Malfunction malfunction = malfunctionRepository.findById(DataUtil.EXISTING_MALFUNCTION_ID).orElseGet(null);
        malfunction.setAssignee(userRepository.findByUsernameIgnoreCase(DataUtil.EXISTING_BUILDING_COMPANY_USERNAME).orElseGet(null));
        malfunctionRepository.save(malfunction);

        final RepairmentCreateDto repairmentCreateDto = new RepairmentCreateDto();
        repairmentCreateDto.setPrice(10);
        repairmentCreateDto.setRepairDate(new Date());

        // Act
        mockMvc.perform(post(BASE_URL + '/' + DataUtil.EXISTING_MALFUNCTION_ID + "/repairment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(repairmentCreateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_COMPANY_USERNAME))))
                .andExpect(status().isCreated());
    }

    /**
     * Tests create repairment with invalid data and authorized user.
     * This should return bad request.
     */
    @Test
    public void createRepairmentShouldReturnBadRequestWhenInvalid() throws Exception {
        // Arrange
        final Malfunction malfunction = malfunctionRepository.findById(DataUtil.EXISTING_MALFUNCTION_ID).orElseGet(null);
        malfunction.setAssignee(userRepository.findByUsernameIgnoreCase(DataUtil.EXISTING_BUILDING_COMPANY_USERNAME).orElseGet(null));
        malfunctionRepository.save(malfunction);

        final RepairmentCreateDto repairmentCreateDto = new RepairmentCreateDto();

        // Act
        mockMvc.perform(post(BASE_URL + '/' + DataUtil.EXISTING_MALFUNCTION_ID + "/repairment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(repairmentCreateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_COMPANY_USERNAME))))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests create repairment with unauthorized user.
     * This should return forbidden.
     */
    @Test
    public void createRepairmentShouldReturnForbiddenWhenUserIsNotAssignee() throws Exception {
        // Arrange
        final Malfunction malfunction = malfunctionRepository.findById(DataUtil.EXISTING_MALFUNCTION_ID).orElseGet(null);
        malfunction.setAssignee(userRepository.findByUsernameIgnoreCase(DataUtil.EXISTING_BUILDING_COMPANY_USERNAME).orElseGet(null));
        malfunctionRepository.save(malfunction);

        final RepairmentCreateDto repairmentCreateDto = new RepairmentCreateDto();
        repairmentCreateDto.setPrice(10);
        repairmentCreateDto.setRepairDate(new Date());

        // Act
        mockMvc.perform(post(BASE_URL + '/' + DataUtil.EXISTING_MALFUNCTION_ID + "/repairment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(repairmentCreateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isForbidden());
    }
}
