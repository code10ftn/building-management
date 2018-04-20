package com.code10.kts.model.dto;

import javax.validation.constraints.Min;

/**
 * Represents building's apartment to which tenant wishes to move.
 */
public class TenantResidenceDto {

    /**
     * Apartment number.
     */
    @Min(0)
    private int apartmentNumber;

    public TenantResidenceDto() {
    }

    public TenantResidenceDto(int apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
    }

    public int getApartmentNumber() {
        return apartmentNumber;
    }

    public void setApartmentNumber(int apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
    }
}
