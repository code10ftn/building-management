package com.code10.kts.model.dto;

import javax.validation.constraints.NotNull;

/**
 * Represents forwarding of malfunction's assignee.
 */
public class AssigneeUpdateDto {

    /**
     * New assignee ID.
     */
    @NotNull
    private Long assigneeId;

    public AssigneeUpdateDto() {
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }
}
