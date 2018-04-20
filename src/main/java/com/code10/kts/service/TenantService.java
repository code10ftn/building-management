package com.code10.kts.service;

import com.code10.kts.model.domain.ResidentialRequest;
import com.code10.kts.model.domain.user.Role;
import com.code10.kts.model.domain.user.Tenant;
import com.code10.kts.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Service layer for tenants' business logic.
 */
@Service
public class TenantService {

    private final UserRepository userRepository;

    @Autowired
    public TenantService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Creates a new tenant.
     *
     * @param tenant tenant to create
     * @return created tenant
     */
    public Tenant create(Tenant tenant) {
        return userRepository.save(tenant);
    }

    /**
     * Changes tenant's current apartment.
     * If tenant was a supervisor in a previous building, that building now no longer has a supervisor.
     *
     * @param residentialRequest residentialRequest created by tenant
     */
    public void addResidence(ResidentialRequest residentialRequest) {
        final Tenant tenant = residentialRequest.getTenant();

        // Tenant was a supervisor in a previous building.
        if (tenant.hasAuthority(Role.SUPERVISOR)) {
            tenant.getApartment().getBuilding().setSupervisor(null);
            tenant.setAuthorities(new ArrayList<>());
            tenant.getAuthorities().add(Role.TENANT);
        }

        tenant.setApartment(residentialRequest.getApartment());
        userRepository.save(tenant);
    }
}
