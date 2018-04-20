package com.code10.kts.controller;

import com.code10.kts.model.domain.Building;
import com.code10.kts.model.domain.Survey;
import com.code10.kts.model.domain.user.Tenant;
import com.code10.kts.model.dto.AnswerDto;
import com.code10.kts.model.dto.SurveyCreateDto;
import com.code10.kts.service.BuildingService;
import com.code10.kts.service.SurveyService;
import com.code10.kts.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * REST controller for managing buildings' surveys.
 */
@RestController
@RequestMapping("/api/buildings/{buildingId}/surveys")
public class SurveyController {

    private final BuildingService buildingService;

    private final SurveyService surveyService;

    private final UserService userService;

    @Autowired
    public SurveyController(BuildingService buildingService, SurveyService surveyService, UserService userService) {
        this.buildingService = buildingService;
        this.surveyService = surveyService;
        this.userService = userService;
    }

    /**
     * GET /api/buildings/buildingID/surveys
     * Gets building's active, inactive or all surveys.
     *
     * @param buildingId building ID
     * @param isActive   find criteria, both active and inactive when null
     * @param pageable   page number
     * @return ResponseEntity with list of all building's surveys
     */
    @GetMapping()
    @PreAuthorize("@userService.hasAccessTo(#buildingId)")
    public ResponseEntity findAll(@PathVariable long buildingId, @RequestParam(required = false) Boolean isActive, Pageable pageable) {
        if (isActive == null) {
            return new ResponseEntity<>(surveyService.findByBuildingId(buildingId, pageable), HttpStatus.OK);
        }
        return new ResponseEntity<>(surveyService.findByBuildingIdAndIsActive(buildingId, isActive, pageable), HttpStatus.OK);
    }

    /**
     * GET /api/buildings/buildingID/surveys/ID
     * Gets survey by ID.
     *
     * @param buildingId building ID
     * @param id         building's survey ID
     * @return ResponseEntity with survey
     */
    @GetMapping("/{id}")
    @PreAuthorize("@userService.hasAccessTo(#buildingId)")
    public ResponseEntity findById(@PathVariable long buildingId, @PathVariable long id) {
        return new ResponseEntity<>(surveyService.findByBuildingIdAndId(id, buildingId), HttpStatus.OK);
    }

    /**
     * POST /api/buildings/buildingID/surveys
     * Creates a new survey for a building.
     *
     * @param buildingId      building ID
     * @param surveyCreateDto DTO with survey's information
     * @return ResponseEntity with created survey's ID
     */
    @PostMapping
    @PreAuthorize("@userService.isSupervisorOf(#buildingId)")
    public ResponseEntity create(@PathVariable long buildingId, @Valid @RequestBody SurveyCreateDto surveyCreateDto) {
        final Building building = buildingService.findById(buildingId);
        final Survey survey = surveyService.create(surveyCreateDto.createSurvey(building));
        return new ResponseEntity<>(survey.getId(), HttpStatus.CREATED);
    }

    /**
     * POST /api/buildings/buildingID/surveys/ID/answers
     * Creates a tenant's answer(s) for a survey.
     *
     * @param buildingId building ID
     * @param id         building's survey ID
     * @param answerDto  DTO with chosen answers
     * @return ResponseEntity with survey containing new answers for a tenant
     */
    @PostMapping("/{id}/answers")
    @PreAuthorize("@userService.isTenantOf(#buildingId)")
    public ResponseEntity answer(@PathVariable long buildingId, @PathVariable long id,
                                 @Valid @RequestBody AnswerDto answerDto) {
        final Tenant tenant = (Tenant) userService.findCurrentUser();
        surveyService.addSurveyAnswers(id, buildingId, tenant, answerDto.getAnswerIds());
        return new ResponseEntity<>(surveyService.findByBuildingIdAndId(id, buildingId), HttpStatus.CREATED);
    }
}