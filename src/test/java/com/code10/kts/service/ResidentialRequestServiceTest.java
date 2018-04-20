package com.code10.kts.service;

import com.code10.kts.controller.exception.BadRequestException;
import com.code10.kts.controller.exception.NotFoundException;
import com.code10.kts.data.ResidentialRequestData;
import com.code10.kts.model.domain.Apartment;
import com.code10.kts.model.domain.ResidentialRequest;
import com.code10.kts.model.domain.user.Tenant;
import com.code10.kts.repository.ResidentialRequestRepository;
import com.code10.kts.util.DataUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests {@link ResidentialRequestService} methods.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
@Transactional
public class ResidentialRequestServiceTest {

    @Autowired
    private ResidentialRequestRepository residentialRequestRepository;

    @Autowired
    private ResidentialRequestService residentialRequestService;

    @Autowired
    private ApartmentService apartmentService;

    @Autowired
    private UserService userService;

    /**
     * Test find by id and tenant id with valid data.
     * This should return valid residential request.
     */
    @Test
    public void findByIdAndTenantIdShouldReturnAllWhenValid() {
        // Arrange
        final Tenant tenant = userService.findTenantById(DataUtil.EXISTING_TENANT_ID);
        final Apartment apartment = apartmentService.findById(DataUtil.EXISTING_APARTMENT_ID);
        ResidentialRequest residentialRequest = ResidentialRequestData.getResidentialRequest(tenant, apartment);
        residentialRequest = residentialRequestRepository.save(residentialRequest);

        // Act
        final ResidentialRequest persisted = residentialRequestService.findByIdAndTenantId(residentialRequest.getId(), tenant.getId());

        // Assert
        assertNotNull(persisted);
        assertEquals(residentialRequest.getId(), persisted.getId());
        assertEquals(tenant, persisted.getTenant());
        assertEquals(apartment, persisted.getApartment());
    }

    /**
     * Test find by id and tenant id with non existent tenant id.
     * This should throw NotFound exception.
     */
    @Test(expected = NotFoundException.class)
    public void findByIdAndTenantIdShouldThrowExceptionWhenTenantIdInvalid() {
        // Arrange
        final Tenant tenant = userService.findTenantById(DataUtil.EXISTING_TENANT_ID);
        final Apartment apartment = apartmentService.findById(DataUtil.EXISTING_APARTMENT_ID);
        final ResidentialRequest residentialRequest = ResidentialRequestData.getResidentialRequest(tenant, apartment);
        residentialRequestRepository.save(residentialRequest);

        // Act
        residentialRequestService.findByIdAndTenantId(residentialRequest.getId(), DataUtil.EXISTING_BUILDING_NON_TENANT_ID);
    }

    /**
     * Test create residential request with valid tenant and apartment.
     * This should return created residential request.
     */
    @Test
    public void createShouldReturnCreatedResidentialRequestWhenValid() {
        // Arrange
        final Tenant tenant = userService.findTenantById(DataUtil.EXISTING_BUILDING_NON_TENANT_ID);
        final Apartment apartment = apartmentService.findById(DataUtil.EXISTING_APARTMENT_ID);

        // Act
        final ResidentialRequest residentialRequest = residentialRequestService.create(tenant, apartment);

        // Assert
        assertNotNull(residentialRequest);
        assertEquals(tenant, residentialRequest.getTenant());
        assertEquals(apartment, residentialRequest.getApartment());
    }

    /**
     * Test create residential request with user that is already a tenant in this apartment.
     * This should throw BadRequest exception.
     */
    @Test(expected = BadRequestException.class)
    public void createShouldThrowExceptionIfUserIsAlreadyATenant() {
        // Arrange
        final Tenant tenant = userService.findTenantById(DataUtil.EXISTING_TENANT_ID);
        final Apartment apartment = apartmentService.findById(DataUtil.EXISTING_APARTMENT_ID);

        // Act
        residentialRequestService.create(tenant, apartment);
    }

    /**
     * Test create residential request with tenant that already has a submitted residential request.
     * This should throw BadRequest exception.
     */
    @Test(expected = BadRequestException.class)
    public void createShouldThrowExceptionIfUserAlreadySubmittedResidentialRequest() {
        // Arrange
        final Tenant tenant = userService.findTenantById(DataUtil.EXISTING_BUILDING_NON_TENANT_ID);
        final Apartment apartment = apartmentService.findById(DataUtil.EXISTING_APARTMENT_ID);
        final ResidentialRequest residentialRequest = ResidentialRequestData.getResidentialRequest(tenant, apartment);
        residentialRequestRepository.save(residentialRequest);

        // Act
        residentialRequestService.create(tenant, apartment);
    }

    /**
     * Test remove residential request.
     * This should delete a request and throw NotFound exception after querying for the same id.
     */
    @Test(expected = NotFoundException.class)
    public void removeShouldDeleteResidentialRequest() {
        // Arrange
        final Tenant tenant = userService.findTenantById(DataUtil.EXISTING_BUILDING_NON_TENANT_ID);
        final Apartment apartment = apartmentService.findById(DataUtil.EXISTING_APARTMENT_ID);
        ResidentialRequest residentialRequest = ResidentialRequestData.getResidentialRequest(tenant, apartment);
        residentialRequest = residentialRequestRepository.save(residentialRequest);

        // Act
        residentialRequestService.removeResidentialRequest(residentialRequest);
        residentialRequestService.findById(residentialRequest.getId());
    }
}
