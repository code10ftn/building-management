package com.code10.kts.controller;

import com.code10.kts.model.domain.Building;
import com.code10.kts.model.domain.WorkArea;
import com.code10.kts.model.domain.user.Company;
import com.code10.kts.model.domain.user.Tenant;
import com.code10.kts.model.domain.user.User;
import com.code10.kts.model.dto.BuildingCreateDto;
import com.code10.kts.model.dto.BuildingResponse;
import com.code10.kts.model.dto.BuildingUpdateDto;
import com.code10.kts.model.dto.SupervisorResponse;
import com.code10.kts.service.BuildingService;
import com.code10.kts.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for managing buildings.
 */
@RestController
@RequestMapping("/api/buildings")
public class BuildingController {

    private final BuildingService buildingService;

    private final UserService userService;

    @Autowired
    public BuildingController(BuildingService buildingService, UserService userService) {
        this.buildingService = buildingService;
        this.userService = userService;
    }

    /**
     * GET /api/buildings
     * Gets all buildings.
     *
     * @param pageable page number
     * @return ResponseEntity with list of all buildings
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity findAll(Pageable pageable) {
        final Page<Building> buildings = buildingService.findAll(pageable);
        return new ResponseEntity<>(buildings.map(BuildingResponse::new), HttpStatus.OK);
    }

    /**
     * GET /api/buildings/ID
     * Gets a building by its ID.
     *
     * @param id building ID
     * @return ResponseEntity with building matching ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity findById(@PathVariable long id) {
        return new ResponseEntity<>(new BuildingResponse(buildingService.findById(id)), HttpStatus.OK);
    }

    /**
     * POST /api/buildings
     * Creates a new building.
     *
     * @param buildingCreateDto DTO with building's information
     * @return ResponseEntity with created building's ID
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity create(@Valid @RequestBody BuildingCreateDto buildingCreateDto) {
        final Building building = buildingService.create(buildingCreateDto.createBuilding());
        return new ResponseEntity<>(building.getId(), HttpStatus.CREATED);
    }

    /**
     * PUT /api/buildings/ID
     * Updates an existing building.
     *
     * @param id                building ID
     * @param buildingUpdateDto DTO with building's updated information
     * @return ResponseEntity with updated building
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity update(@PathVariable long id, @Valid @RequestBody BuildingUpdateDto buildingUpdateDto) {
        User user = null;
        if (buildingUpdateDto.getSupervisorId() != null) {
            user = userService.findById(buildingUpdateDto.getSupervisorId());
            userService.checkCanSupervise(user, id);
        }

        final Building building = buildingService.update(id, buildingUpdateDto.createBuilding(user));
        return new ResponseEntity<>(building, HttpStatus.OK);
    }

    /**
     * DELETE /api/buildings/ID
     * Deletes a building.
     *
     * @param id building ID
     * @return ResponseEntity
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity delete(@PathVariable long id) {
        buildingService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * PUT /api/buildings/ID/assignCompany
     * Adds a company to a building's list of maintenance companies.
     *
     * @param id        building ID
     * @param companyId company ID
     * @return ResponseEntity
     */
    @PutMapping("/{id}/assignCompany")
    @PreAuthorize("@userService.isSupervisorOf(#id)")
    public ResponseEntity assignCompanyToBuilding(@PathVariable long id, @RequestParam long companyId) {
        final Building building = buildingService.findById(id);
        final Company company = userService.findCompanyById(companyId);
        buildingService.assignCompany(building, company);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * PUT /api/buildings/ID/revokeCompany
     * Removes a company from a building's list of maintenance companies.
     *
     * @param id        building ID
     * @param companyId company ID
     * @return ResponseEntity
     */
    @PutMapping("/{id}/revokeCompany")
    @PreAuthorize("@userService.isSupervisorOf(#id)")
    public ResponseEntity revokeCompanyFromBuilding(@PathVariable long id, @RequestParam long companyId) {
        final Building building = buildingService.findById(id);
        final Company company = userService.findCompanyById(companyId);
        buildingService.revokeCompany(building, company);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * GET /api/buildings/addressTaken
     * Checks if address is taken.
     *
     * @param address address
     * @return ResponseEntity with true if taken
     */
    @GetMapping("/addressTaken")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity addressTaken(@RequestParam String address) {
        return new ResponseEntity<>(buildingService.addressTaken(address), HttpStatus.OK);
    }

    /**
     * GET /api/buildings/{id}/potentialSupervisors
     * Gets all potential supervisors for a building.
     * Potential supervisors are all tenants and companies that are in HOUSEKEEPING business.
     *
     * @param id building's id
     * @return ResponseEntity with list of potential supervisors
     */
    @GetMapping("/{id}/potentialSupervisors")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity getSupervisors(@PathVariable long id) {
        final List<Tenant> tenants = buildingService.findById(id).getApartments().stream().flatMap(apartment -> apartment.getTenants().stream()).collect(Collectors.toList());
        final List<Company> companies = userService.findAll().stream().filter(user ->
                user instanceof Company && ((Company) user).getWorkArea().equals(WorkArea.HOUSEKEEPING)
        ).map(user -> (Company) user).collect(Collectors.toList());
        final List<SupervisorResponse> potentialSupervisors = new ArrayList<>();
        potentialSupervisors.addAll(tenants.stream().map(SupervisorResponse::new).collect(Collectors.toList()));
        potentialSupervisors.addAll(companies.stream().map(SupervisorResponse::new).collect(Collectors.toList()));
        return new ResponseEntity<>(potentialSupervisors, HttpStatus.OK);
    }
}
