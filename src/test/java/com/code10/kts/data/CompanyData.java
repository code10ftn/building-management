package com.code10.kts.data;

import com.code10.kts.model.domain.WorkArea;
import com.code10.kts.model.dto.CompanyRegisterDto;

/**
 * Company testing constants and utility methods.
 */
public class CompanyData {

    public static CompanyRegisterDto getCompanyRegisterDto() {
        return new CompanyRegisterDto("Company username", "Company password", "Company email", "Phone number"
                , "Company name", "Company address", "Description", WorkArea.WATER);
    }
}