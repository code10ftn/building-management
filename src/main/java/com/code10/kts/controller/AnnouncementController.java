package com.code10.kts.controller;

import com.code10.kts.model.domain.Announcement;
import com.code10.kts.model.domain.Building;
import com.code10.kts.model.domain.user.User;
import com.code10.kts.model.dto.AnnouncementCreateDto;
import com.code10.kts.service.AnnouncementService;
import com.code10.kts.service.BuildingService;
import com.code10.kts.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * REST controller for managing buildings' announcements.
 */
@RestController
@RequestMapping("/api/buildings/{buildingId}/announcements")
public class AnnouncementController {

    private final UserService userService;

    private final BuildingService buildingService;

    private final AnnouncementService announcementService;

    @Autowired
    public AnnouncementController(UserService userService, BuildingService buildingService, AnnouncementService announcementService) {
        this.userService = userService;
        this.buildingService = buildingService;
        this.announcementService = announcementService;
    }

    /**
     * GET /api/buildings/buildingID/announcements
     * Gets all building's announcements.
     *
     * @param buildingId building ID
     * @param pageable   page number
     * @return ResponseEntity with list of all building's announcements
     */
    @GetMapping
    @PreAuthorize("@userService.hasAccessTo(#buildingId)")
    public ResponseEntity findAll(@PathVariable long buildingId, Pageable pageable) {
        return new ResponseEntity<>(announcementService.findAllByBuildingId(buildingId, pageable), HttpStatus.OK);
    }

    /**
     * GET /api/buildings/buildingID/announcements/ID
     * Gets a building's announcement by its ID.
     *
     * @param buildingId building ID
     * @param id         building's announcement ID
     * @return ResponseEntity with building's announcement matching ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("@userService.hasAccessTo(#buildingId)")
    public ResponseEntity findById(@PathVariable long buildingId, @PathVariable long id) {
        return new ResponseEntity<>(announcementService.findByIdAndBuildingId(id, buildingId), HttpStatus.OK);
    }

    /**
     * POST /api/buildings/buildingID/s
     * Creates a new announcement for a building.
     *
     * @param buildingId            building ID
     * @param announcementCreateDto DTO with announcement's information
     * @return ResponseEntity with created announcement's ID
     */
    @PostMapping
    @PreAuthorize("@userService.hasAccessTo(#buildingId)")
    public ResponseEntity create(@PathVariable long buildingId, @Valid @RequestBody AnnouncementCreateDto announcementCreateDto) {
        final User author = userService.findCurrentUser();
        final Building building = buildingService.findById(buildingId);
        final Announcement announcement = announcementService.create(announcementCreateDto.createAnnouncement(author, building));
        return new ResponseEntity<>(announcement.getId(), HttpStatus.CREATED);
    }

    /**
     * DELETE /api/buildings/buildingID/announcements/ID
     * Deletes a building's announcement.
     *
     * @param buildingId building ID
     * @param id         building's announcement ID
     * @return ResponseEntity
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@userService.isSupervisorOf(#buildingId) or @userService.isCreatorOf(@announcementService.findById(#id))")
    public ResponseEntity delete(@PathVariable long buildingId, @PathVariable long id) {
        announcementService.delete(id, buildingId);
        return new ResponseEntity(HttpStatus.OK);
    }
}
