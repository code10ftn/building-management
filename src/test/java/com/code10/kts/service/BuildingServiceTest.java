package com.code10.kts.service;

import com.code10.kts.controller.exception.BadRequestException;
import com.code10.kts.controller.exception.NotFoundException;
import com.code10.kts.data.BuildingData;
import com.code10.kts.model.domain.Building;
import com.code10.kts.model.domain.user.Company;
import com.code10.kts.util.DataUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
@Transactional
public class BuildingServiceTest {

    @Autowired
    private BuildingService buildingService;

    @Autowired
    private UserService userService;

    /**
     * Test create building that has both address and supervisor set.
     * This should return created building.
     */
    @Test
    public void createShouldReturnCreatedBuildingWhenValid() {
        // Arrange
        final Building building = BuildingData.getBuilding(userService.findById(BuildingData.COMPANY_SUPERVISOR_ID));

        // Act
        final Building createdBuilding = buildingService.create(building);

        // Assert
        assertNotNull(createdBuilding);
        assertNotNull(createdBuilding.getAddress());
        assertEquals(building.getAddress(), createdBuilding.getAddress());
        assertNotNull(createdBuilding.getSupervisor());
        assertEquals(BuildingData.COMPANY_SUPERVISOR_ID, createdBuilding.getSupervisor().getId());
        assertNotNull(createdBuilding.getApartments());
        assertEquals(building.getApartments().size(), createdBuilding.getApartments().size());
    }

    /**
     * Test create building when address is set to null.
     * This should throw exception because of database integrity violation.
     */
    @Test(expected = DataIntegrityViolationException.class)
    public void createShouldThrowExceptionWhenInvalid() {
        // Arrange
        final Building building = BuildingData.getBuilding(userService.findById(BuildingData.COMPANY_SUPERVISOR_ID));
        building.setAddress(null);

        // Act
        buildingService.create(building);
    }

    /**
     * Test update building when new address is set and supervisor is null.
     * This should return updated building in which supervisor is not changed.
     */
    @Test
    public void updateShouldReturnUpdatedBuildingWithValidAddress() {
        // Arrange
        final Building building = BuildingData.getBuilding(null);

        // Act
        final Building updated = buildingService.update(DataUtil.EXISTING_BUILDING_ID, building);

        // Assert
        assertNotNull(updated);
        assertNotNull(updated.getAddress());
        assertEquals(building.getAddress(), updated.getAddress());
        assertNotNull(updated.getSupervisor());
        assertEquals(BuildingData.COMPANY_SUPERVISOR_ID, updated.getSupervisor().getId());
    }

    /**
     * Test update building when address is set to null and supervisor is changed.
     * This should return updated building in which address is not changed.
     */
    @Test
    public void updateShouldReturnUpdatedBuildingWithValidSupervisor() {
        // Arrange
        final Building building = BuildingData.getBuilding(userService.findById(BuildingData.COMPANY_SUPERVISOR_ID));
        building.setAddress(null);

        // Act
        final Building updated = buildingService.update(DataUtil.EXISTING_BUILDING_ID, building);

        // Assert
        assertNotNull(updated);
        assertNotNull(updated.getAddress());
        assertNotEquals(updated.getAddress(), "");
        assertNotNull(updated.getSupervisor());
        assertEquals(BuildingData.COMPANY_SUPERVISOR_ID, updated.getSupervisor().getId());
        assertEquals(2, updated.getSupervisor().getSupervisingBuildings().size());
    }

    /**
     * Test update building when non existent building id is passed.
     * This should throw NotFound exception.
     */
    @Test(expected = NotFoundException.class)
    public void updateShouldFailWhenIdIsNonExistent() {
        // Arrange
        final Building building = BuildingData.getBuilding(userService.findById(BuildingData.COMPANY_SUPERVISOR_ID));
        building.setAddress(null);

        // Act
        buildingService.update(BuildingData.NON_EXISTENT_BUILDING_ID, building);
    }

    /**
     * Test delete building when existing building id is passed.
     * This should throw exception because building no longer exists.
     */
    @Test(expected = NotFoundException.class)
    public void deleteShouldDeleteBuilding() {
        // Act
        buildingService.delete(DataUtil.EXISTING_BUILDING_ID);
        buildingService.findById(DataUtil.EXISTING_BUILDING_ID);
    }

    /**
     * Test assign company to building when existing company that's not already assigned is passed.
     * This should pass.
     */
    @Test
    public void assignCompanyShouldAssignCompanyWhenValid() {
        // Arrange
        final Building building = buildingService.findById(DataUtil.EXISTING_BUILDING_ID);
        final Company company = userService.findCompanyById(BuildingData.NON_ASSIGNED_COMPANY_ID);

        // Act
        buildingService.assignCompany(building, company);
    }

    /**
     * Test assign company to building when company is already assigned to building.
     * This should throw exception.
     */
    @Test(expected = BadRequestException.class)
    public void assignCompanyShouldThrowExceptionWhenCompanyIsAlreadyAdded() {
        // Arrange
        final Building building = buildingService.findById(DataUtil.EXISTING_BUILDING_ID);
        final Company company = userService.findCompanyById(BuildingData.COMPANY_ID);

        // Act
        buildingService.assignCompany(building, company);
    }

    /**
     * Test revoke company from building when assigned company is passed.
     * This should pass.
     */
    @Test
    public void revokeCompanyShouldBeValidWhenCompanyIsAdded() {
        // Arrange
        final Building building = buildingService.findById(DataUtil.EXISTING_BUILDING_ID);
        final Company company = userService.findCompanyById(BuildingData.COMPANY_ID);

        // Act
        buildingService.revokeCompany(building, company);
    }

    /**
     * Test revoke company from building when company is not assigned.
     * This should pass since the outcome is the same, i.e. company is not assigned.
     */
    @Test
    public void revokeCompanyShouldBeValidWhenCompanyIsNotAdded() {
        // Arrange
        final Building building = buildingService.findById(DataUtil.EXISTING_BUILDING_ID);
        final Company company = userService.findCompanyById(BuildingData.NON_ASSIGNED_COMPANY_ID);

        // Act
        buildingService.revokeCompany(building, company);
    }

    /**
     * Test check is address unique when no other building with the same address exists.
     * This should return false.
     */
    @Test
    public void checkIfAddressIsTakenShouldReturnFalseWhenAddressIsUnique() {
        // Act
        final boolean taken = buildingService.addressTaken(BuildingData.BUILDING_NON_EXISTENT_ADDRESS);

        // Assert
        assertFalse(taken);
    }

    /**
     * Test check is address unique when building with the same address exists.
     * This should return true.
     */
    @Test
    public void checkIfAddressIsTakenShouldReturnTrueWhenAddressIsTaken() {
        // Act
        final boolean taken = buildingService.addressTaken(BuildingData.BUILDING_EXISTING_ADDRESS);

        // Assert
        assertTrue(taken);
    }
}
