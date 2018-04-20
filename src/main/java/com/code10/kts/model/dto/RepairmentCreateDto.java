package com.code10.kts.model.dto;

import com.code10.kts.model.domain.Repairment;
import com.code10.kts.model.domain.user.User;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Represents a new repairment.
 */
public class RepairmentCreateDto {

    /**
     * Price of the malfunction's repairment.
     */
    private double price;

    /**
     * Malfunction's repair date.
     */
    @NotNull
    private Date repairDate;

    public RepairmentCreateDto() {
    }

    public RepairmentCreateDto(double price, Date repairDate) {
        this.price = price;
        this.repairDate = repairDate;
    }

    /**
     * Creates Repairment from this DTO.
     *
     * @param assignee user that performs repairment
     * @return Repairment model
     */
    public Repairment createRepairment(User assignee) {
        return new Repairment(price, repairDate, assignee, null);
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
}
