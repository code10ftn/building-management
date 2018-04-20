package com.code10.kts.service;

import com.code10.kts.controller.exception.NotFoundException;
import com.code10.kts.model.domain.Apartment;
import com.code10.kts.repository.ApartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service layer for building's apartments' business logic.
 */
@Service
public class ApartmentService {

    private final ApartmentRepository apartmentRepository;

    @Autowired
    public ApartmentService(ApartmentRepository apartmentRepository) {
        this.apartmentRepository = apartmentRepository;
    }

    /**
     * Gets building's apartment by its ID.
     *
     * @param id apartment ID
     * @return building's apartment with matching ID
     */
    public Apartment findById(long id) {
        return apartmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("No apartment with ID %s found!", id)));
    }

    /**
     * Gets building's apartment by its number and building ID
     *
     * @param number     apartment number
     * @param buildingId building ID
     * @return building's apartment matching apartment number and building ID
     */
    public Apartment findByNumberAndBuildingId(int number, long buildingId) {
        return apartmentRepository.findByNumberAndBuildingId(number, buildingId)
                .orElseThrow(() -> new NotFoundException(String.format("No apartment with number %d in building with ID %s found!", number, buildingId)));
    }
}
