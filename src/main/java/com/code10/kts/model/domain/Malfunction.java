package com.code10.kts.model.domain;

import com.code10.kts.model.domain.user.Tenant;
import com.code10.kts.model.domain.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Represents a malfunction in the building.
 */
@Entity
@Table(name = "malfunction")
public class Malfunction {

    /**
     * Malfunction's ID.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /**
     * Description of a malfunction.
     */
    @Column(name = "description", nullable = false)
    private String description;

    /**
     * Date when malfunction was reported.
     */
    @Column(name = "report_date", nullable = false)
    private Date reportDate;

    /**
     * Type of malfunction.
     */
    @Column(name = "work_area")
    @Enumerated(EnumType.STRING)
    private WorkArea workArea;

    /**
     * Building in which malfunction occurred.
     */
    @ManyToOne
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    /**
     * User that reported malfunction.
     */
    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    /**
     * User that this malfunction is assigned to. Either a supervisor or a company.
     */
    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private User assignee;

    /**
     * Comments on a malfunction. Posted by creator and/or assignee.
     */
    @OneToMany(mappedBy = "malfunction")
    private List<Comment> comments;

    /**
     * Report to which this malfunction is tied to after the meeting.
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "report_id")
    private Report report;

    /**
     * Scheduled repairment of a malfunction.
     */
    @OneToOne(cascade = CascadeType.ALL)
    private Repairment repairment;

    public Malfunction() {
    }

    public Malfunction(Long id, String description, Date reportDate, WorkArea workArea,
                       Building building, Tenant creator, User assignee, Report report) {
        this.id = id;
        this.description = description;
        this.reportDate = reportDate;
        this.workArea = workArea;
        this.building = building;
        this.creator = creator;
        this.assignee = assignee;
        this.report = report;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public WorkArea getWorkArea() {
        return workArea;
    }

    public void setWorkArea(WorkArea workArea) {
        this.workArea = workArea;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public User getAssignee() {
        return assignee;
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public Repairment getRepairment() {
        return repairment;
    }

    public void setRepairment(Repairment repairment) {
        this.repairment = repairment;
    }
}
