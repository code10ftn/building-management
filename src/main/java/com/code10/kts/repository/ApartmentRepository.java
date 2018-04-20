package com.code10.kts.repository;

import com.code10.kts.model.domain.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApartmentRepository extends JpaRepository<Apartment, Long> {

    Optional<Apartment> findById(long id);

    Optional<Apartment> findByNumberAndBuildingId(int number, long buildingId);
}
