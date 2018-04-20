package com.code10.kts.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

/**
 * Represents a meeting report.
 * Report is created by a supervisor after meeting has concluded.
 */
@Entity
@Table(name = "report")
public class Report {

    /**
     * Report's ID.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /**
     * Meeting for which this report is created.
     */
    @OneToOne
    @JsonIgnore
    private Meeting meeting;

    /**
     * All the malfunctions that were reported in the period since the last meeting.
     */
    @OneToMany(mappedBy = "report")
    private List<Malfunction> malfunctions;

    /**
     * All the surveys that were created by the supervisor in the period since the last meeting.
     */
    @OneToMany(mappedBy = "report")
    private List<Survey> surveys;

    /**
     * Report comment.
     */
    @Column(name = "comment")
    private String comment;

    public Report() {
    }

    public Report(Meeting meeting, List<Malfunction> malfunctions, List<Survey> surveys, String comment) {
        this.meeting = meeting;
        this.malfunctions = malfunctions;
        this.surveys = surveys;
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Meeting getMeeting() {
        return meeting;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    public List<Malfunction> getMalfunctions() {
        return malfunctions;
    }

    public void setMalfunctions(List<Malfunction> malfunctions) {
        this.malfunctions = malfunctions;
    }

    public List<Survey> getSurveys() {
        return surveys;
    }

    public void setSurveys(List<Survey> surveys) {
        this.surveys = surveys;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
