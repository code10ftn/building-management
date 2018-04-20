package com.code10.kts.service;

import com.code10.kts.controller.exception.NotFoundException;
import com.code10.kts.data.AnnouncementData;
import com.code10.kts.model.domain.Announcement;
import com.code10.kts.model.domain.user.User;
import com.code10.kts.repository.AnnouncementRepository;
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

/**
 * Tests {@link AnnouncementService} methods.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
@Transactional
public class AnnouncementServiceTest {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private UserService userService;

    @Autowired
    private BuildingService buildingService;

    /**
     * Test find by id and building id with valid id and building id.
     * This should return announcement.
     */
    @Test
    public void findByIdAndBuildingIdShouldReturnAllWhenValid() {
        // Arrange
        Announcement announcement = AnnouncementData.getValidAnnouncement(userService.findById(DataUtil.EXISTING_TENANT_ID),
                buildingService.findById(DataUtil.EXISTING_BUILDING_ID));
        announcement = announcementRepository.save(announcement);

        // Act
        final Announcement persisted = announcementService.findByIdAndBuildingId(announcement.getId(), DataUtil.EXISTING_BUILDING_ID);

        // Assert
        assertNotNull(persisted);
        assertEquals(announcement.getId(), persisted.getId());
        assertEquals(DataUtil.EXISTING_BUILDING_ID, persisted.getBuilding().getId());
        assertEquals(announcement.getContent(), persisted.getContent());
    }

    /**
     * Test find by id and building id when announcement id is not valid.
     * This should throw NotFound exception.
     */
    @Test(expected = NotFoundException.class)
    public void findByIdAndBuildingIdShouldThrowExceptionWhenIdInvalid() {
        // Arrange
        final Announcement announcement = AnnouncementData.getValidAnnouncement(userService.findById(DataUtil.EXISTING_TENANT_ID),
                buildingService.findById(DataUtil.EXISTING_BUILDING_ID));
        announcementRepository.save(announcement);

        // Act
        announcementService.findByIdAndBuildingId(AnnouncementData.NON_EXISTENT_ANNOUNCEMENT_ID, DataUtil.EXISTING_BUILDING_ID);
    }

    /**
     * Test create announcement with valid announcement data.
     * This should return ok and the created announcement.
     */
    @Test
    public void createShouldReturnCreatedAnnouncementWhenValid() {
        // Arrange
        final Announcement announcement = AnnouncementData.getValidAnnouncement(userService.findById(DataUtil.EXISTING_TENANT_ID),
                buildingService.findById(DataUtil.EXISTING_BUILDING_ID));

        // Act
        final Announcement persisted = announcementService.create(announcement);

        // Assert
        assertNotNull(persisted);
        assertNotNull(persisted.getId());
        assertEquals(announcement.getContent(), persisted.getContent());
        assertEquals(announcement.getBuilding(), persisted.getBuilding());
        assertEquals(announcement.getAuthor(), persisted.getAuthor());
    }

    /**
     * Test create when announcement content is null.
     * This should throw DataIntegrityViolation exception since content cannot be null.
     */
    @Test(expected = DataIntegrityViolationException.class)
    public void createShouldThrowExceptionWhenContentIsNull() {
        // Arrange
        final Announcement announcement = AnnouncementData.getValidAnnouncement(userService.findById(DataUtil.EXISTING_TENANT_ID),
                buildingService.findById(DataUtil.EXISTING_BUILDING_ID));
        announcement.setContent(null);

        // Act
        announcementService.create(announcement);
    }

    /**
     * Test delete announcement with valid announcement id.
     * This should return ok and announcement should no longer exist in a database.
     */
    @Test
    public void deleteShouldDeleteAnnouncementWhenValid() {
        // Arrange
        final User currentUser = userService.findById(DataUtil.EXISTING_TENANT_ID);
        Announcement persisted = AnnouncementData.getValidAnnouncement(currentUser,
                buildingService.findById(DataUtil.EXISTING_BUILDING_ID));
        persisted = announcementRepository.save(persisted);

        // Act
        announcementService.delete(persisted.getId(), DataUtil.EXISTING_BUILDING_ID);

        // Assert
        final Announcement deleted = announcementRepository.findOne(persisted.getId());
        assertNull(deleted);
    }
}
