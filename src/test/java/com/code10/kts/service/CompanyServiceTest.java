package com.code10.kts.service;

import com.code10.kts.data.CompanyData;
import com.code10.kts.model.domain.user.Company;
import com.code10.kts.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertNotNull;

/**
 * Tests {@link CompanyService} methods.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
@Transactional
public class CompanyServiceTest {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Test create company.
     */
    @Test
    public void createShouldReturnCreatedSurvey() {
        // Arrange
        final Company company = CompanyData.getCompanyRegisterDto().createCompany();

        // Act
        final Company created = companyService.create(company);

        // Assert
        assertNotNull(created);
    }

    /**
     * Test create company when name is set to null.
     */
    @Test(expected = DataIntegrityViolationException.class)
    public void createShouldThrowExceptionWhenNameIsNull() {
        // Arrange
        final Company company = CompanyData.getCompanyRegisterDto().createCompany();
        company.setName(null);

        // Act
        companyService.create(company);
    }

    /**
     * Test create company when address is set to null.
     */
    @Test(expected = DataIntegrityViolationException.class)
    public void createShouldThrowExceptionWhenAddressIsNull() {
        // Arrange
        final Company company = CompanyData.getCompanyRegisterDto().createCompany();
        company.setAddress(null);

        // Act
        companyService.create(company);
    }
}