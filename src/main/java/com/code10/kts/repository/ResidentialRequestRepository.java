package com.code10.kts.repository;

import com.code10.kts.model.domain.ResidentialRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResidentialRequestRepository extends JpaRepository<ResidentialRequest, Long> {

    Optional<ResidentialRequest> findById(long id);

    Optional<ResidentialRequest> findByTenantId(long tenantId);

    Optional<ResidentialRequest> findByIdAndTenantId(long id, long tenantId);
}
