package com.code10.kts.model.domain;

import com.code10.kts.model.domain.user.Tenant;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "apartment")
public class Apartment {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "number", nullable = false)
    private int number;

    @OneToMany(mappedBy = "apartment")
    @JsonIgnore
    private List<Tenant> tenants;

    @ManyToOne
    @JoinColumn(name = "building_id")
    private Building building;

    public Apartment() {
    }

    public Apartment(int number) {
        this.number = number;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public List<Tenant> getTenants() {
        return tenants;
    }

    public void setTenants(List<Tenant> tenants) {
        this.tenants = tenants;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }
}