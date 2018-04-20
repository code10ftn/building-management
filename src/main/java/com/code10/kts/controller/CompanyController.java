package com.code10.kts.controller;

import com.code10.kts.model.domain.Building;
import com.code10.kts.model.domain.Malfunction;
import com.code10.kts.model.domain.user.Company;
import com.code10.kts.model.dto.CompanyRegisterDto;
import com.code10.kts.service.BuildingService;
import com.code10.kts.service.CompanyService;
import com.code10.kts.service.MalfunctionService;
import com.code10.kts.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * REST controller for managing companies.
 */
@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    private final BuildingService buildingService;

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final MalfunctionService malfunctionService;

    @Autowired
    public CompanyController(CompanyService companyService, BuildingService buildingService,
                             UserService userService, PasswordEncoder passwordEncoder,
                             MalfunctionService malfunctionService) {
        this.companyService = companyService;
        this.buildingService = buildingService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.malfunctionService = malfunctionService;
    }

    /**
     * GET /api/companies
     * Gets all companies not assigned to a building yet.
     *
     * @param buildingId building's id
     * @return Response entity with list of companies still unassigned to a building
     */
    @GetMapping
    public ResponseEntity findAllUnassigned(@RequestParam long buildingId) {
        final Building building = buildingService.findById(buildingId);
        final List<Company> companies = companyService.findUnaccepted(building);
        return new ResponseEntity<>(companies, HttpStatus.OK);
    }

    /**
     * POST /api/companies
     * Creates a new company.
     *
     * @param companyRegisterDto DTO with company's information
     * @return ResponseEntity with created company's ID
     */
    @PostMapping
    public ResponseEntity create(@Valid @RequestBody CompanyRegisterDto companyRegisterDto) {
        userService.checkIsUsernameUnique(companyRegisterDto.getUsername());
        companyRegisterDto.setPassword(passwordEncoder.encode(companyRegisterDto.getPassword()));
        final Company company = companyService.create(companyRegisterDto.createCompany());
        return new ResponseEntity<>(company.getId(), HttpStatus.CREATED);
    }

    /**
     * GET api/companies/companyId/malfunctions
     * Get all malfunctions that are assigned to a company.
     *
     * @param id       Company id
     * @param pageable page number
     * @return ResponseEntity with malfunctions
     */
    @GetMapping("/{id}/malfunctions")
    @PreAuthorize("hasAuthority('COMPANY')")
    public ResponseEntity findAllMalfunctions(@PathVariable long id, Pageable pageable) {
        final Page<Malfunction> malfunctions = malfunctionService.findByCompanyId(id, pageable);
        return new ResponseEntity<>(malfunctions, HttpStatus.OK);
    }
}
