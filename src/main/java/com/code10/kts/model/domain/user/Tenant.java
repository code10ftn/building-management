package com.code10.kts.model.domain.user;

import com.code10.kts.model.domain.Apartment;

import javax.persistence.*;

/**
 * Represents a tenant in the system.
 */
@Entity
@Table(name = "tenant")
public class Tenant extends User {

    /**
     * Tenant's first name.
     */
    @Column(name = "first_name", nullable = false)
    private String firstName;

    /**
     * Tenant's last name.
     */
    @Column(name = "last_name", nullable = false)
    private String lastName;

    /**
     * Tenant's apartment.
     */
    @ManyToOne
    @JoinColumn(name = "apartment_id")
    private Apartment apartment;

    public Tenant() {
    }

    public Tenant(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Tenant(String username, String password, String email, String phoneNumber,
                  String firstName, String lastName) {
        super(username, password, email, phoneNumber);
        this.firstName = firstName;
        this.lastName = lastName;

        this.authorities.add(Role.TENANT);
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

    public Apartment getApartment() {
        return apartment;
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
    }
}
