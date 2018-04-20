package com.code10.kts.model.dto;

import com.code10.kts.model.domain.Malfunction;
import com.code10.kts.model.domain.WorkArea;

/**
 * Represents an update of a malfunction.
 */
public class MalfunctionUpdateDto {

    /**
     * Description of a malfunction.
     */
    private String description;

    /**
     * Type of malfunction.
     */
    private WorkArea workArea;

    public MalfunctionUpdateDto() {
    }

    public MalfunctionUpdateDto(String description, WorkArea workArea) {
        this.description = description;
        this.workArea = workArea;
    }

    /**
     * Creates Malfunction from this DTO.
     *
     * @return Malfunction model
     */
    public Malfunction createMalfunction() {
        Malfunction malfunction = new Malfunction();
        malfunction.setDescription(description);
        malfunction.setWorkArea(workArea);
        return malfunction;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public WorkArea getWorkArea() {
        return workArea;
    }

    public void setWorkArea(WorkArea workArea) {
        this.workArea = workArea;
    }
}
