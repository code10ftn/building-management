package com.code10.kts.model.dto;

import com.code10.kts.model.domain.Announcement;
import com.code10.kts.model.domain.Building;
import com.code10.kts.model.domain.user.User;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Represents new announcement.
 */
public class AnnouncementCreateDto {

    /**
     * Announcement's text.
     */
    @NotEmpty
    private String content;

    public AnnouncementCreateDto() {
    }

    public AnnouncementCreateDto(String content) {
        this.content = content;
    }

    /**
     * Creates Announcement from this DTO.
     *
     * @param user     user that posted this announcement
     * @param building building which this announcement is regarding
     * @return Announcement model
     */
    public Announcement createAnnouncement(User user, Building building) {
        return new Announcement(content, user, building);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
