package com.code10.kts.service;

import com.code10.kts.controller.exception.BadRequestException;
import com.code10.kts.data.MeetingData;
import com.code10.kts.model.domain.Building;
import com.code10.kts.model.domain.Meeting;
import com.code10.kts.model.domain.Topic;
import com.code10.kts.repository.BuildingRepository;
import com.code10.kts.repository.MeetingRepository;
import com.code10.kts.util.DataUtil;
import com.code10.kts.util.DateUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests {@link MeetingService} methods.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
@Transactional
public class MeetingServiceTest {

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private MeetingService meetingService;

    /**
     * Test create meeting with valid data.
     */
    @Test
    public void createShouldReturnCreatedMeetingWhenValid() {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        final Meeting meeting = MeetingData.getMeeting(building, true);

        // Act
        final Meeting created = meetingService.create(meeting);

        // Assert
        assertNotNull(created);
        assertEquals(DateUtil.DATE_FUTURE, created.getDate());
        assertNotNull(created.getBuilding());
        assertEquals(DataUtil.EXISTING_BUILDING_ID, created.getBuilding().getId());
        assertNotNull(created.getTopics());
        assertEquals(MeetingData.MEETING_TOPICS_COUNT, created.getTopics().size());
    }

    /**
     * Test create meeting with invalid data (no building).
     */
    @Test(expected = DataIntegrityViolationException.class)
    public void createShouldThrowExceptionWhenInvalid() {
        // Arrange
        final Meeting meeting = new Meeting();
        meeting.setDate(DateUtil.DATE_FUTURE);

        // Act
        meetingService.create(meeting);
    }

    /**
     * Test create meeting for passed date.
     */
    @Test(expected = BadRequestException.class)
    public void createShouldThrowExceptionWhenDatePassed() {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        final Meeting meeting = new Meeting(DateUtil.DATE_PAST, building);

        // Act
        meetingService.create(meeting);
    }

    /**
     * Test update meeting with valid data.
     */
    @Test
    public void updateShouldReturnUpdatedMeetingWhenValid() {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        Meeting meeting = MeetingData.getMeeting(building, false);
        meeting = meetingRepository.save(meeting);
        meeting.setDate(DateUtil.DATE_FUTURE);

        // Act
        final Meeting updated = meetingService.update(meeting.getId(), DataUtil.EXISTING_BUILDING_ID, meeting);

        // Assert
        assertNotNull(updated);
        assertEquals(DateUtil.DATE_FUTURE, updated.getDate());
        assertNotNull(updated.getBuilding());
        assertEquals(DataUtil.EXISTING_BUILDING_ID, updated.getBuilding().getId());
        assertNotNull(updated.getTopics());
        assertEquals(MeetingData.MEETING_TOPICS_COUNT, updated.getTopics().size());
    }

    /**
     * Test update meeting with invalid data (no building).
     */
    @Test(expected = DataIntegrityViolationException.class)
    public void updateShouldThrowExceptionWhenInvalid() {
        // Arrange
        Meeting meeting = MeetingData.getMeeting(null, true);
        meeting = meetingRepository.save(meeting);

        // Act
        meetingService.update(meeting.getId(), DataUtil.EXISTING_BUILDING_ID, meeting);
    }

    /**
     * Test update meeting with passed date.
     */
    @Test(expected = BadRequestException.class)
    public void updateShouldThrowExceptionWhenDatePassed() {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        Meeting meeting = MeetingData.getMeeting(building, true);
        meeting = meetingRepository.save(meeting);
        meeting.setDate(DateUtil.DATE_PAST);

        // Act
        meetingService.update(meeting.getId(), DataUtil.EXISTING_BUILDING_ID, meeting);
    }

    /**
     * Test create meeting report with valid data.
     */
    @Test
    public void createReportWhenValid() {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        Meeting meeting = MeetingData.getMeeting(building, false);
        meeting = meetingRepository.save(meeting);

        final List<Topic> comments = new ArrayList<>();
        comments.add(new Topic(meeting.getTopics().get(0).getId(), MeetingData.TOPIC_COMMENT));
        comments.add(new Topic(meeting.getTopics().get(1).getId(), MeetingData.TOPIC_COMMENT));

        // Act
        final Meeting meetingWithReport = meetingService.createReport(meeting, comments, null, null, null);

        // Assert
        assertNotNull(meetingWithReport);
        assertNotNull(meetingWithReport.getId());
        assertEquals(meeting.getId(), meetingWithReport.getId());
        for (Topic topic : meetingWithReport.getTopics()) {
            assertNotNull(topic.getComment());
            assertEquals(MeetingData.TOPIC_COMMENT, topic.getComment());
        }
    }

    /**
     * Test create meeting report for future meeting.
     */
    @Test(expected = BadRequestException.class)
    public void createReportShouldThrowExceptionWhenDateNotPassed() {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        final Meeting meeting = MeetingData.getMeeting(building, true);

        // Act
        meetingService.createReport(meeting, null, null, null, null);
    }

    /**
     * Test find date from previous meeting with valid data.
     */
    @Test
    public void findPreviousMeetingDateWhenValid() {
        // Arrange
        final Building building = buildingRepository.findById(DataUtil.EXISTING_BUILDING_ID).orElseGet(null);
        Meeting pastMeeting = MeetingData.getMeeting(building, false);
        Meeting futureMeeting = MeetingData.getMeeting(building, true);
        meetingRepository.save(pastMeeting);
        futureMeeting = meetingRepository.save(futureMeeting);

        // Act
        final Date previousMeetingDate = meetingService.findPreviousMeetingDate(futureMeeting);

        // Assert
        assertNotNull(previousMeetingDate);
        assertEquals(pastMeeting.getDate(), previousMeetingDate);
    }

    /**
     * Test find date from previous meeting from earliest meeting.
     */
    @Test
    public void findPreviousMeetingDateWhenEarliestMeeting() {
        // Arrange
        final Meeting pastMeeting = meetingRepository.findByIdAndBuildingId(1, DataUtil.EXISTING_BUILDING_ID).orElseGet(null);

        // Act
        final Date previousMeetingDate = meetingService.findPreviousMeetingDate(pastMeeting);

        // Assert
        assertNotNull(previousMeetingDate);
        assertEquals(new Date(0), previousMeetingDate);
    }
}
