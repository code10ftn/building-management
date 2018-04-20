package com.code10.kts.controller;

import com.code10.kts.data.CustomPageImpl;
import com.code10.kts.data.MeetingData;
import com.code10.kts.data.TopicData;
import com.code10.kts.model.domain.Building;
import com.code10.kts.model.domain.Meeting;
import com.code10.kts.model.domain.Topic;
import com.code10.kts.model.dto.TopicCreateDto;
import com.code10.kts.model.dto.TopicUpdateDto;
import com.code10.kts.model.security.UserDetailsImpl;
import com.code10.kts.repository.BuildingRepository;
import com.code10.kts.repository.MeetingRepository;
import com.code10.kts.repository.TopicRepository;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests {@link TopicController} methods.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
@AutoConfigureMockMvc
@Transactional
public class TopicControllerTest {

    private static final String BASE_URL = String.format("/api/buildings/%s/meetings", DataUtil.EXISTING_BUILDING_ID);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private TopicRepository topicRepository;

    /**
     * Test find all meeting topics when authenticated as a tenant living in meeting building.
     * This should return ok and list of topics.
     */
    @Test
    public void findAllShouldReturnOkWithBuildingTenants() throws Exception {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        final Meeting meeting = meetingRepository.save(MeetingData.getMeeting(building, true));
        final String url = String.format("%s/%s/topics", BASE_URL, meeting.getId());

        // Act
        final MvcResult mvcResult = mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .param("p", "1")
                .param("s", "100")
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        final TypeReference type = new TypeReference<CustomPageImpl<Topic>>() {
        };
        final CustomPageImpl<Topic> topics = JsonUtil.pojo(mvcResult.getResponse().getContentAsString(), type);
        assertEquals(MeetingData.MEETING_TOPICS_COUNT, topics.getTotalElements());
        for (Topic topic : topics) {
            assertEquals(TopicData.TOPIC_CONTENT, topic.getContent());
        }
    }

