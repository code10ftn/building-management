package com.code10.kts.model.dto;

import com.code10.kts.model.domain.Building;
import com.code10.kts.model.domain.user.User;

/**
 * Represents building update.
 * Used when street changes the name or when building has a new supervisor.
 */
public class BuildingUpdateDto {

    /**
     * Building's new address.
     */
    private String address;

    /**
     * Building's new supervisor id.
     */
    private Long supervisorId;

    public BuildingUpdateDto() {
    }

    public BuildingUpdateDto(String address, Long supervisorId) {
        this.address = address;
        this.supervisorId = supervisorId;
    }

    /**
     * Creates Building from this DTO.
     *
     * @param user building's supervisor
     * @return Building model
     */
    public Building createBuilding(User user) {
        final Building building = new Building();
        building.setAddress(address);
        building.setSupervisor(user);
        return building;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(Long supervisorId) {
        this.supervisorId = supervisorId;
    }
}
