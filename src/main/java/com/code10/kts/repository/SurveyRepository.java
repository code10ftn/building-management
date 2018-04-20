package com.code10.kts.repository;

import com.code10.kts.model.domain.Survey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface SurveyRepository extends JpaRepository<Survey, Long> {

    Optional<Survey> findByIdAndBuildingId(long id, long buildingId);

    Page<Survey> findByBuildingId(long buildingId, Pageable pageable);

    Page<Survey> findByBuildingIdAndExpirationDateAfter(long buildingId, Date date, Pageable pageable);

    Page<Survey> findByBuildingIdAndExpirationDateBefore(long buildingId, Date date, Pageable pageable);

    List<Survey> findByBuildingIdAndExpirationDateBetween(long buildingId, Date start, Date end);

    List<Survey> findByReportId(Long reportId);
}
