package com.code10.kts.service;

import com.code10.kts.controller.exception.NotFoundException;
import com.code10.kts.data.MeetingData;
import com.code10.kts.data.TopicData;
import com.code10.kts.model.domain.Building;
import com.code10.kts.model.domain.Meeting;
import com.code10.kts.model.domain.Topic;
import com.code10.kts.model.domain.user.Role;
import com.code10.kts.model.domain.user.Tenant;
import com.code10.kts.model.domain.user.User;
import com.code10.kts.repository.BuildingRepository;
import com.code10.kts.repository.MeetingRepository;
import com.code10.kts.util.DataUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

/**
 * Tests {@link TopicService} methods.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
@Transactional
public class TopicServiceTest {

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private TopicService topicService;

    /**
     * Test find by meeting ID and building ID should return all meeting's topics.
     * This should return list of topics.
     */
    @Test
    public void findByMeetingIdAndBuildingIdShouldReturnTopics() {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        final Meeting meeting = meetingRepository.save(MeetingData.getMeeting(building, true));

        // Act
        final Page<Topic> topics = topicService.findByMeetingIdAndBuildingId(meeting.getId(), building.getId(), new PageRequest(0, 100));

        // Assert
        assertNotNull(topics);
        assertEquals(MeetingData.MEETING_TOPICS_COUNT, topics.getTotalElements());
    }

    /**
     * Test find topic by ID and meeting ID and building ID.
     * This should return topic.
     */
    @Test
    public void findByIdAndMeetingIdAndBuildingIdShouldReturnTopic() {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        final Meeting meeting = meetingRepository.save(MeetingData.getMeeting(building, true));
        final Long topicId = meeting.getTopics().get(0).getId();

        // Act
        final Topic topic = topicService.findByIdAndMeetingIdAndBuildingId(topicId, meeting.getId(), building.getId());

        // Assert
        assertNotNull(topic);
        assertEquals(topicId, topic.getId());
        assertEquals(meeting.getId(), topic.getMeeting().getId());
        assertEquals(TopicData.TOPIC_CONTENT, topic.getContent());
    }

    /**
     * Test find topic by ID and meeting ID and building ID with non existent topic ID.
     * This should throw exception.
     */
    @Test(expected = NotFoundException.class)
    public void findByIdAndMeetingIdAndBuildingIdShouldThrowExceptionWhenInvalid() {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        final Meeting meeting = meetingRepository.save(MeetingData.getMeeting(building, true));

        // Act
        topicService.findByIdAndMeetingIdAndBuildingId(TopicData.NON_EXISTING_TOPIC_ID, meeting.getId(), building.getId());
    }

    /**
     * Test create topic with valid data.
     * This should return created topic.
     */
    @Test
    public void createShouldReturnTopicWhenValid() {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        final Meeting meeting = meetingRepository.save(MeetingData.getMeeting(building, true));
        final Topic topic = new Topic(TopicData.TOPIC_CONTENT, true, meeting);
        final User user = new Tenant();

        // Act
        final Topic created = topicService.create(topic, user);

        assertNotNull(created);
        assertEquals(meeting.getId(), created.getMeeting().getId());
        assertEquals(TopicData.TOPIC_CONTENT, created.getContent());
    }

    /**
     * Test create topic with invalid data (no content).
     * This should return created topic.
     */
    @Test(expected = DataIntegrityViolationException.class)
    public void createShouldThrowExceptionWhenInvalid() {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        final Meeting meeting = meetingRepository.save(MeetingData.getMeeting(building, true));
        final Topic topic = new Topic(TopicData.TOPIC_CONTENT, true, meeting);
        final User user = new Tenant();
        user.getAuthorities().add(Role.SUPERVISOR);
        topic.setContent(null);

        // Act
        topicService.create(topic, user);
    }

    /**
     * Test update topic with valid data.
     * This should return updated topic.
     */
    @Test
    public void updateShouldReturnUpdatedTopicWhenValid() {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        final Meeting meeting = meetingRepository.save(MeetingData.getMeeting(building, true));
        final Topic topic = meeting.getTopics().get(0);
        topic.setAccepted(false);
        topic.setContent(TopicData.TOPIC_CONTENT_UPDATED);

        // Act
        final Topic updated = topicService.update(topic.getId(), meeting.getId(), building.getId(), topic);

        // Assert
        assertNotNull(updated);
        assertEquals(meeting.getId(), updated.getMeeting().getId());
        assertFalse(updated.isAccepted());
        assertEquals(TopicData.TOPIC_CONTENT_UPDATED, updated.getContent());
    }

    /**
     * Test update topic with invalid data (no content).
     * This should return updated topic.
     */
    @Test(expected = DataIntegrityViolationException.class)
    public void updateShouldThrowExceptionWhenInvalid() {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        final Meeting meeting = meetingRepository.save(MeetingData.getMeeting(building, true));
        final Topic topic = meeting.getTopics().get(0);
        topic.setContent(null);

        // Act
        topicService.update(topic.getId(), meeting.getId(), building.getId(), topic);
    }
}
