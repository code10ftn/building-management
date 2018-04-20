package com.code10.kts.controller;

import com.code10.kts.data.AnnouncementData;
import com.code10.kts.data.BuildingData;
import com.code10.kts.data.CustomPageImpl;
import com.code10.kts.model.domain.Announcement;
import com.code10.kts.model.dto.AnnouncementCreateDto;
import com.code10.kts.model.security.UserDetailsImpl;
import com.code10.kts.repository.AnnouncementRepository;
import com.code10.kts.security.TokenUtils;
import com.code10.kts.service.BuildingService;
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

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests {@link AnnouncementController} methods.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
@AutoConfigureMockMvc
@Transactional
public class AnnouncementControllerTest {

    private static final String BASE_URL = "/api/buildings/" + DataUtil.EXISTING_BUILDING_ID + "/announcements";

    private static final String BAD_BASE_URL = "/api/buildings/" + BuildingData.NON_EXISTENT_BUILDING_ID + "/announcements";

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BuildingService buildingService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenUtils tokenUtils;

    /**
     * Test find all when user is tenant.
     * This should return ok and all announcements.
     */
    @Test
    public void findAllShouldReturnAllAnnouncementsWhenUserHasAccess() throws Exception {
        // Arrange
        final Announcement announcement = AnnouncementData.getValidAnnouncement(userService.findById(DataUtil.EXISTING_TENANT_ID),
                buildingService.findById(DataUtil.EXISTING_BUILDING_ID));
        announcementRepository.save(announcement);

        announcement.setContent("Another issue in the building");
        announcementRepository.save(announcement);

        // Act
        final MvcResult result = mockMvc.perform(get(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .param("p", "1")
                .param("s", "100")
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isOk())
                .andReturn();

        //Assert
        final TypeReference type = new TypeReference<CustomPageImpl<Announcement>>() {
        };
        final CustomPageImpl<Announcement> announcements = JsonUtil.pojo(result.getResponse().getContentAsString(), type);
        assertNotNull(announcements);
        assertTrue(announcements.getTotalElements() > 0);

        for (Announcement n : announcements) {
            assertEquals(DataUtil.EXISTING_BUILDING_ID, n.getBuilding().getId());
        }
    }

    /**
     * Test find all announcements when user doesn't have access to building (i.e. user is not a tenant or a supervisor).
     * This should return Forbidden.
     */
    @Test
    public void findAllShouldReturnForbiddenWhenUserHasNoAccessToBuilding() throws Exception {
        // Act
        mockMvc.perform(get(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_NON_TENANT_USERNAME))))
                .andExpect(status().isForbidden());
    }

    /**
     * Test find by id when user is a tenant of a building.
     * This should return Ok and a announcement with that id.
     */
    @Test
    public void findByIdShouldReturnOkWhenUserHasAccessToBuilding() throws Exception {
        // Arrange
        Announcement announcement = AnnouncementData.getValidAnnouncement(userService.findById(DataUtil.EXISTING_TENANT_ID),
                buildingService.findById(DataUtil.EXISTING_BUILDING_ID));
        announcement = announcementRepository.save(announcement);

        // Act
        final MvcResult result = mockMvc.perform(get(BASE_URL + "/" + announcement.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        final Announcement persisted = JsonUtil.pojo(result.getResponse().getContentAsString(), Announcement.class);
        assertNotNull(persisted);
        assertEquals(announcement.getId(), persisted.getId());
    }

    /**
     * Test find by id when user has no access to a building (i.e. user is a company).
     * This should return Forbidden.
     */
    @Test
    public void findByIdShouldReturnForbiddenWhenUserHasNoAccess() throws Exception {
        // Arrange
        final Announcement announcement = AnnouncementData.getValidAnnouncement(userService.findById(DataUtil.EXISTING_TENANT_ID),
                buildingService.findById(DataUtil.EXISTING_BUILDING_ID));
        final Announcement persisted = announcementRepository.save(announcement);

        // Act
        mockMvc.perform(get(BASE_URL + "/" + persisted.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_NON_COMPANY_USERNAME))))
                .andExpect(status().isForbidden());
    }

    /**
     * Test find by id when building id is invalid.
     * This should throw NotFound.
     */
    @Test
    public void findByIdShouldReturnNotFoundWhenBuildingIdIsInvalid() throws Exception {
        // Arrange
        final Announcement announcement = AnnouncementData.getValidAnnouncement(userService.findById(DataUtil.EXISTING_TENANT_ID),
                buildingService.findById(DataUtil.EXISTING_BUILDING_ID));
        final Announcement persisted = announcementRepository.save(announcement);

        // Act
        mockMvc.perform(get(BAD_BASE_URL + "/" + persisted.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isNotFound());
    }

    /**
     * Test create announcement with valid data.
     * This should return Created.
     */
    @Test
    public void createShouldCreateAnnouncementWhenValid() throws Exception {
        // Arrange
        final AnnouncementCreateDto announcementCreateDto = AnnouncementData.getAnnouncementDto();

        // Act
        final MvcResult mvcResult = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(announcementCreateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isCreated())
                .andReturn();

        // Assert
        final Long announcementId = JsonUtil.pojo(mvcResult.getResponse().getContentAsString(), Long.class);
        final Announcement announcement = announcementRepository.findById(announcementId).orElseGet(null);
        assertNotNull(announcement);
        assertEquals(announcementCreateDto.getContent(), announcement.getContent());
    }

    /**
     * Test create when announcement content is empty.
     * This should return BadRequest.
     */
    @Test
    public void createShouldReturnBadRequestWhenAnnouncementContentIsEmpty() throws Exception {
        // Arrange
        final AnnouncementCreateDto announcementCreateDto = AnnouncementData.getAnnouncementDto();
        announcementCreateDto.setContent("");

        // Act
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(announcementCreateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test create announcement when building id is non existent.
     * This should return NotFound.
     */
    @Test
    public void createShouldReturnNotFoundWhenBuildingIdIsInvalid() throws Exception {
        // Arrange
        final AnnouncementCreateDto announcementCreateDto = AnnouncementData.getAnnouncementDto();

        // Act
        mockMvc.perform(post(BAD_BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(announcementCreateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isNotFound());
    }

    /**
     * Test delete announcement with valid announcement id and user authenticated as a supervisor.
     * This should return Ok.
     */
    @Test
    public void deleteShouldReturnOkWhenUserIsSupervisor() throws Exception {
        // Arrange
        Announcement announcement = AnnouncementData.getValidAnnouncement(userService.findById(DataUtil.EXISTING_TENANT_ID),
                buildingService.findById(DataUtil.EXISTING_BUILDING_ID));
        announcement = announcementRepository.save(announcement);

        // Act
        mockMvc.perform(delete(BASE_URL + "/" + announcement.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_SUPERVISOR_USERNAME))))
                .andExpect(status().isOk());
    }

    /**
     * Test delete announcement with valid announcement id and user authenticated as an author of a announcement.
     * This should return Ok.
     */
    @Test
    public void deleteShouldReturnOkWhenUserIsCreator() throws Exception {
        // Arrange
        Announcement announcement = AnnouncementData.getValidAnnouncement(userService.findById(DataUtil.EXISTING_TENANT_ID),
                buildingService.findById(DataUtil.EXISTING_BUILDING_ID));
        announcement = announcementRepository.save(announcement);

        // Act
        mockMvc.perform(delete(BASE_URL + "/" + announcement.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isOk());
    }

    /**
     * Test delete announcement with valid announcement id and user has no permissions to delete a announcement.
     * This means that user is not a supervisor or an author of a announcement.
     * This should return Forbidden.
     */
    @Test
    public void deleteShouldReturnForbiddenWhenUserIsNotAuthorizedToDelete() throws Exception {
        // Arrange
        Announcement announcement = AnnouncementData.getValidAnnouncement(userService.findById(DataUtil.EXISTING_TENANT_ID),
                buildingService.findById(DataUtil.EXISTING_BUILDING_ID));
        announcement = announcementRepository.save(announcement);

        // Act
        mockMvc.perform(delete(BASE_URL + "/" + announcement.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_NON_TENANT_USERNAME))))
                .andExpect(status().isForbidden());
    }

    /**
     * Test delete announcement with non existent announcement id.
     * This should return NotFound.
     */
    @Test
    public void deleteShouldReturnNotFoundWhenAnnouncementIdIsInvalid() throws Exception {
        // Arrange
        final Announcement announcement = AnnouncementData.getValidAnnouncement(userService.findById(DataUtil.EXISTING_TENANT_ID),
                buildingService.findById(DataUtil.EXISTING_BUILDING_ID));
        announcementRepository.save(announcement);

        // Act
        mockMvc.perform(delete(BASE_URL + "/" + AnnouncementData.NON_EXISTENT_ANNOUNCEMENT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isNotFound());
    }
}
