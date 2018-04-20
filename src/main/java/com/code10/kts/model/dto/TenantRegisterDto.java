package com.code10.kts.model.dto;

import com.code10.kts.model.domain.user.Tenant;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Represents a new tenant.
 */
public class TenantRegisterDto extends UserRegisterDto {

    /**
     * Tenant's first name.
     */
    @NotEmpty
    private String firstName;

    /**
     * Tenant's last name.
     */
    @NotEmpty
    private String lastName;

    public TenantRegisterDto() {
    }

    public TenantRegisterDto(String username, String password, String email, String phoneNumber, String firstName, String lastName) {
        super(username, password, email, phoneNumber);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Creates Tenant from this DTO.
     *
     * @return Tenant model
     */
    public Tenant createTenant() {
        return new Tenant(username, password, email, phoneNumber, firstName, lastName);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
