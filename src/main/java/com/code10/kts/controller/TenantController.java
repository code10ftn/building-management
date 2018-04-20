package com.code10.kts.controller;

import com.code10.kts.model.domain.user.Tenant;
import com.code10.kts.model.dto.TenantRegisterDto;
import com.code10.kts.service.TenantService;
import com.code10.kts.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * REST controller for managing tenants.
 */
@RestController
@RequestMapping("/api/tenants")
public class TenantController {

    private final TenantService tenantService;

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public TenantController(TenantService tenantService, UserService userService, PasswordEncoder passwordEncoder) {
        this.tenantService = tenantService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * POST /api/tenants
     * Creates a new tenant.
     *
     * @param tenantRegisterDto DTO with tenant's information
     * @return ResponseEntity with created tenant's ID
     */
    @PostMapping
    public ResponseEntity create(@Valid @RequestBody TenantRegisterDto tenantRegisterDto) {
        userService.checkIsUsernameUnique(tenantRegisterDto.getUsername());
        tenantRegisterDto.setPassword(passwordEncoder.encode(tenantRegisterDto.getPassword()));
        final Tenant tenant = tenantService.create(tenantRegisterDto.createTenant());
        return new ResponseEntity<>(tenant.getId(), HttpStatus.CREATED);
    }
}
