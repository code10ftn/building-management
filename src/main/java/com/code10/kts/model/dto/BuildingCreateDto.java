package com.code10.kts.model.dto;

import com.code10.kts.model.domain.Apartment;
import com.code10.kts.model.domain.Building;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents new building.
 */
public class BuildingCreateDto {

    /**
     * Building's address.
     */
    @NotEmpty
    private String address;

    /**
     * Number of apartments in the building.
     */
    @Min(1)
    private int apartmentCount;

    public BuildingCreateDto() {
    }

    public BuildingCreateDto(String address, int apartmentCount) {
        this.address = address;
        this.apartmentCount = apartmentCount;
    }

    /**
     * Creates Building from this DTO.
     *
     * @return Building model
     */
    public Building createBuilding() {
        final Building building = new Building();
        building.setAddress(address);

        final List<Apartment> apartments = new ArrayList<>();
        for (int i = 0; i < apartmentCount; i++) {
            final Apartment apartment = new Apartment(i + 1);
            apartments.add(apartment);
            apartment.setBuilding(building);
        }
        building.setApartments(apartments);

        return building;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getApartmentCount() {
        return apartmentCount;
    }

    public void setApartmentCount(int apartmentCount) {
        this.apartmentCount = apartmentCount;
    }
}
