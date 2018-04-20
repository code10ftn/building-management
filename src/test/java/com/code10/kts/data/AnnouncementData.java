package com.code10.kts.data;

import com.code10.kts.model.domain.Announcement;
import com.code10.kts.model.domain.Building;
import com.code10.kts.model.domain.user.User;
import com.code10.kts.model.dto.AnnouncementCreateDto;

/**
 * Announcement testing constants and utility methods.
 */
public class AnnouncementData {

    public static final Long NON_EXISTENT_ANNOUNCEMENT_ID = 100L;

    public static Announcement getValidAnnouncement(User author, Building building) {
        return new Announcement("There are plumbing issues.", author, building);
    }

    public static AnnouncementCreateDto getAnnouncementDto() {
        return new AnnouncementCreateDto("Some issue.");
    }
}
