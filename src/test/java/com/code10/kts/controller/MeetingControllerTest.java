package com.code10.kts.controller;

import com.code10.kts.data.CustomPageImpl;
import com.code10.kts.data.MeetingData;
import com.code10.kts.model.domain.Building;
import com.code10.kts.model.domain.Meeting;
import com.code10.kts.model.domain.Topic;
import com.code10.kts.model.dto.MeetingCreateDto;
import com.code10.kts.model.dto.MeetingUpdateDto;
import com.code10.kts.model.dto.ReportCreateDto;
import com.code10.kts.model.dto.TopicCommentDto;
import com.code10.kts.model.security.UserDetailsImpl;
import com.code10.kts.repository.BuildingRepository;
import com.code10.kts.repository.MeetingRepository;
import com.code10.kts.security.TokenUtils;
import com.code10.kts.util.DataUtil;
import com.code10.kts.util.DateUtil;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests {@link MeetingController} methods.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
@AutoConfigureMockMvc
@Transactional
public class MeetingControllerTest {

    private static final String BASE_URL = String.format("/api/buildings/%s/meetings", DataUtil.EXISTING_BUILDING_ID);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    /**
     * Test find all building meetings when authenticated as a tenant living in that building.
     * This should return ok and list of building meetings.
     */
    @Test
    public void findAllShouldReturnOkWithBuildingTenant() throws Exception {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        final Meeting meeting1 = new Meeting(DateUtil.DATE_FUTURE, building);
        final Meeting meeting2 = new Meeting(DateUtil.DATE_FUTURE, building);
        meetingRepository.save(meeting1);
        meetingRepository.save(meeting2);

        // Act
        final MvcResult mvcResult = mockMvc.perform(get(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .param("p", "1")
                .param("s", "100")
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        final TypeReference type = new TypeReference<CustomPageImpl<Meeting>>() {
        };
        final CustomPageImpl<Meeting> meetings = JsonUtil.pojo(mvcResult.getResponse().getContentAsString(), type);
        assertEquals(MeetingData.MEETINGS_COUNT, meetings.getTotalElements());
        for (Meeting meeting : meetings) {
            assertEquals(DateUtil.DATE_FUTURE, meeting.getDate());
        }
    }

    /**
     * Test find all building meetings when authenticated as tenant not living in that building.
     * This should return forbidden.
     */
    @Test
    public void findAllShouldReturnForbiddenWithBuildingNonTenant() throws Exception {
        // Act
        mockMvc.perform(get(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_NON_TENANT_USERNAME))))
                .andExpect(status().isForbidden());
    }

