package com.code10.kts.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Represents a meeting in a building.
 */
@Entity
@Table(name = "meeting")
public class Meeting {

    /**
     * Meeting's ID.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /**
     * Date of a scheduled meeting.
     */
    @Column(name = "meeting_date", nullable = false)
    private Date date;

    /**
     * Building in which this meeting is held.
     */
    @ManyToOne
    @JoinColumn(name = "building_id", nullable = false)
    @JsonIgnore
    private Building building;

    /**
     * Topics of a meeting, both accepted and unaccepted.
     */
    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL)
    private List<Topic> topics;

    /**
     * Report created by supervisor after a meeting.
     */
    @OneToOne(cascade = CascadeType.ALL)
    private Report report;

    public Meeting() {
    }

    public Meeting(Date date, Building building, List<Topic> topics) {
        this.date = date;
        this.building = building;
        this.topics = topics;
    }

    public Meeting(Date date, Building building) {
        this.date = date;
        this.building = building;
    }

    public boolean datePassed() {
        return date.before(new Date());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }
}
