package com.code10.kts.service;

import com.code10.kts.controller.exception.NotFoundException;
import com.code10.kts.model.domain.Announcement;
import com.code10.kts.repository.AnnouncementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service layer for announcements' business logic.
 */
@Service
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    @Autowired
    public AnnouncementService(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    /**
     * Gets a announcement by its ID.
     *
     * @param id announcement ID
     * @return announcement with matching ID
     */
    public Announcement findById(long id) {
        return announcementRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("No announcement with ID %s found!", id)));
    }

    /**
     * Gets a building's announcement by its ID.
     *
     * @param id         announcement ID
     * @param buildingId building ID
     * @return building's announcement with matching ID
     */
    public Announcement findByIdAndBuildingId(long id, long buildingId) {
        return announcementRepository.findByIdAndBuildingId(id, buildingId)
                .orElseThrow(() -> new NotFoundException(String.format("No announcement with ID %s and building ID %s found!", id, buildingId)));
    }

    /**
     * Gets all building's announcements.
     *
     * @param buildingId building ID
     * @param pageable   page number
     * @return one page of building's announcements
     */
    public Page<Announcement> findAllByBuildingId(long buildingId, Pageable pageable) {
        return announcementRepository.findAllByBuildingIdOrderByTimestampDesc(buildingId, pageable);
    }

    /**
     * Creates a new announcement for a building.
     *
     * @param announcement announcement to create
     * @return created announcement
     */
    public Announcement create(Announcement announcement) {
        return announcementRepository.save(announcement);
    }

    /**
     * Deletes a building's announcement.
     *
     * @param announcementId announcement ID
     * @param buildingId     building ID
     */
    public void delete(long announcementId, long buildingId) {
        findByIdAndBuildingId(announcementId, buildingId);
        announcementRepository.delete(announcementId);
    }
}
