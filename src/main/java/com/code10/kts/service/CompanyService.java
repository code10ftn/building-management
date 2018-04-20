package com.code10.kts.service;

import com.code10.kts.model.domain.Building;
import com.code10.kts.model.domain.user.Company;
import com.code10.kts.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for companies' business logic.
 */
@Service
public class CompanyService {

    private final UserRepository userRepository;

    @Autowired
    public CompanyService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Gets all companies still not assigned to a building.
     *
     * @param building building
     * @return List of unassigned companies
     */
    public List<Company> findUnaccepted(Building building) {
        return userRepository.findAll().stream()
                .filter(user -> user instanceof Company && !building.getCompanies().contains(user))
                .map(user -> (Company) user).collect(Collectors.toList());
    }

    /**
     * Creates a new company.
     *
     * @param company company to create
     * @return created company
     */
    public Company create(Company company) {
        return userRepository.save(company);
    }
}
