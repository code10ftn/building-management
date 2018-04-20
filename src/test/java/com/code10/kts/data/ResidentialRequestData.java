package com.code10.kts.data;

import com.code10.kts.model.domain.Apartment;
import com.code10.kts.model.domain.ResidentialRequest;
import com.code10.kts.model.domain.user.Tenant;
import com.code10.kts.model.dto.TenantResidenceDto;
import com.code10.kts.util.DataUtil;

/**
 * Residential request testing constants and utility methods.
 */
public class ResidentialRequestData {

    private static final int UNINHABITED_APARTMENT_NUMBER = 2;

    public static final int NON_EXISTENT_APARTMENT_NUMBER = 100;

    public static ResidentialRequest getResidentialRequest(Tenant tenant, Apartment apartment) {
        return new ResidentialRequest(tenant, apartment);
    }

    public static TenantResidenceDto getTenantResidenceDto() {
        return new TenantResidenceDto(UNINHABITED_APARTMENT_NUMBER);
    }

    public static TenantResidenceDto getExistingTenantResidenceDto() {
        return new TenantResidenceDto(DataUtil.EXISTING_APARTMENT_NUMBER);
    }
}
