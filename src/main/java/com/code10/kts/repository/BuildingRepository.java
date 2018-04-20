package com.code10.kts.repository;

import com.code10.kts.model.domain.Building;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BuildingRepository extends JpaRepository<Building, Long> {

    Optional<Building> findById(long id);

    Optional<Building> findByAddressIgnoreCase(String address);
}
