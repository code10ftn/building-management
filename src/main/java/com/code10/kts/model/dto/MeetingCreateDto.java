package com.code10.kts.model.dto;

import com.code10.kts.model.domain.Building;
import com.code10.kts.model.domain.Meeting;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents new meeting.
 */
public class MeetingCreateDto {

    /**
     * Date of the meeting.
     */
    @NotNull
    private Date date;

    /**
     * Meeting's topics.
     */
    @Valid
    private List<TopicCreateDto> topics;

    public MeetingCreateDto() {
    }

    public MeetingCreateDto(Date date, List<TopicCreateDto> topics) {
        this.date = date;
        this.topics = topics;
    }

    /**
     * Creates Meeting from this DTO.
     *
     * @param building building in which this meeting is held
     * @return Meeting model
     */
    public Meeting createMeeting(Building building) {
        final Meeting meeting = new Meeting(date, building, null);
        if (topics != null) {
            meeting.setTopics(topics.stream().map(t -> t.createTopic(meeting)).collect(Collectors.toList()));
        } else {
            meeting.setTopics(new ArrayList<>());
        }
        return meeting;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<TopicCreateDto> getTopics() {
        return topics;
    }

    public void setTopics(List<TopicCreateDto> topics) {
        this.topics = topics;
    }
}