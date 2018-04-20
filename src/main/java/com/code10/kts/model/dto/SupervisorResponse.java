package com.code10.kts.model.dto;

import com.code10.kts.model.domain.user.Company;
import com.code10.kts.model.domain.user.Tenant;

/**
 * Represents one potentital supervisor sent to client.
 */
public class SupervisorResponse {

    /**
     * User's id.
     */
    private long supervisorId;

    /**
     * Concatenated name of a potential supervisor.
     */
    private String name;

    public SupervisorResponse() {
    }

    public SupervisorResponse(Tenant tenant) {
        this.supervisorId = tenant.getId();
        this.name = String.format("%s %s", tenant.getFirstName(), tenant.getLastName());
    }

    public SupervisorResponse(Company company) {
        this.supervisorId = company.getId();
        this.name = company.getName();
    }

    public long getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(long supervisorId) {
        this.supervisorId = supervisorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
