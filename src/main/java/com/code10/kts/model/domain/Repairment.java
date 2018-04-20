package com.code10.kts.model.domain;

import com.code10.kts.model.domain.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

/**
 * Represents a repairment of a malfunction.
 * Repairment is created when company/supervisor performs a repairment.
 */
@Entity
@Table(name = "repairment")
public class Repairment {

    /**
     * Repairment's ID.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /**
     * Repairment's price.
     */
    @Column(name = "price")
    private double price;

    /**
     * Repair date of a malfunction.
     */
    @Column(name = "repair_date")
    private Date repairDate;

    /**
     * User (company/supervisor) that performs repairment.
     */
    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private User assignee;

    /**
     * Malfunction that this repairment is for.
     */
    @JsonIgnore
    @OneToOne
    private Malfunction malfunction;

    public Repairment() {
    }

    public Repairment(double price, Date repairDate, User assignee, Malfunction malfunction) {
        this.price = price;
        this.repairDate = repairDate;
        this.assignee = assignee;
        this.malfunction = malfunction;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getRepairDate() {
        return repairDate;
    }

    public void setRepairDate(Date repairDate) {
        this.repairDate = repairDate;
    }

    public User getAssignee() {
        return assignee;
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    public Malfunction getMalfunction() {
        return malfunction;
    }

    public void setMalfunction(Malfunction malfunction) {
        this.malfunction = malfunction;
    }
}
