package com.code10.kts.model.domain.user;

import com.code10.kts.model.domain.Building;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Represents an entity that is able to log in to the system.
 */
@Entity
@Table(name = "registered_user")
@Inheritance(strategy = InheritanceType.JOINED)
public class User {

    /**
     * User's ID.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    protected Long id;

    /**
     * User's username.
     */
    @Column(name = "username", nullable = false)
    protected String username;

    /**
     * User's password.
     */
    @JsonIgnore
    @Column(name = "password", nullable = false)
    protected String password;

    /**
     * User's email.
     */
    @Column(name = "email", nullable = false)
    protected String email;

    /**
     * User's phone number.
     */
    @Column(name = "phone_number")
    protected String phoneNumber;

    /**
     * The date of user's last password reset.
     */
    @Column(name = "last_password_reset")
    protected Date lastPasswordReset;

    /**
     * Collection of user's authorities.
     */
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "authority", nullable = false)
    @Enumerated(EnumType.STRING)
    protected Collection<Role> authorities = new ArrayList<>();

    /**
     * List of buildings which the user is supervising.
     */
    @OneToMany(mappedBy = "supervisor")
    protected List<Building> supervisingBuildings;

    public User() {
        super();
    }

    public User(String username, String password, String email, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.authorities = new ArrayList<>();
    }

    public User(String username, String password, String email, String phoneNumber, Date lastPasswordReset, Collection<Role> authorities) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.lastPasswordReset = lastPasswordReset;
        this.authorities = authorities;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getLastPasswordReset() {
        return this.lastPasswordReset;
    }

    public void setLastPasswordReset(Date lastPasswordReset) {
        this.lastPasswordReset = lastPasswordReset;
    }

    public Collection<Role> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<Role> authorities) {
        this.authorities = authorities;
    }

    public boolean hasAuthority(Role role) {
        return authorities.contains(role);
    }

    public List<Building> getSupervisingBuildings() {
        return supervisingBuildings;
    }

    public void setSupervisingBuildings(List<Building> supervisingBuildings) {
        this.supervisingBuildings = supervisingBuildings;
    }
}
