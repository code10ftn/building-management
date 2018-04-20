package com.code10.kts.controller;

import com.code10.kts.model.domain.Building;
import com.code10.kts.model.domain.Malfunction;
import com.code10.kts.model.domain.user.User;
import com.code10.kts.model.dto.AssigneeUpdateDto;
import com.code10.kts.model.dto.MalfunctionCreateDto;
import com.code10.kts.model.dto.MalfunctionUpdateDto;
import com.code10.kts.model.dto.RepairmentCreateDto;
import com.code10.kts.service.BuildingService;
import com.code10.kts.service.MalfunctionService;
import com.code10.kts.service.StorageService;
import com.code10.kts.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

/**
 * REST controller for managing buildings' malfunctions.
 */
@RestController
@RequestMapping("/api/buildings/{buildingId}/malfunctions")
public class MalfunctionController {

    private final MalfunctionService malfunctionService;

    private final BuildingService buildingService;

    private final UserService userService;

    private final StorageService storageService;

    @Autowired
    public MalfunctionController(MalfunctionService malfunctionService,
                                 BuildingService buildingService, UserService userService,
                                 StorageService storageService) {
        this.malfunctionService = malfunctionService;
        this.buildingService = buildingService;
        this.userService = userService;
        this.storageService = storageService;
    }

    /**
     * GET /api/buildings/buildingID/malfunctions
     * Gets all building's malfunctions.
     *
     * @param buildingId building ID
     * @param pageable   page number
     * @return ResponseEntity with list of all building's malfunction
     */
    @GetMapping
    @PreAuthorize("@userService.hasAccessToBuildingMalfunctions(#buildingId)")
    public ResponseEntity findAll(@PathVariable long buildingId, Pageable pageable) {
        final User currentUser = userService.findCurrentUser();
        return new ResponseEntity<>(malfunctionService.findByBuildingId(buildingId, currentUser, pageable), HttpStatus.OK);
    }

    /**
     * GET /api/buildings/buildingID/malfunctions/malfunctionId
     * Gets malfunction by id.
     *
     * @param id         malfunction ID
     * @param buildingId building ID
     * @return ResponseEntity with malfunction
     */
    @GetMapping("/{id}")
    @PreAuthorize("@userService.hasAccessToBuildingMalfunctions(#buildingId)")
    public ResponseEntity findById(@PathVariable long id, @PathVariable long buildingId) {

        return new ResponseEntity<>(malfunctionService.findByIdAndBuildingId(id, buildingId), HttpStatus.OK);
    }

    /**
     * POST /api/buildings/buildingID/malfunctions
     * Creates a new malfunction for a building.
     *
     * @param buildingId           building ID
     * @param malfunctionCreateDto DTO with malfunction's information
     * @return ResponseEntity with created malfunction's ID
     */
    @PostMapping
    @PreAuthorize("@userService.hasAccessTo(#buildingId)")
    public ResponseEntity create(@PathVariable long buildingId, @Valid @RequestBody MalfunctionCreateDto malfunctionCreateDto) {
        final Building building = buildingService.findById(buildingId);
        final User currentUser = userService.findCurrentUser();

        final Malfunction malfunction = malfunctionService.create(malfunctionCreateDto
                .createMalfunction(building, currentUser));

        return new ResponseEntity<>(malfunction.getId(), HttpStatus.CREATED);
    }

    /**
     * PUT /api/buildings/buildingID/malfunction/ID
     * Updates an existing building's malfunction.
     *
     * @param buildingId           building ID
     * @param id                   building's malfunction ID
     * @param malfunctionUpdateDto DTO with malfunction's updated information
     * @return ResponseEntity with updated malfunction
     */
    @PutMapping("/{id}")
    @PreAuthorize("@userService.isSupervisorOf(#buildingId) or @userService.isCreatorOf(@malfunctionService.findById(#id))")
    public ResponseEntity update(@PathVariable long buildingId, @PathVariable long id,
                                 @Valid @RequestBody MalfunctionUpdateDto malfunctionUpdateDto) {
        final Malfunction malfunction = malfunctionService.update(id, buildingId,
                malfunctionUpdateDto.createMalfunction());

        return new ResponseEntity<>(malfunction, HttpStatus.OK);
    }

    /**
     * PUT /api/buildings/buildingID/malfunctions/ID/forward
     * Forwards a malfunction by updating its assignee.
     *
     * @param buildingId        building ID
     * @param id                building's malfunction ID
     * @param assigneeUpdateDto DTO with assignee to whom the malfunction is being forwarded to
     * @return ResponseEntity with updated malfunction containing the new assignee
     */
    @PutMapping("/{id}/forward")
    @PreAuthorize("@userService.isSupervisorOf(#buildingId) or @userService.isCreatorOf(@malfunctionService.findById(#id)) or @userService.isAssigneeOf(@malfunctionService.findById(#id))")
    public ResponseEntity forward(@PathVariable long buildingId, @PathVariable long id, @Valid @RequestBody AssigneeUpdateDto assigneeUpdateDto) {
        final User user = userService.findById(assigneeUpdateDto.getAssigneeId());
        final Malfunction updatedMalfunction = malfunctionService.updateAssignee(id, buildingId, user);

        return new ResponseEntity<>(updatedMalfunction, HttpStatus.OK);
    }

    /**
     * POST /api/buildings/buildingID/malfunctions/ID/repairment
     * Schedules a repairment for a malfunction.
     *
     * @param buildingId          building ID
     * @param id                  building's malfunction ID
     * @param repairmentCreateDto DTO with malfunction's scheduled repairment information.
     * @return ResponseEntity with updated malfunction containing the scheduled repairment
     */
    @PostMapping("/{id}/repairment")
    @PreAuthorize("@userService.isAssigneeOf(@malfunctionService.findById(#id))")
    public ResponseEntity createRepairment(@PathVariable long buildingId, @PathVariable long id,
                                           @Valid @RequestBody RepairmentCreateDto repairmentCreateDto) {
        final User user = userService.findCurrentUser();
        final Malfunction malfunction = malfunctionService.findByIdAndBuildingId(id, buildingId);
        final Malfunction updatedMalfunction = malfunctionService.createRepairment(repairmentCreateDto.createRepairment(user), malfunction.getId());

        return new ResponseEntity<>(updatedMalfunction, HttpStatus.CREATED);
    }

    /**
     * POST /api/buildings/buildingID/malfunctions/ID/image
     * Upload malfunction image.
     *
     * @param id   building's malfunction ID
     * @param file Multipart file
     * @return ResponseEntity with malfunction id
     */
    @PostMapping("/{id}/image")
    @PreAuthorize("@userService.isCreatorOf(@malfunctionService.findById(#id))")
    public ResponseEntity uploadImage(@PathVariable long id, @RequestParam("image") MultipartFile file) {
        malfunctionService.findById(id);
        storageService.store(file, id);

        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    /**
     * GET /api/buildings/buildingID/malfunctions/ID/image
     * Get malfunction image.
     *
     * @param buildingId building ID
     * @param id         building's malfunction ID
     * @return ResponseEntity with malfunction image
     */
    @RequestMapping(value = "/{id}/image", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.IMAGE_JPEG_VALUE})
    @PreAuthorize("@userService.hasAccessToBuildingMalfunctions(#buildingId)")
    public ResponseEntity getImage(@PathVariable long buildingId, @PathVariable long id) {
        malfunctionService.findById(id);
        final byte[] imageBytes = storageService.load(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }
}
