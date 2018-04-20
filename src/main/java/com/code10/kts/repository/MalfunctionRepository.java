package com.code10.kts.repository;

import com.code10.kts.model.domain.Malfunction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface MalfunctionRepository extends JpaRepository<Malfunction, Long> {

    Optional<Malfunction> findById(long id);

    Page<Malfunction> findByBuildingId(long id, Pageable pageable);

    Page<Malfunction> findByBuildingIdAndAssigneeId(long buildingId, long assigneeId, Pageable pageable);

    Optional<Malfunction> findByIdAndBuildingId(long id, long buildingId);

    List<Malfunction> findByBuildingIdAndReportDateBetween(long buildingId, Date start, Date end);

    List<Malfunction> findByReportId(Long reportId);

    Page<Malfunction> findByAssigneeId(Long id, Pageable pageable);
}
