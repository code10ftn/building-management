package com.code10.kts.model.dto;

import com.code10.kts.model.domain.Apartment;
import com.code10.kts.model.domain.Building;
import com.code10.kts.model.domain.user.Company;
import com.code10.kts.model.domain.user.Tenant;
import com.code10.kts.model.domain.user.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents one building sent to client.
 */
public class BuildingResponse {

    /**
     * Building's id.
     */
    private long id;

    /**
     * Building's address.
     */
    private String address;

    /**
     * Building's supervisor.
     */
    private User supervisor;

    /**
     * Apartments in a building.
     */
    private List<Apartment> apartments;

    /**
     * Companies assigned to building.
     */
    private List<Company> companies;

    /**
     * Tenants living in a building.
     */
    private List<Tenant> tenants;

    public BuildingResponse() {
    }

    public BuildingResponse(Building building) {
        this.id = building.getId();
        this.address = building.getAddress();
        this.supervisor = building.getSupervisor();
        this.apartments = building.getApartments();
        this.companies = building.getCompanies();
        this.tenants = building.getApartments().stream().map(Apartment::getTenants).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public User getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(User supervisor) {
        this.supervisor = supervisor;
    }

    public List<Apartment> getApartments() {
        return apartments;
    }

    public void setApartments(List<Apartment> apartments) {
        this.apartments = apartments;
    }

    public List<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }

    public List<Tenant> getTenants() {
        return tenants;
    }

    public void setTenants(List<Tenant> tenants) {
        this.tenants = tenants;
    }
}
