package com.code10.kts.service;

import com.code10.kts.controller.exception.BadRequestException;
import com.code10.kts.controller.exception.NotFoundException;
import com.code10.kts.model.domain.Building;
import com.code10.kts.model.domain.user.Company;
import com.code10.kts.model.domain.user.User;
import com.code10.kts.repository.BuildingRepository;
import com.code10.kts.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service layer for buildings' business logic.
 */
@Service
public class BuildingService {

    private final BuildingRepository buildingRepository;

    private final UserRepository userRepository;

    @Autowired
    public BuildingService(BuildingRepository buildingRepository, UserRepository userRepository) {
        this.buildingRepository = buildingRepository;
        this.userRepository = userRepository;
    }

    /**
     * Gets all buildings.
     *
     * @param pageable page number
     * @return one page of buildings
     */
    public Page<Building> findAll(Pageable pageable) {
        return buildingRepository.findAll(pageable);
    }

    /**
     * Gets a building by its ID
     *
     * @param id building ID
     * @return building with matching ID
     */
    public Building findById(long id) {
        return buildingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("No building with ID %s found!", id)));
    }

    /**
     * Creates a new building.
     *
     * @param building building to create
     * @return created building
     */
    public Building create(Building building) {
        return buildingRepository.save(building);
    }

    /**
     * Updates an existing building.
     *
     * @param id       building ID
     * @param building building to update
     * @return updated building
     */
    public Building update(long id, Building building) {
        final Building persisted = findById(id);

        if (building.getAddress() != null && !building.getAddress().isEmpty()) {
            persisted.setAddress(building.getAddress());
        }

        if (building.getSupervisor() != null) {
            final User oldSupervisor = persisted.getSupervisor();
            if (oldSupervisor != null) {
                oldSupervisor.getSupervisingBuildings().remove(persisted);
                userRepository.save(oldSupervisor);
            }

            final User newSupervisor = building.getSupervisor();
            if (!newSupervisor.getSupervisingBuildings().contains(persisted)) {
                newSupervisor.getSupervisingBuildings().add(persisted);
                userRepository.save(newSupervisor);
            }

            persisted.setSupervisor(newSupervisor);
        }

        return buildingRepository.save(persisted);
    }

    /**
     * Deletes a building.
     *
     * @param id building ID
     */
    public void delete(long id) {
        final Building building = findById(id);
        building.getApartments().stream().flatMap(apartment -> apartment.getTenants().stream())
                .forEach(tenant -> {
                    tenant.setApartment(null);
                    userRepository.save(tenant);
                });

        buildingRepository.delete(id);
    }

    /**
     * Adds a company to a building's list of maintenance companies.
     *
     * @param building building to which the company is assigned
     * @param company  company being assigned
     */
    public void assignCompany(Building building, Company company) {
        if (building.getCompanies().contains(company)) {
            throw new BadRequestException("Company is already assigned to building!");
        }

        building.getCompanies().add(company);
        buildingRepository.save(building);
    }

    /**
     * Removes a company from a building's list of maintenance companies.
     *
     * @param building building from which the company is being removed
     * @param company  company whose access is being revoked
     */
    public void revokeCompany(Building building, Company company) {
        building.getCompanies().remove(company);
        buildingRepository.save(building);
    }

    /**
     * Checks if address is taken.
     *
     * @param address address
     * @return true if taken
     */
    public boolean addressTaken(String address) {
        final Optional building = buildingRepository.findByAddressIgnoreCase(address);
        return building.isPresent();
    }
}
