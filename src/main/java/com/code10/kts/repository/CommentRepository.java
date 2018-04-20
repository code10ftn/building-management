package com.code10.kts.repository;

import com.code10.kts.model.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findById(long id);

    @Query("select c from Comment c join c.malfunction m join m.building b where b.id = :buildingId and m.id = :id")
    List<Comment> findByMalfunctionIdAndBuildingId(@Param("id") long id, @Param("buildingId") long buildingId);

    @Query("select c from Comment c join c.malfunction m join m.building b where b.id = :buildingId and m.id = :malfunctionId and c.id =:id")
    Optional<Comment> findByIdAndMalfunctionIdAndBuildingId(@Param("id") long id, @Param("malfunctionId") long malfunctionId,
                                                            @Param("buildingId") long buildingId);
}
