package com.code10.kts.service;

import com.code10.kts.controller.exception.BadRequestException;
import com.code10.kts.controller.exception.NotFoundException;
import com.code10.kts.model.domain.Malfunction;
import com.code10.kts.model.domain.Repairment;
import com.code10.kts.model.domain.user.Role;
import com.code10.kts.model.domain.user.User;
import com.code10.kts.repository.MalfunctionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Service layer for malfunctions' business logic.
 */
@Service
public class MalfunctionService {

    private final MalfunctionRepository malfunctionRepository;

    @Autowired
    public MalfunctionService(MalfunctionRepository malfunctionRepository) {
        this.malfunctionRepository = malfunctionRepository;
    }

    /**
     * Creates a new malfunction for a building.
     *
     * @param malfunction malfunction to create
     * @return created malfunction
     */
    public Malfunction create(Malfunction malfunction) {
        return malfunctionRepository.save(malfunction);
    }

    /**
     * Gets all building's malfunctions.
     *
     * @param id       building ID
     * @param user     current user
     * @param pageable page number  @return one page of building's malfunctions
     * @return one page of building's malfunctions
     */
    public Page<Malfunction> findByBuildingId(long id, User user, Pageable pageable) {
        if (user.hasAuthority(Role.COMPANY) &&
                user.getSupervisingBuildings().stream().noneMatch(b -> b.getSupervisor().getId().equals(user.getId()))) {
            return malfunctionRepository.findByBuildingIdAndAssigneeId(id, user.getId(), pageable);
        } else
            return malfunctionRepository.findByBuildingId(id, pageable);
    }

    /**
     * Gets a malfunction by its ID.
     *
     * @param id malfunction ID
     * @return malfunction with matching ID
     */
    public Malfunction findById(long id) {
        return malfunctionRepository.findById(id).orElseThrow(() -> new NotFoundException(String
                .format("No malfunction with ID %s found!", id)));
    }

    /**
     * Gets a building's malfunction by its ID.
     *
     * @param id         malfunction ID
     * @param buildingId building ID
     * @return building's malfunction with matching ID
     */
    public Malfunction findByIdAndBuildingId(long id, long buildingId) {
        return malfunctionRepository.findByIdAndBuildingId(id, buildingId)
                .orElseThrow(() -> new NotFoundException(String
                        .format("No malfunction with ID %s found in building with ID %s!", id, buildingId)));
    }

    /**
     * Gets all building's malfunctions between start and end date.
     *
     * @param buildingId building ID
     * @param start      start date
     * @param end        end date
     * @return list of building's malfunctions between start and end date
     */
    public List<Malfunction> findByBuildingIdAndDateBetween(long buildingId, Date start, Date end) {
        return malfunctionRepository.findByBuildingIdAndReportDateBetween(buildingId, start, end);
    }

    /**
     * Gets all malfunctions from a meeting report.
     *
     * @param reportId report ID
     * @return list of report's malfunctions
     */
    public List<Malfunction> findByReportId(Long reportId) {
        return malfunctionRepository.findByReportId(reportId);
    }

    /**
     * Updates an existing building's malfunction.
     *
     * @param id          malfunction ID
     * @param buildingId  building ID
     * @param malfunction malfunction to update
     * @return updated malfunction
     */
    public Malfunction update(long id, long buildingId, Malfunction malfunction) {
        final Malfunction persisted = findByIdAndBuildingId(id, buildingId);

        if (malfunction.getDescription() != null && !malfunction.getDescription().isEmpty()) {
            persisted.setDescription(malfunction.getDescription());
        }
        if (malfunction.getWorkArea() != null) {
            persisted.setWorkArea(malfunction.getWorkArea());
        }

        return malfunctionRepository.save(persisted);
    }

    /**
     * Forwards a malfunction by updating its assignee.
     *
     * @param id         malfunction ID
     * @param buildingId building ID
     * @param user       new assignee
     * @return updated malfunction with new assignee
     */
    public Malfunction updateAssignee(long id, long buildingId, User user) {
        final Malfunction persisted = findByIdAndBuildingId(id, buildingId);
        persisted.setAssignee(user);

        return malfunctionRepository.save(persisted);
    }

    /**
     * Schedules a repairment for a malfunction.
     *
     * @param repairment    repairment to schedule
     * @param malfunctionId malfunction ID
     * @return updated malfunction with repairment
     */
    public Malfunction createRepairment(Repairment repairment, long malfunctionId) {
        final Malfunction malfunction = findById(malfunctionId);

        if (malfunction.getReportDate().after(repairment.getRepairDate())) {
            throw new BadRequestException("Malfunction report date is after repairment date!");
        }

        malfunction.setRepairment(repairment);
        repairment.setMalfunction(malfunction);

        return malfunctionRepository.save(malfunction);
    }

    public Page<Malfunction> findByCompanyId(long companyId, Pageable pageable) {
        return malfunctionRepository.findByAssigneeId(companyId, pageable);
    }
}
