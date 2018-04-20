package com.code10.kts.model.domain;

import com.code10.kts.model.domain.user.Tenant;

import javax.persistence.*;

/**
 * Represents a residential request.
 * Residential request is created when a user moves to another building/apartment.
 */
@Entity
@Table(name = "residential_request")
public class ResidentialRequest {

    /**
     * Residential request's ID.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /**
     * Tenant that request the move.
     */
    @OneToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    /**
     * Apartment to which this tenant is moving.
     */
    @ManyToOne
    @JoinColumn(name = "apartment_id")
    private Apartment apartment;

    public ResidentialRequest() {
    }

    public ResidentialRequest(Tenant tenant, Apartment apartment) {
        this.tenant = tenant;
        this.apartment = apartment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
    }
}
