package com.code10.kts.data;

import com.code10.kts.model.domain.Apartment;
import com.code10.kts.model.domain.Building;
import com.code10.kts.model.domain.user.User;
import com.code10.kts.model.dto.BuildingCreateDto;
import com.code10.kts.model.dto.BuildingUpdateDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Building testing constants and utility methods.
 */
public class BuildingData {

    public static final String EXISTING_BUILDING_SUPERVISOR_USERNAME = "haus";

    public static final String BUILDING_UPDATED_ADDRESS = "Nova adresa";

    public static final String BUILDING_EXISTING_ADDRESS = "Adresa 174c";

    public static final String BUILDING_NON_EXISTENT_ADDRESS = "Some very unique address never seen before 123";

    public static final Long COMPANY_SUPERVISOR_ID = 9L;

    public static final Long NON_EXISTENT_BUILDING_ID = 100L;

    public static final Long COMPANY_ID = 6L;

    public static final Long NON_ASSIGNED_COMPANY_ID = 7L;

    public static final Long DELETE_BUILDING_ID = 2L;

    public static Building getBuilding(User user) {
        final Building building = new Building("Test Address 12", user);
        final List<Apartment> apartments = new ArrayList<>();

        int apartmentCount = 5;
        for (int i = 0; i < apartmentCount; i++) {
            final Apartment apartment = new Apartment(i);
            apartment.setBuilding(building);
            apartments.add(apartment);
        }

        building.setApartments(apartments);

        return building;
    }

    public static BuildingCreateDto getCreateDto() {
        return new BuildingCreateDto("Ilije Bircanina 48", 49);
    }

    public static BuildingUpdateDto getUpdateDto() {
        return new BuildingUpdateDto(BUILDING_UPDATED_ADDRESS, null);
    }
}
