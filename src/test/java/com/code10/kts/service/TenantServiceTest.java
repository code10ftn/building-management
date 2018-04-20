package com.code10.kts.service;

import com.code10.kts.model.domain.Apartment;
import com.code10.kts.model.domain.ResidentialRequest;
import com.code10.kts.model.domain.user.Role;
import com.code10.kts.model.domain.user.Tenant;
import com.code10.kts.repository.ApartmentRepository;
import com.code10.kts.repository.UserRepository;
import com.code10.kts.util.DataUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Tests {@link TenantService} methods.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
@Transactional
public class TenantServiceTest {

    @Autowired
    private TenantService tenantService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApartmentRepository apartmentRepository;

    /**
     * Test adding residence if data is valid.
     */
    @Test
    public void addResidenceShouldUpdateApartmentWhenValid() {
        // Arrange
        final ResidentialRequest residentialRequest = new ResidentialRequest();
        residentialRequest.setTenant((Tenant) userRepository.findById(DataUtil.EXISTING_TENANT_ID).orElseGet(null));
        residentialRequest.setApartment(apartmentRepository.findOne((DataUtil.EXISTING_APARTMENT_ID)));

        // Act
        tenantService.addResidence(residentialRequest);

        // Assert
        final Tenant tenant = (Tenant) userRepository.findById(DataUtil.EXISTING_TENANT_ID).orElseGet(null);
        assertEquals(residentialRequest.getApartment().getId(), tenant.getApartment().getId());
    }

    /**
     * Test adding residence if tenant is supervisor.
     */
    @Test
    public void addResidenceShouldUpdateSupervisorRoleWhenTenantIsSupervisor() {
        // Arrange
        final Tenant supervisor = (Tenant) userRepository.findById(DataUtil.EXISTING_TENANT_SUPERVISOR_ID).orElseGet(null);
        final Apartment apartment = apartmentRepository.findOne((DataUtil.EXISTING_APARTMENT_ID));
        supervisor.setApartment(apartment);
        userRepository.save(supervisor);

        Apartment newApartment = new Apartment();
        newApartment.setNumber(2);
        newApartment.setBuilding(apartment.getBuilding());
        newApartment = apartmentRepository.save(newApartment);

        final ResidentialRequest residentialRequest = new ResidentialRequest();
        residentialRequest.setTenant(supervisor);
        residentialRequest.setApartment(newApartment);

        // Act
        tenantService.addResidence(residentialRequest);

        // Assert
        final Tenant tenant = (Tenant) userRepository.findById(DataUtil.EXISTING_TENANT_ID).orElseGet(null);
        assertFalse(tenant.hasAuthority(Role.SUPERVISOR));
    }
}
