package com.code10.kts.model.dto;

import com.code10.kts.model.domain.Meeting;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Represents update of a meeting.
 */
public class MeetingUpdateDto {

    /**
     * Meeting's new date.
     */
    @NotNull
    private Date date;

    public MeetingUpdateDto() {
    }

    public MeetingUpdateDto(Date date) {
        this.date = date;
    }

    /**
     * Creates Meeting from this DTO.
     *
     * @return Meeting model
     */
    public Meeting createMeeting() {
        final Meeting meeting = new Meeting();
        meeting.setDate(date);
        return meeting;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