    /**
     * Test find all meeting topics when authenticated as a tenant not living in meeting building.
     * This should return forbidden.
     */
    @Test
    public void findAllShouldReturnForbiddenWithBuildingNonTenant() throws Exception {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        final Meeting meeting = meetingRepository.save(MeetingData.getMeeting(building, true));
        final String url = String.format("%s/%s/topics", BASE_URL, meeting.getId());

        // Act
        mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_NON_TENANT_USERNAME))))
                .andExpect(status().isForbidden());
    }

    /**
     * Test find all meeting topics when authenticated as a company assigned to the meeting's building.
     * This should return forbidden.
     */
    @Test
    public void findAllShouldReturnForbiddenWithBuildingCompany() throws Exception {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        final Meeting meeting = meetingRepository.save(MeetingData.getMeeting(building, true));
        final String url = String.format("%s/%s/topics", BASE_URL, meeting.getId());

        // Act
        mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_COMPANY_USERNAME))))
                .andExpect(status().isForbidden());
    }

    /**
     * Test find all meeting topics when authenticated as a company not assigned to the meeting's building.
     * This should return forbidden.
     */
    @Test
    public void findAllShouldReturnForbiddenWithBuildingNonCompany() throws Exception {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        final Meeting meeting = meetingRepository.save(MeetingData.getMeeting(building, true));
        final String url = String.format("%s/%s/topics", BASE_URL, meeting.getId());

        // Act
        mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_NON_COMPANY_USERNAME))))
                .andExpect(status().isForbidden());
    }

    /**
     * Test find by ID when authenticated as a tenant living in meeting's building and topic with ID exists.
     * This should return ok and topic.
     */
    @Test
    public void findByIdShouldReturnOk() throws Exception {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        final Meeting meeting = meetingRepository.save(MeetingData.getMeeting(building, true));
        final Long topicId = meeting.getTopics().get(0).getId();
        final String url = String.format("%s/%s/topics/%s", BASE_URL, meeting.getId(), topicId);

        // Act
        final MvcResult mvcResult = mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        final Topic topic = JsonUtil.pojo(mvcResult.getResponse().getContentAsString(), Topic.class);
        assertNotNull(topic);
        assertEquals(TopicData.TOPIC_CONTENT, topic.getContent());
    }

    /**
     * Test find by ID when authenticated as a tenant living in meeting's building and topic with ID doesn't exist.
     * This should return not found.
     */
    @Test
    public void findByIdShouldReturnNotFound() throws Exception {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        final Meeting meeting = meetingRepository.save(MeetingData.getMeeting(building, true));
        final String url = String.format("%s/%s/topics/%s", BASE_URL, meeting.getId(), TopicData.NON_EXISTING_TOPIC_ID);

        // Act
        mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isNotFound());
    }

    /**
     * Test find by ID when authenticated as a tenant not living in meeting's building.
     * This should return forbidden.
     */
    @Test
    public void findByIdShouldReturnForbidden() throws Exception {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        final Meeting meeting = meetingRepository.save(MeetingData.getMeeting(building, true));
        final Long topicId = meeting.getTopics().get(0).getId();
        final String url = String.format("%s/%s/topics/%s", BASE_URL, meeting.getId(), topicId);

        // Act
        mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_NON_TENANT_USERNAME))))
                .andExpect(status().isForbidden());
    }

    /**
     * Test create topic with valid data and authenticated as a building tenant.
     * This should return created.
     */
    @Test
    public void createShouldReturnCreatedWhenValidAndAuthorized() throws Exception {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        final Meeting meeting = meetingRepository.save(MeetingData.getMeeting(building, true));
        final TopicCreateDto topicCreateDto = new TopicCreateDto(TopicData.TOPIC_CONTENT);
        final String url = String.format("%s/%s/topics", BASE_URL, meeting.getId());

        // Act
        final MvcResult mvcResult = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(topicCreateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isCreated())
                .andReturn();

        // Assert
        final Long topicId = JsonUtil.pojo(mvcResult.getResponse().getContentAsString(), Long.class);
        final Topic topic = topicRepository.findByIdAndMeetingIdAndBuildingId(topicId, meeting.getId(), building.getId()).orElseGet(null);
        assertNotNull(topic);
        assertEquals(TopicData.TOPIC_CONTENT, topic.getContent());
    }

    /**
     * Test create topic with valid data and authenticated as a non building tenant.
     * This should return forbidden.
     */
    @Test
    public void createShouldReturnForbiddenWhenUnauthorized() throws Exception {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        final Meeting meeting = meetingRepository.save(MeetingData.getMeeting(building, true));
        final TopicCreateDto topicCreateDto = new TopicCreateDto(TopicData.TOPIC_CONTENT);
        final String url = String.format("%s/%s/topics", BASE_URL, meeting.getId());

        // Act
        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(topicCreateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_NON_TENANT_USERNAME))))
                .andExpect(status().isForbidden());
    }

    /**
     * Test create meeting with invalid data (no content) and authenticated as a building tenant.
     * This should return bad request.
     */
    @Test
    public void createShouldReturnBadRequestWhenInvalid() throws Exception {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        final Meeting meeting = meetingRepository.save(MeetingData.getMeeting(building, true));
        final TopicCreateDto topicCreateDto = new TopicCreateDto("");
        final String url = String.format("%s/%s/topics", BASE_URL, meeting.getId());

        // Act
        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(topicCreateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test update topic with valid data and authenticated as a building supervisor.
     * This should return ok.
     */
    @Test
    public void updateShouldReturnUpdatedWhenValidAndAuthorized() throws Exception {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        final Meeting meeting = meetingRepository.save(MeetingData.getMeeting(building, true));
        final TopicUpdateDto topicUpdateDto = new TopicUpdateDto(TopicData.TOPIC_CONTENT_UPDATED, true);
        final Long topicId = meeting.getTopics().get(0).getId();
        final String url = String.format("%s/%s/topics/%s", BASE_URL, meeting.getId(), topicId);

        // Act
        final MvcResult mvcResult = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(topicUpdateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_SUPERVISOR_USERNAME))))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        final Topic updated = JsonUtil.pojo(mvcResult.getResponse().getContentAsString(), Topic.class);
        assertNotNull(updated);
        assertEquals(TopicData.TOPIC_CONTENT_UPDATED, updated.getContent());
    }

    /**
     * Test update topic with valid data and authenticated as a non building supervisor.
     * This should return forbidden.
     */
    @Test
    public void updateShouldReturnForbiddenWhenUnauthorized() throws Exception {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        final Meeting meeting = meetingRepository.save(MeetingData.getMeeting(building, true));
        final TopicUpdateDto topicUpdateDto = new TopicUpdateDto(TopicData.TOPIC_CONTENT_UPDATED, true);
        final Long topicId = meeting.getTopics().get(0).getId();
        final String url = String.format("%s/%s/topics/%s", BASE_URL, meeting.getId(), topicId);

        // Act
        mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.json(topicUpdateDto))
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isForbidden());
    }

    /**
     * Test delete existing topic authenticated as a supervisor of building.
     * This should return ok.
     */
    @Test
    public void deleteShouldReturnOkWhenAuthorized() throws Exception {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        final Meeting meeting = meetingRepository.save(MeetingData.getMeeting(building, true));
        final Long topicId = meeting.getTopics().get(0).getId();
        final String url = String.format("%s/%s/topics/%s", BASE_URL, meeting.getId(), topicId);

        // Act
        mockMvc.perform(delete(url)
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
        final Meeting meeting = meetingRepository.save(MeetingData.getMeeting(building, true));
        final Long topicId = meeting.getTopics().get(0).getId();
        final String url = String.format("%s/%s/topics/%s", BASE_URL, meeting.getId(), topicId);

        // Act
        mockMvc.perform(delete(url)
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_TENANT_USERNAME))))
                .andExpect(status().isForbidden());
    }

    /**
     * Test delete non existing topic authenticated as a building supervisor.
     * This should return not found.
     */
    @Test
    public void deleteShouldReturnNotFoundWhenNonExistent() throws Exception {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        final Meeting meeting = meetingRepository.save(MeetingData.getMeeting(building, true));
        final String url = String.format("%s/%s/topics/%s", BASE_URL, meeting.getId(), TopicData.NON_EXISTING_TOPIC_ID);

        // Act
        mockMvc.perform(delete(url)
                .contentType(MediaType.APPLICATION_JSON)
                .header(DataUtil.AUTH_HEADER, tokenUtils.generateToken(new UserDetailsImpl(DataUtil.EXISTING_BUILDING_SUPERVISOR_USERNAME))))
                .andExpect(status().isNotFound());
    }
}
