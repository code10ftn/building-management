package com.code10.kts.model.domain;

import com.code10.kts.model.domain.user.Tenant;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a survey created by building's supervisor.
 * Tenants answer surveys on matters regarding building maintenance.
 */
@Entity
@Table(name = "survey")
public class Survey {

    /**
     * Survey's ID.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /**
     * Survey's name.
     */
    @Column(name = "name")
    private String name;

    /**
     * Survey's expiration date.
     */
    @Column(name = "expiration_date")
    private Date expirationDate;

    /**
     * Building in which this survey was created.
     */
    @ManyToOne
    @JoinColumn(name = "building_id")
    private Building building;

    /**
     * Survey's questions.
     */
    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL)
    private List<Question> questions;

    /**
     * List of tenants this survey was answered by.
     */
    @ManyToMany
    @JoinTable(name = "survey_tenant",
            joinColumns = @JoinColumn(name = "survey_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "tenant_id", referencedColumnName = "id"))
    private List<Tenant> answeredBy;

    /**
     * Report to which this survey will be added after meeting is concluded.
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "report_id")
    private Report report;

    public Survey() {
        this.questions = new ArrayList<>();
        this.answeredBy = new ArrayList<>();
    }

    public Survey(Date expirationDate, Building building, List<Question> questions, List<Tenant> answeredBy, Report report) {
        this.expirationDate = expirationDate;
        this.building = building;
        this.questions = questions;
        this.answeredBy = answeredBy;
        this.report = report;
    }

    public boolean datePassed() {
        return expirationDate.before(new Date());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<Tenant> getAnsweredBy() {
        return answeredBy;
    }

    public void setAnsweredBy(List<Tenant> answeredBy) {
        this.answeredBy = answeredBy;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}