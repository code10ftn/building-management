package com.code10.kts.model.domain.user;

import com.code10.kts.model.domain.Building;
import com.code10.kts.model.domain.WorkArea;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Represents a company in the system.
 */
@Entity
@Table(name = "company")
public class Company extends User {

    /**
     * Company's name.
     */
    @Column(name = "registered_name", nullable = false)
    private String name;

    /**
     * Company's address.
     */
    @Column(name = "address", nullable = false)
    private String address;

    /**
     * Description of a company.
     */
    @Column(name = "description")
    private String description;

    /**
     * List of buildings to which this company was assigned.
     */
    @ManyToMany(mappedBy = "companies")
    private List<Building> buildings;

    /**
     * Work area of the company. (e.g. HOUSEKEEPING)
     */
    @Column(name = "work_area")
    @Enumerated(EnumType.STRING)
    private WorkArea workArea;

    public Company() {
    }

    public Company(String name, String address, String description) {
        this.name = name;
        this.address = address;
        this.description = description;
    }

    public Company(String username, String password, String email, String phoneNumber, Date lastPasswordReset,
                   Collection<Role> authorities, String name, String address, String description,
                   List<Building> buildings) {
        super(username, password, email, phoneNumber, lastPasswordReset, authorities);
        this.name = name;
        this.address = address;
        this.description = description;
        this.buildings = buildings;
    }

    public Company(String username, String password, String email, String phoneNumber, String name, String address, String description, WorkArea workArea) {
        super(username, password, email, phoneNumber);
        this.name = name;
        this.address = address;
        this.description = description;
        this.workArea = workArea;
        this.authorities.add(Role.COMPANY);
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

    public List<Building> getBuildings() {
        return buildings;
    }

    public void setBuildings(List<Building> buildings) {
        this.buildings = buildings;
    }

    public WorkArea getWorkArea() {
        return workArea;
    }

    public void setWorkArea(WorkArea workArea) {
        this.workArea = workArea;
    }
}
