package com.code10.kts.model.domain;

import com.code10.kts.model.domain.user.Company;
import com.code10.kts.model.domain.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "building")
public class Building {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "address", unique = true, nullable = false)
    private String address;

    @ManyToOne
    @JoinColumn(name = "supervisor_id")
    @JsonIgnore
    private User supervisor;

    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(name = "building_company",
            joinColumns = @JoinColumn(name = "building_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "company_id", referencedColumnName = "id"))
    @JsonIgnore
    private List<Company> companies;

    @JsonIgnore
    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL)
    private List<Apartment> apartments;

    @JsonIgnore
    @OneToMany(mappedBy = "building", cascade = CascadeType.REMOVE)
    private List<Meeting> meetings;

    @JsonIgnore
    @OneToMany(mappedBy = "building", cascade = CascadeType.REMOVE)
    private List<Announcement> announcements;

    @OneToMany(mappedBy = "building", cascade = CascadeType.REMOVE)
    private List<Survey> surveys;

    @OneToMany(mappedBy = "building", cascade = CascadeType.REMOVE)
    private List<Malfunction> malfunctions;

    public Building() {
        this.apartments = new ArrayList<>();
    }

    public Building(String address, User supervisor, List<Company> companies, List<Meeting> meetings) {
        this();
        this.address = address;
        this.supervisor = supervisor;
        this.companies = companies;
        this.meetings = meetings;
    }

    public Building(String address, User supervisor) {
        this.address = address;
        this.supervisor = supervisor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public List<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }

    public List<Apartment> getApartments() {
        return apartments;
    }

    public void setApartments(List<Apartment> apartments) {
        this.apartments = apartments;
    }

    public List<Meeting> getMeetings() {
        return meetings;
    }

    public void setMeetings(List<Meeting> meetings) {
        this.meetings = meetings;
    }

    public List<Announcement> getAnnouncements() {
        return announcements;
    }

    public void setAnnouncements(List<Announcement> announcements) {
        this.announcements = announcements;
    }
}
