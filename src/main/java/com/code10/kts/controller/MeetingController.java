package com.code10.kts.controller;

import com.code10.kts.model.domain.*;
import com.code10.kts.model.dto.MeetingCreateDto;
import com.code10.kts.model.dto.MeetingUpdateDto;
import com.code10.kts.model.dto.ReportCreateDto;
import com.code10.kts.service.BuildingService;
import com.code10.kts.service.MalfunctionService;
import com.code10.kts.service.MeetingService;
import com.code10.kts.service.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * REST controller for managing buildings' meetings.
 */
@RestController
@RequestMapping("/api/buildings/{buildingId}/meetings")
public class MeetingController {

    private final BuildingService buildingService;

    private final MeetingService meetingService;

    private final MalfunctionService malfunctionService;

    private final SurveyService surveyService;

    @Autowired
    public MeetingController(BuildingService buildingService, MeetingService meetingService,
                             MalfunctionService malfunctionService, SurveyService surveyService) {
        this.buildingService = buildingService;
        this.meetingService = meetingService;
        this.malfunctionService = malfunctionService;
        this.surveyService = surveyService;
    }

    /**
     * GET /api/buildings/buildingID/meetings
     * Gets all building's meetings.
     *
     * @param buildingId building ID
     * @param pageable   page number
     * @return ResponseEntity with list of all building's meetings
     */
    @GetMapping
    @PreAuthorize("@userService.hasAccessTo(#buildingId)")
    public ResponseEntity findAll(@PathVariable long buildingId, Pageable pageable) {
        return new ResponseEntity<>(meetingService.findByBuildingId(buildingId, pageable), HttpStatus.OK);
    }

    /**
     * GET /api/buildings/buildingID/meetings/ID
     * Gets a building's meeting by its ID.
     *
     * @param buildingId building ID
     * @param id         building's meeting ID
     * @return ResponseEntity with building's meeting matching ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("@userService.hasAccessTo(#buildingId)")
    public ResponseEntity findById(@PathVariable long buildingId, @PathVariable long id) {
        final Meeting meeting = meetingService.findByIdAndBuildingId(id, buildingId);
        final Report report = meeting.getReport();
        if (report != null) {
            List<Malfunction> malfunctions = malfunctionService.findByReportId(report.getId());
            List<Survey> surveys = surveyService.findByReportId(report.getId());
            report.setMalfunctions(malfunctions);
            report.setSurveys(surveys);
        }
        return new ResponseEntity<>(meeting, HttpStatus.OK);
    }

    /**
     * POST /api/buildings/buildingID/meetings
     * Creates a new meeting for a building.
     *
     * @param buildingId       building ID
     * @param meetingCreateDto DTO with meeting's information
     * @return ResponseEntity with created meeting's ID
     */
    @PostMapping
    @PreAuthorize("@userService.isSupervisorOf(#buildingId)")
    public ResponseEntity create(@PathVariable long buildingId, @Valid @RequestBody MeetingCreateDto meetingCreateDto) {
        final Building building = buildingService.findById(buildingId);
        final Meeting meeting = meetingService.create(meetingCreateDto.createMeeting(building));
        return new ResponseEntity<>(meeting.getId(), HttpStatus.CREATED);
    }

    /**
     * PUT /api/buildings/buildingID/meetings/ID
     * Updates an existing building's meeting.
     *
     * @param buildingId       building ID
     * @param id               building's meeting ID
     * @param meetingUpdateDto DTO with meeting's updated information
     * @return ResponseEntity with updated meeting
     */
    @PutMapping("/{id}")
    @PreAuthorize("@userService.isSupervisorOf(#buildingId)")
    public ResponseEntity update(@PathVariable long buildingId, @PathVariable long id, @Valid @RequestBody MeetingUpdateDto meetingUpdateDto) {
        final Meeting meeting = meetingService.update(id, buildingId, meetingUpdateDto.createMeeting());
        return new ResponseEntity<>(meeting, HttpStatus.OK);
    }

    /**
     * PUT /api/buildings/buildingID/meetings/ID
     * Creates a report for building's meeting.
     *
     * @param buildingId      building ID
     * @param id              building's meeting ID
     * @param reportCreateDto DTO with meeting's report information
     * @return ResponseEntity with meeting containing its report
     */
    @PutMapping("/{id}/report")
    @PreAuthorize("@userService.isSupervisorOf(#buildingId)")
    public ResponseEntity createReport(@PathVariable long buildingId, @PathVariable long id, @Valid @RequestBody ReportCreateDto reportCreateDto) {
        Meeting meeting = meetingService.findByIdAndBuildingId(id, buildingId);
        final Date previousMeetingDate = meetingService.findPreviousMeetingDate(meeting);
        final List<Malfunction> malfunctions = malfunctionService.findByBuildingIdAndDateBetween(buildingId, previousMeetingDate, meeting.getDate());
        final List<Survey> surveys = surveyService.findByBuildingIdAndDateBetween(buildingId, previousMeetingDate, meeting.getDate());

        meeting = meetingService.createReport(meeting, reportCreateDto.createTopics(), malfunctions, surveys, reportCreateDto.getComment());
        return new ResponseEntity<>(meeting, HttpStatus.OK);
    }

    /**
     * DELETE /api/buildings/buildingID/meetings/ID
     * Deletes a building's meeting.
     *
     * @param buildingId building ID
     * @param id         building's meeting ID
     * @return ResponseEntity
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@userService.isSupervisorOf(#buildingId)")
    public ResponseEntity delete(@PathVariable long buildingId, @PathVariable long id) {
        meetingService.findByIdAndBuildingId(id, buildingId);
        meetingService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