    /**
     * Test find all building meetings when authenticated as a company assigned to that building.
     * This should return forbidden.
     */
    @Test
    public void findAllShouldReturnForbiddenWithBuildingCompany() throws Exception {
        // Act
        mockMvc.perform(get(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_COMPANY_USERNAME))))
                .andExpect(status().isForbidden());
    }

    /**
     * Test find all building meetings when authenticated as a company not assigned to that building.
     * This should return forbidden.
     */
    @Test
    public void findAllShouldReturnForbiddenWithBuildingNonCompany() throws Exception {
        // Act
        mockMvc.perform(get(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_NON_COMPANY_USERNAME))))
                .andExpect(status().isForbidden());
    }

    /**
     * Test find meeting by ID when authenticated as a tenant living in that building
     * and meeting with that ID exists.
     * This should return ok and building meeting.
     */
    @Test
    public void findByIdShouldReturnOk() throws Exception {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        Meeting meeting = MeetingData.getMeeting(building, true);
        meeting = meetingRepository.save(meeting);

        // Act
        final MvcResult mvcResult = mockMvc.perform(get(BASE_URL + "/" + meeting.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        final Meeting found = JsonUtil.pojo(mvcResult.getResponse().getContentAsString(), Meeting.class);
        assertNotNull(found);
        assertEquals(DateUtil.DATE_FUTURE, found.getDate());
    }

    /**
     * Test find meeting by ID when authenticated as a tenant living in that building
     * and meeting with that ID doesn't exist.
     * This should return not found.
     */
    @Test
    public void findByIdShouldReturnNotFound() throws Exception {
        // Act
        mockMvc.perform(get(BASE_URL + "/" + MeetingData.NON_EXISTING_MEETING_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isNotFound());
    }

    /**
     * Test find meeting by ID when authenticated as a tenant not living in that building.
     * This should return forbidden.
     */
    @Test
    public void findByIdShouldReturnForbidden() throws Exception {
        // Act
        mockMvc.perform(get(BASE_URL + "/" + MeetingData.NON_EXISTING_MEETING_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_NON_TENANT_USERNAME))))
                .andExpect(status().isForbidden());
    }

    /**
     * Test create meeting with valid data and authenticated as a supervisor of that building.
     * This should return created.
     */
    @Test
    public void createShouldReturnCreatedWhenValidAndAuthorized() throws Exception {
        // Arrange
        final MeetingCreateDto meetingCreateDto = MeetingData.getMeetingCreateDto();

        // Act
        final MvcResult mvcResult = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(meetingCreateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_SUPERVISOR_USERNAME))))
                .andExpect(status().isCreated())
                .andReturn();

        // Assert
        final Long meetingId = JsonUtil.pojo(mvcResult.getResponse().getContentAsString(), Long.class);
        final Meeting meeting = meetingRepository.findByIdAndBuildingId(meetingId, DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        assertNotNull(meeting);
        assertEquals(DateUtil.DATE_FUTURE, meeting.getDate());
    }

    /**
     * Test create meeting with valid data and authenticated as a non building supervisor.
     * This should return forbidden.
     */
    @Test
    public void createShouldReturnForbiddenWhenUnauthorized() throws Exception {
        // Arrange
        final MeetingCreateDto meetingCreateDto = MeetingData.getMeetingCreateDto();

        // Act
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(meetingCreateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isForbidden());
    }

    /**
     * Test create meeting with invalid data (no date) and authenticated as a building supervisor.
     * This should return bad request.
     */
    @Test
    public void createShouldReturnBadRequestWhenInvalid() throws Exception {
        // Arrange
        final MeetingCreateDto meetingCreateDto = new MeetingCreateDto();

        // Act
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(meetingCreateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_SUPERVISOR_USERNAME))))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test update meeting with valid data and authenticated as a supervisor of that building.
     * This should return ok.
     */
    @Test
    public void updateShouldReturnUpdatedWhenValidAndAuthorized() throws Exception {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        Meeting meeting = MeetingData.getMeeting(building, true);
        meeting = meetingRepository.save(meeting);
        final MeetingUpdateDto meetingUpdateDto = new MeetingUpdateDto(DateUtil.DATE_FUTURE);

        // Act
        final MvcResult mvcResult = mockMvc.perform(put(BASE_URL + "/" + meeting.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(meetingUpdateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_SUPERVISOR_USERNAME))))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        final Meeting updated = JsonUtil.pojo(mvcResult.getResponse().getContentAsString(), Meeting.class);
        assertNotNull(updated);
        assertEquals(DateUtil.DATE_FUTURE, updated.getDate());
    }

    /**
     * Test update meeting with valid data and authenticated as a non building supervisor.
     * This should return forbidden.
     */
    @Test
    public void updateShouldReturnForbiddenWhenUnauthorized() throws Exception {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        Meeting meeting = MeetingData.getMeeting(building, true);
        meeting = meetingRepository.save(meeting);
        final MeetingUpdateDto meetingUpdateDto = new MeetingUpdateDto(DateUtil.DATE_FUTURE);

        // Act
        mockMvc.perform(put(BASE_URL + "/" + meeting.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(meetingUpdateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isForbidden());
    }

    /**
     * Test update meeting with invalid data (no date) and authenticated as a building supervisor.
     * This should return bad request.
     */
    @Test
    public void updateShouldReturnBadRequestWhenInvalid() throws Exception {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        Meeting meeting = MeetingData.getMeeting(building, true);
        meeting = meetingRepository.save(meeting);
        final MeetingUpdateDto meetingUpdateDto = new MeetingUpdateDto();

        // Act
        mockMvc.perform(put(BASE_URL + "/" + meeting.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(meetingUpdateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_SUPERVISOR_USERNAME))))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test create meeting report with valid data and authenticated as a supervisor of that building.
     * This should return ok.
     */
    @Test
    public void createReportShouldReturnMeetingWithReportWhenValidAndAuthorized() throws Exception {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        Meeting meeting = MeetingData.getMeeting(building, false);
        meeting = meetingRepository.save(meeting);
        final ReportCreateDto reportCreateDto = MeetingData.getReportCreateDto(meeting);

        // Act
        final MvcResult mvcResult = mockMvc.perform(put(BASE_URL + "/" + meeting.getId() + "/report")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(reportCreateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_SUPERVISOR_USERNAME))))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        final Meeting created = JsonUtil.pojo(mvcResult.getResponse().getContentAsString(), Meeting.class);
        assertNotNull(created);
        assertNotNull(created.getReport());
        assertNotNull(created.getReport().getId());
        for (Topic topic : created.getTopics()) {
            assertEquals(MeetingData.TOPIC_COMMENT, topic.getComment());
        }
    }

    /**
     * Test create meeting report with valid data and authenticated as a non building supervisor.
     * This should return forbidden.
     */
    @Test
    public void createReportShouldReturnForbiddenWhenUnauthorized() throws Exception {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        Meeting meeting = MeetingData.getMeeting(building, false);
        meeting = meetingRepository.save(meeting);
        final ReportCreateDto reportCreateDto = MeetingData.getReportCreateDto(meeting);

        // Act
        mockMvc.perform(put(BASE_URL + "/" + meeting.getId() + "/report")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(reportCreateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isForbidden());
    }

    /**
     * Test create meeting report with invalid data (topic comment with no topic ID)
     * and authenticated as a building supervisor.
     * This should return bad request.
     */
    @Test
    public void createReportShouldReturnBadRequestWhenInvalid() throws Exception {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        Meeting meeting = MeetingData.getMeeting(building, false);
        meeting = meetingRepository.save(meeting);
        final ReportCreateDto reportCreateDto = new ReportCreateDto();
        final List<TopicCommentDto> topicCommentDtos = new ArrayList<>();
        topicCommentDtos.add(new TopicCommentDto(MeetingData.TOPIC_COMMENT));
        reportCreateDto.setComments(topicCommentDtos);

        // Act
        mockMvc.perform(put(BASE_URL + "/" + meeting.getId() + "/report")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(reportCreateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_SUPERVISOR_USERNAME))))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test delete existing meeting authenticated as a supervisor of that building.
     * This should return ok.
     */
    @Test
    public void deleteShouldReturnOkWhenAuthorized() throws Exception {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        Meeting meeting = MeetingData.getMeeting(building, true);
        meeting = meetingRepository.save(meeting);

        // Act
        mockMvc.perform(delete(BASE_URL + "/" + meeting.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_SUPERVISOR_USERNAME))))
                .andExpect(status().isOk());
    }

    /**
     * Test delete existing meeting authenticated as a non building supervisor.
     * This should return forbidden.
     */
    @Test
    public void deleteShouldReturnForbiddenWhenUnauthorized() throws Exception {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        Meeting meeting = MeetingData.getMeeting(building, true);
        meeting = meetingRepository.save(meeting);

        // Act
        mockMvc.perform(delete(BASE_URL + "/" + meeting.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isForbidden());
    }

    /**
     * Test delete non existing meeting authenticated as a building supervisor.
     * This should return bad request.
     */
    @Test
    public void deleteShouldReturnNotFoundWhenNonExistent() throws Exception {
        // Act
        mockMvc.perform(put(BASE_URL + "/" + MeetingData.NON_EXISTING_MEETING_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_SUPERVISOR_USERNAME))))
                .andExpect(status().isBadRequest());
    }
}
