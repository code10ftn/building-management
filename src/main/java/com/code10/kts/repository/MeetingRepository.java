package com.code10.kts.repository;

import com.code10.kts.model.domain.Meeting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    Page<Meeting> findByBuildingId(long buildingId, Pageable pageable);

    Optional<Meeting> findByIdAndBuildingId(long id, long buildingId);
}
