package com.code10.kts.controller;

import com.code10.kts.controller.exception.ForbiddenException;
import com.code10.kts.model.domain.Apartment;
import com.code10.kts.model.domain.ResidentialRequest;
import com.code10.kts.model.domain.user.Tenant;
import com.code10.kts.model.domain.user.User;
import com.code10.kts.model.dto.TenantResidenceDto;
import com.code10.kts.service.ApartmentService;
import com.code10.kts.service.ResidentialRequestService;
import com.code10.kts.service.TenantService;
import com.code10.kts.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * REST controller for managing buildings' residential requests.
 */
@RestController
@RequestMapping("/api/buildings/{buildingId}/residentialRequests")
public class ResidentialRequestController {

    private final TenantService tenantService;

    private final UserService userService;

    private final ApartmentService apartmentService;

    private final ResidentialRequestService residentialRequestService;

    public ResidentialRequestController(TenantService tenantService, UserService userService, ApartmentService apartmentService, ResidentialRequestService residentialRequestService) {
        this.tenantService = tenantService;
        this.userService = userService;
        this.apartmentService = apartmentService;
        this.residentialRequestService = residentialRequestService;
    }

    /**
     * GET /api/buildings/buildingID/residentialRequests
     * Gets all building's unaccepted residential requests.
     *
     * @return ResponseEntity with list of all building's unaccepted residential requests
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('SUPERVISOR', 'ADMIN')")
    public ResponseEntity findAll() {
        final List<ResidentialRequest> residentialRequests = residentialRequestService.findAllUnaccepted(userService.findCurrentUser());
        return new ResponseEntity<>(residentialRequests, HttpStatus.OK);
    }

    /**
     * GET /api/buildings/buildingID/residentialRequests/ID
     * Gets a building's residential request by tenant's ID.
     *
     * @return ResponseEntity with building's residential request matching tenant's ID
     */
    @GetMapping("/my")
    @PreAuthorize("hasAuthority('TENANT')")
    public ResponseEntity findMyRequest() {
        final User currentUser = userService.findCurrentUser();
        final ResidentialRequest residentialRequest = residentialRequestService.findByTenantId(currentUser.getId());
        return new ResponseEntity<>(residentialRequest, HttpStatus.OK);
    }

    /**
     * POST /api/buildings/buildingID/residentialRequests
     * Creates a new residential request for a building.
     *
     * @param buildingId         building ID
     * @param tenantResidenceDto DTO with the requested apartment number
     * @return ResponseEntity with created residential request's ID
     */
    @PostMapping
    @PreAuthorize("hasAuthority('TENANT')")
    public ResponseEntity create(@PathVariable long buildingId, @Valid @RequestBody TenantResidenceDto tenantResidenceDto) {
        final Apartment apartment = apartmentService
                .findByNumberAndBuildingId(tenantResidenceDto.getApartmentNumber(), buildingId);
        final Tenant tenant = (Tenant) userService.findCurrentUser();
        final ResidentialRequest residentialRequest = residentialRequestService.create(tenant, apartment);

        return new ResponseEntity<>(residentialRequest.getId(), HttpStatus.CREATED);
    }

    /**
     * PUT /api/buildings/buildingID/residentialRequests/ID
     * Accepts a tenant's residential request for a building.
     *
     * @param buildingId building ID
     * @param id         building's residentialRequests ID
     * @return ResponseEntity
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or @userService.isSupervisorOf(#buildingId)")
    public ResponseEntity accept(@PathVariable long buildingId, @PathVariable long id) {
        final ResidentialRequest residentialRequest = residentialRequestService.findById(id);
        tenantService.addResidence(residentialRequest);
        residentialRequestService.removeResidentialRequest(residentialRequest);

        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * DELETE /api/buildings/buildingID/residentialRequests/ID
     * Deletes a building's residential request.
     *
     * @param buildingId building ID
     * @param id         building's residentialRequests ID
     * @return ResponseEntity
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TENANT') or @userService.isSupervisorOf(#buildingId)")
    public ResponseEntity delete(@PathVariable long buildingId, @PathVariable long id) {
        final ResidentialRequest residentialRequest = residentialRequestService.findById(id);
        final User currentUser = userService.findCurrentUser();

        if (currentUser instanceof Tenant && !residentialRequest.getTenant().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Current user doesn't have privileges for this action!");
        }

        residentialRequestService.removeResidentialRequest(residentialRequest);
        return new ResponseEntity(HttpStatus.OK);
    }
}
