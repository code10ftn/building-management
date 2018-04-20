package com.code10.kts.repository;

import com.code10.kts.model.domain.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    @Query("select t from Topic t join t.meeting m join m.building b where b.id = :buildingId and m.id = :meetingId")
    Page<Topic> findByMeetingIdAndBuildingId(@Param("meetingId") long meetingId, @Param("buildingId") long buildingId, Pageable pageable);

    @Query("select t from Topic t join t.meeting m join m.building b where b.id = :buildingId and m.id = :meetingId and t.id = :id")
    Optional<Topic> findByIdAndMeetingIdAndBuildingId(@Param("id") long id, @Param("meetingId") long meetingId, @Param("buildingId") long buildingId);
}
