package com.code10.kts.model.dto;

import com.code10.kts.model.domain.WorkArea;
import com.code10.kts.model.domain.user.Company;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Represents when a company is registered.
 */
public class CompanyRegisterDto extends UserRegisterDto {

    /**
     * Company's name.
     */
    @NotEmpty
    private String name;

    /**
     * Company's address.
     */
    @NotEmpty
    private String address;

    /**
     * Description of a company.
     */
    @NotEmpty
    private String description;

    /**
     * Company's work area.
     */
    @NotNull
    private WorkArea workArea;

    public CompanyRegisterDto() {
    }

    public CompanyRegisterDto(String username, String password, String email, String phoneNumber, String name, String address, String description, WorkArea workArea) {
        super(username, password, email, phoneNumber);
        this.name = name;
        this.address = address;
        this.description = description;
        this.workArea = workArea;
    }

    /**
     * Creates a company from this DTO.
     *
     * @return Company model
     */
    public Company createCompany() {
        return new Company(username, password, email, phoneNumber, name, address, description, workArea);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public WorkArea getWorkArea() {
        return workArea;
    }

    public void setWorkArea(WorkArea workArea) {
        this.workArea = workArea;
    }
}