package com.code10.kts.service;

import com.code10.kts.controller.exception.BadRequestException;
import com.code10.kts.controller.exception.NotFoundException;
import com.code10.kts.model.domain.Apartment;
import com.code10.kts.model.domain.Building;
import com.code10.kts.model.domain.ResidentialRequest;
import com.code10.kts.model.domain.user.Role;
import com.code10.kts.model.domain.user.Tenant;
import com.code10.kts.model.domain.user.User;
import com.code10.kts.repository.ResidentialRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for residential requests' business logic.
 */
@Service
public class ResidentialRequestService {

    private final ResidentialRequestRepository residentialRequestRepository;

    @Autowired
    public ResidentialRequestService(ResidentialRequestRepository residentialRequestRepository) {
        this.residentialRequestRepository = residentialRequestRepository;
    }

    /**
     * Gets all building's unaccepted residential requests.
     *
     * @param user current user
     * @return one page of unaccepted residential requests
     */
    public List<ResidentialRequest> findAllUnaccepted(User user) {
        List<ResidentialRequest> residentialRequests = residentialRequestRepository.findAll();

        if (user.hasAuthority(Role.SUPERVISOR)) {
            final List<Building> buildings = user.getSupervisingBuildings();
            residentialRequests = residentialRequests.stream().filter(residentialRequest -> buildings.contains(residentialRequest.getApartment().getBuilding())).collect(Collectors.toList());
        }

        return residentialRequests;
    }

    /**
     * Gets a building's residential request by its ID.
     *
     * @param id residential request ID
     * @return building's residential request with matching ID
     */
    public ResidentialRequest findById(long id) {
        return residentialRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("No residential request found with ID %s!", id)));
    }

    /**
     * Gets a building's residential request by tenant's ID.
     *
     * @param id tenant ID
     * @return building's residential request with matching tenantID
     */
    public ResidentialRequest findByTenantId(long id) {
        return residentialRequestRepository.findByTenantId(id)
                .orElseThrow(() -> new NotFoundException(String.format("No requests found for tenant with ID %s", id)));
    }

    /**
     * Gets a building's residential request by its ID and current user ID.
     *
     * @param id       residential request ID
     * @param tenantId tenant ID
     * @return tenant's residential request with matching ID
     */
    public ResidentialRequest findByIdAndTenantId(long id, long tenantId) {
        return residentialRequestRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new NotFoundException(String.format("No residential request with ID %s found for tenant with ID %s!", id, tenantId)));
    }

    /**
     * Creates a new residential request for a building.
     *
     * @param tenant    tenant
     * @param apartment apartment
     * @return created residential request
     */
    public ResidentialRequest create(Tenant tenant, Apartment apartment) {
        if (tenant.getApartment() != null && tenant.getApartment().getId().equals(apartment.getId())) {
            throw new BadRequestException(String.format("Tenant with ID %s already lives in apartment with ID %s",
                    tenant.getId(), apartment.getId()));
        }

        if (residentialRequestRepository.findByTenantId(tenant.getId()).isPresent()) {
            throw new BadRequestException(String.format("Tenant with ID %d already submitted a residential request!",
                    tenant.getId()));
        }

        final ResidentialRequest residentialRequest = new ResidentialRequest(tenant, apartment);
        return residentialRequestRepository.save(residentialRequest);
    }

    /**
     * Deletes a residential request.
     *
     * @param residentialRequest residentialRequest
     */
    public void removeResidentialRequest(ResidentialRequest residentialRequest) {
        residentialRequestRepository.delete(residentialRequest);
    }
}
