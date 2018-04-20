package com.code10.kts.service;

import com.code10.kts.controller.exception.NotFoundException;
import com.code10.kts.model.domain.Building;
import com.code10.kts.model.domain.Malfunction;
import com.code10.kts.model.domain.user.User;
import com.code10.kts.repository.BuildingRepository;
import com.code10.kts.repository.MalfunctionRepository;
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
import static org.junit.Assert.assertNotNull;

/**
 * Tests {@link MalfunctionService} methods.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
@Transactional
public class MalfunctionServiceTest {

    @Autowired
    private MalfunctionService malfunctionService;

    @Autowired
    private MalfunctionRepository malfunctionRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Tests find by id when malfunction exists.
     * This should return malfunction with id.
     */
    @Test
    public void findByIdShouldReturnMalfunction() {
        // Act
        final Malfunction malfunction = malfunctionService.findById(DataUtil.EXISTING_MALFUNCTION_ID);

        // Assert
        assertNotNull(malfunction);
        assertEquals(DataUtil.EXISTING_MALFUNCTION_ID, malfunction.getId());
    }

    /**
     * Tests find by id when malfunction does not exist.
     * This should throw NotFoundException.
     */
    @Test(expected = NotFoundException.class)
    public void findByIdShouldThrowNotFoundExceptionWhenMalfunctionDoesNotExist() {
        // Arrange
        malfunctionRepository.delete(DataUtil.EXISTING_MALFUNCTION_ID);

        // Act
        malfunctionService.findById(DataUtil.EXISTING_MALFUNCTION_ID);
    }

    /**
     * Tests find by id and building id when malfunction exists.
     * This should return a malfunction.
     */
    @Test
    public void findByIdAndBuildingIdShouldReturnMalfunction() {
        // Arrange
        final Malfunction existing = malfunctionRepository.findById(DataUtil.EXISTING_MALFUNCTION_ID).orElseGet(null);
        final long buildingId = existing.getBuilding().getId();

        // Act
        final Malfunction malfunction = malfunctionService.findByIdAndBuildingId(buildingId,
                DataUtil.EXISTING_MALFUNCTION_ID);

        // Assert
        assertNotNull(malfunction);
        assertEquals(DataUtil.EXISTING_MALFUNCTION_ID, malfunction.getId());
    }

    /**
     * Tests find by id and building id when malfunction does not exist in building.
     * This should throw NotFoundException.
     */
    @Test(expected = NotFoundException.class)
    public void findByIdAndBuildingIdThrowNotFoundExceptionWhenMalfunctionDoesNotExistInBuilding() {
        // Arrange
        Building building = new Building();
        building.setAddress("test");
        building = buildingRepository.save(building);

        // Act
        malfunctionService.findByIdAndBuildingId(building.getId(),
                DataUtil.EXISTING_MALFUNCTION_ID);
    }

    /**
     * Tests update malfunction when data is valid.
     * This should update malfunction.
     */
    @Test
    public void updateShouldUpdateMalfunctionWhenValid() {
        // Arrange
        final String description = "new desc";
        final Malfunction malfunction = new Malfunction();
        malfunction.setDescription(description);

        final Malfunction existMalfunction = malfunctionRepository.findById(DataUtil.EXISTING_MALFUNCTION_ID).orElseGet(null);

        // Act
        final Malfunction updated = malfunctionService.update(DataUtil.EXISTING_MALFUNCTION_ID,
                existMalfunction.getBuilding().getId(), malfunction);

        // Assert
        assertEquals(description, updated.getDescription());
        assertEquals(existMalfunction.getWorkArea(), updated.getWorkArea());
    }

    /**
     * Tests update assignee.
     * This should update assignee.
     */
    @Test
    public void updateAssigneeShouldUpdateAssigneeWhenValid() {
        // Arrange
        Malfunction malfunction = malfunctionRepository.findById(DataUtil.EXISTING_MALFUNCTION_ID).orElseGet(null);
        malfunction.setAssignee(userRepository.findById(DataUtil.EXISTING_TENANT_ID).orElseGet(null));
        malfunction = malfunctionRepository.save(malfunction);

        final User user = userRepository.findById(DataUtil.EXISTING_TENANT_SUPERVISOR_ID).orElseGet(null);

        // Act
        final Malfunction updated = malfunctionService.updateAssignee(malfunction.getId(), malfunction.getBuilding().getId(), user);

        // Assert
        assertEquals(user.getId(), updated.getAssignee().getId());
    }

    /**
     * Tests update assignee when malfunction is not in building.
     * This should throw NotFoundException.
     */
    @Test(expected = NotFoundException.class)
    public void updateAssigneeShouldThrowNotFoundExceptionWhenMalfunctionDoesNotExistInBuilding() {
        // Arrange
        Building building = new Building();
        building.setAddress("test");
        building = buildingRepository.save(building);

        Malfunction malfunction = malfunctionRepository.findById(DataUtil.EXISTING_MALFUNCTION_ID).orElseGet(null);
        malfunction.setAssignee(userRepository.findById(DataUtil.EXISTING_TENANT_ID).orElseGet(null));
        malfunction = malfunctionRepository.save(malfunction);

        final User user = userRepository.findById(DataUtil.EXISTING_TENANT_SUPERVISOR_ID).orElseGet(null);

        // Act
        final Malfunction updated = malfunctionService.updateAssignee(malfunction.getId(), building.getId(), user);

        // Assert
        assertEquals(user.getId(), updated.getAssignee().getId());
    }
}
