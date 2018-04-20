package com.code10.kts.service;

import com.code10.kts.controller.exception.BadRequestException;
import com.code10.kts.controller.exception.NotFoundException;
import com.code10.kts.model.domain.*;
import com.code10.kts.repository.MalfunctionRepository;
import com.code10.kts.repository.MeetingRepository;
import com.code10.kts.repository.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for meetings' business logic.
 */
@Service
public class MeetingService {

    private final MeetingRepository meetingRepository;

    private final MalfunctionRepository malfunctionRepository;

    private final SurveyRepository surveyRepository;

    @Autowired
    public MeetingService(MeetingRepository meetingRepository, MalfunctionRepository malfunctionRepository, SurveyRepository surveyRepository) {
        this.meetingRepository = meetingRepository;
        this.malfunctionRepository = malfunctionRepository;
        this.surveyRepository = surveyRepository;
    }

    /**
     * Gets all building's meetings.
     *
     * @param buildingId building ID
     * @param pageable   page number
     * @return one page of building's meetings
     */
    public Page<Meeting> findByBuildingId(long buildingId, Pageable pageable) {
        return meetingRepository.findByBuildingId(buildingId, pageable);
    }

    /**
     * Gets a building's meeting by its ID.
     *
     * @param id         meeting ID
     * @param buildingId building ID
     * @return building's meeting with matching ID
     */
    public Meeting findByIdAndBuildingId(long id, long buildingId) {
        return meetingRepository.findByIdAndBuildingId(id, buildingId)
                .orElseThrow(() -> new NotFoundException(String.format("No meeting with ID %s found in building with ID %s!", id, buildingId)));
    }

    /**
     * Creates a new meeting for a building.
     *
     * @param meeting meeting to create
     * @return created meeting
     */
    public Meeting create(Meeting meeting) {
        if (meeting.getDate() == null || meeting.datePassed()) {
            throw new BadRequestException("Meeting date passed!");
        }
        return meetingRepository.save(meeting);
    }

    /**
     * Updates an existing building's meeting.
     *
     * @param id         meeting ID
     * @param buildingId building ID
     * @param meeting    meeting to update
     * @return updated meeting
     */
    public Meeting update(long id, long buildingId, Meeting meeting) {
        if (meeting.datePassed()) {
            throw new BadRequestException("Meeting date passed!");
        }
        final Meeting persisted = findByIdAndBuildingId(id, buildingId);
        persisted.setDate(meeting.getDate());
        return meetingRepository.save(persisted);
    }

    /**
     * Creates a report for building's meeting.
     *
     * @param meeting       meeting for which report is created
     * @param comments      list of comments for meeting's topics
     * @param malfunctions  list of malfunctions that occurred since last meeting
     * @param surveys       list of surveys that were answered since last meeting
     * @param reportComment overall report comment
     * @return meeting containing its report
     */
    public Meeting createReport(Meeting meeting, List<Topic> comments, List<Malfunction> malfunctions, List<Survey> surveys,
                                String reportComment) {
        if (!meeting.datePassed()) {
            throw new BadRequestException("Meeting hasn't passed yet!");
        }

        // Add topic comments.
        for (Topic topic : meeting.getTopics()) {
            if (!topic.isAccepted()) {
                continue;
            }

            final Optional<Topic> comment = comments.stream().filter(t -> t.getId().equals(topic.getId())).findFirst();
            comment.ifPresent(c -> topic.setComment(c.getComment()));
        }

        // Create report with malfunctions and surveys that happened between last and current meeting.
        if (meeting.getReport() == null) {
            final Report report = new Report(meeting, malfunctions, surveys, reportComment);
            meeting.setReport(report);
        } else {
            final Report report = meeting.getReport();
            report.setMalfunctions(malfunctions);
            report.setSurveys(surveys);
            report.setComment(reportComment);
        }

        meeting = meetingRepository.save(meeting);

        if (malfunctions != null) {
            for (Malfunction malfunction : malfunctions) {
                malfunction.setReport(meeting.getReport());
                malfunctionRepository.save(malfunction);
            }
        }
        if (surveys != null) {
            for (Survey survey : surveys) {
                survey.setReport(meeting.getReport());
                surveyRepository.save(survey);
            }
        }

        return meeting;
    }

    /**
     * Deletes a building's meeting.
     *
     * @param id meeting ID
     */
    public void delete(long id) {
        meetingRepository.delete(id);
    }

    /**
     * Gets a date of previous meeting.
     *
     * @param current current meeting
     * @return date of previous meeting
     */
    public Date findPreviousMeetingDate(Meeting current) {
        final List<Meeting> meetings = meetingRepository.findAll();
        meetings.sort(Comparator.comparing(Meeting::getDate));

        final int indexOfCurrent = meetings.indexOf(current);
        if (indexOfCurrent > 0) {
            return meetings.get(indexOfCurrent - 1).getDate();
        }
        return new Date(0);
    }
}
