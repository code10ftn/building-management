package com.code10.kts.model.dto;

import com.code10.kts.model.domain.Building;
import com.code10.kts.model.domain.Malfunction;
import com.code10.kts.model.domain.WorkArea;
import com.code10.kts.model.domain.user.Company;
import com.code10.kts.model.domain.user.User;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Optional;

/**
 * Represents new malfunction.
 */
public class MalfunctionCreateDto {

    /**
     * Description of a malfunction.
     */
    @NotEmpty
    private String description;

    /**
     * Type of malfunction.
     */
    @NotNull
    private WorkArea workArea;

    /**
     * Creates a Malfunction model from this DTO.
     *
     * @param building building in which malfunction occurred
     * @param creator  user that reported malfunction
     * @return Malfunction model
     */
    public Malfunction createMalfunction(Building building, User creator) {
        final Malfunction malfunction = new Malfunction();
        malfunction.setDescription(description);
        malfunction.setWorkArea(workArea);
        malfunction.setReportDate(new Date());

        malfunction.setCreator(creator);
        malfunction.setBuilding(building);

        Optional<Company> company = building.getCompanies().stream()
                .filter(c -> c.getWorkArea() == workArea).findFirst();

        if (company.isPresent()) {
            malfunction.setAssignee(company.get());
        } else {
            malfunction.setAssignee(building.getSupervisor());
        }

        return malfunction;
    }

    public WorkArea getWorkArea() {
        return workArea;
    }

    public void setWorkArea(WorkArea workArea) {
        this.workArea = workArea;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
