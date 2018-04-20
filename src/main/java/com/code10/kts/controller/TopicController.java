package com.code10.kts.controller;

import com.code10.kts.model.domain.Meeting;
import com.code10.kts.model.domain.Topic;
import com.code10.kts.model.domain.user.User;
import com.code10.kts.model.dto.TopicCreateDto;
import com.code10.kts.model.dto.TopicUpdateDto;
import com.code10.kts.service.MeetingService;
import com.code10.kts.service.TopicService;
import com.code10.kts.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * REST controller for managing meeting's topics.
 */
@RestController
@RequestMapping("/api/buildings/{buildingId}/meetings/{meetingId}/topics")
public class TopicController {

    private final MeetingService meetingService;

    private final TopicService topicService;

    private final UserService userService;

    @Autowired
    public TopicController(MeetingService meetingService, TopicService topicService, UserService userService) {
        this.meetingService = meetingService;
        this.topicService = topicService;
        this.userService = userService;
    }

    /**
     * GET /api/buildings/buildingID/meetings/meetingID/topics
     * Gets all meeting's topics.
     *
     * @param buildingId building ID
     * @param meetingId  building's meeting ID
     * @param pageable   page number
     * @return ResponseEntity with list of all meeting's topics
     */
    @GetMapping
    @PreAuthorize("@userService.hasAccessTo(#buildingId)")
    public ResponseEntity findAll(@PathVariable long buildingId, @PathVariable long meetingId, Pageable pageable) {
        return new ResponseEntity<>(topicService.findByMeetingIdAndBuildingId(meetingId, buildingId, pageable), HttpStatus.OK);
    }

    /**
     * GET /api/buildings/buildingID/meetings/meetingID/topics/ID
     * Gets a meeting's topic by its ID.
     *
     * @param buildingId building ID
     * @param meetingId  building's meeting ID
     * @param id         meeting's topic ID
     * @return ResponseEntity with meeting's topic matching ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("@userService.hasAccessTo(#buildingId)")
    public ResponseEntity findById(@PathVariable long buildingId, @PathVariable long meetingId, @PathVariable long id) {
        final Topic topic = topicService.findByIdAndMeetingIdAndBuildingId(id, meetingId, buildingId);
        return new ResponseEntity<>(topic, HttpStatus.OK);
    }

    /**
     * POST /api/buildings/buildingID/meetings/meetingID/topics
     * Creates a new topic for a building.
     *
     * @param buildingId     building ID
     * @param meetingId      building's meeting ID
     * @param topicCreateDto DTO with topic's information
     * @return ResponseEntity with created topics's ID
     */
    @PostMapping
    @PreAuthorize("@userService.hasAccessTo(#buildingId)")
    public ResponseEntity create(@PathVariable long buildingId, @PathVariable long meetingId, @Valid @RequestBody TopicCreateDto topicCreateDto) {
        final Meeting meeting = meetingService.findByIdAndBuildingId(meetingId, buildingId);
        final User user = userService.findCurrentUser();
        final Topic topic = topicService.create(topicCreateDto.createTopic(meeting), user);
        return new ResponseEntity<>(topic.getId(), HttpStatus.CREATED);
    }

    /**
     * PUT /api/buildings/buildingID/meetings/meetingID/topics/ID
     * Updates an existing meeting's topic.
     *
     * @param buildingId     building ID
     * @param meetingId      building's meeting ID
     * @param id             meeting's topic ID
     * @param topicUpdateDto DTO with topic's updated information
     * @return ResponseEntity with updated topics
     */
    @PutMapping("/{id}")
    @PreAuthorize("@userService.isSupervisorOf(#buildingId)")
    public ResponseEntity update(@PathVariable long buildingId, @PathVariable long meetingId, @PathVariable long id,
                                 @Valid @RequestBody TopicUpdateDto topicUpdateDto) {
        final Topic topic = topicService.update(id, meetingId, buildingId, topicUpdateDto.createTopic());
        return new ResponseEntity<>(topic, HttpStatus.OK);
    }

    /**
     * DELETE /api/buildings/buildingID/meetings/meetingID/topics/ID
     * Deletes a meeting's topic.
     *
     * @param buildingId building ID
     * @param meetingId  building's meeting ID
     * @param id         meeting's topic ID
     * @return ResponseEntity
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@userService.isSupervisorOf(#buildingId)")
    public ResponseEntity delete(@PathVariable long buildingId, @PathVariable long meetingId, @PathVariable long id) {
        final Topic topic = topicService.findByIdAndMeetingIdAndBuildingId(id, meetingId, buildingId);
        topicService.delete(topic);
        return new ResponseEntity(HttpStatus.OK);
    }
}
