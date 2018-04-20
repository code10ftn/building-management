package com.code10.kts.repository;

import com.code10.kts.model.domain.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    Page<Announcement> findAllByBuildingIdOrderByTimestampDesc(long buildingId, Pageable pageable);

    Optional<Announcement> findById(long id);

    Optional<Announcement> findByIdAndBuildingId(long id, long buildingId);
}
