package com.code10.kts.service;

import com.code10.kts.controller.exception.BadRequestException;
import com.code10.kts.controller.exception.ForbiddenException;
import com.code10.kts.data.AnnouncementData;
import com.code10.kts.model.domain.Announcement;
import com.code10.kts.model.domain.Malfunction;
import com.code10.kts.model.domain.user.Company;
import com.code10.kts.model.domain.user.Tenant;
import com.code10.kts.model.domain.user.User;
import com.code10.kts.repository.AnnouncementRepository;
import com.code10.kts.repository.MalfunctionRepository;
import com.code10.kts.repository.UserRepository;
import com.code10.kts.util.DataUtil;
import com.code10.kts.util.SecurityUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

/**
 * Tests {@link UserService} methods.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
@Transactional
public class UserServiceTest {

    @Autowired
    private MalfunctionService malfunctionService;

    @Autowired
    private MalfunctionRepository malfunctionRepository;

    @Autowired
    private BuildingService buildingService;

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Test find company by id with valid company id.
     * This should return company with that id.
     */
    @Test
    public void findCompanyByIdShouldReturnCompanyWithValidId() {
        // Act
        final Company company = userService.findCompanyById(DataUtil.EXISTING_COMPANY_ID);

        // Assert
        assertNotNull(company);
        assertEquals(DataUtil.EXISTING_COMPANY_ID, company.getId());
    }

    /**
     * Test find company by id when id is not id of a company.
     * This should throw ClassCastException exception because tenant cannot be cast to company.
     */
    @Test(expected = ClassCastException.class)
    public void findCompanyByIdShouldThrowExceptionWhenIdIsInvalid() {
        // Act
        userService.findCompanyById(DataUtil.EXISTING_TENANT_ID);
    }

    /**
     * Test find tenant by id with valid tenant id.
     * This should return tenant with that id.
     */
    @Test
    public void findTenantByIdShouldReturnTenantWithValidId() {
        // Act
        final Tenant tenant = userService.findTenantById(DataUtil.EXISTING_TENANT_ID);

        // Assert
        assertNotNull(tenant);
        assertEquals(DataUtil.EXISTING_TENANT_ID, tenant.getId());
    }

    /**
     * Test find tenant by id when id is not id of a tenant.
     * This should throw ClassCastException exception because company cannot be cast to tenant.
     */
    @Test(expected = ClassCastException.class)
    public void findTenantByIdShouldThrowExceptionWhenIdIsInvalid() {
        // Act
        userService.findTenantById(DataUtil.EXISTING_COMPANY_ID);
    }

    /**
     * Test find current user when user is logged in.
     * This should pass.
     */
    @Test
    public void findCurrentUserShouldReturnCurrentUserWhenUserIsLoggedIn() {
        // Arrange
        SecurityUtil.setAuthentication(DataUtil.EXISTING_BUILDING_TENANT_USERNAME);

        // Act
        final User user = userService.findCurrentUser();
        assertNotNull(user);
        assertEquals(DataUtil.EXISTING_BUILDING_TENANT_USERNAME, user.getUsername());
    }

    /**
     * Test find current user when user is not logged in.
     * This should throw NullPointer exception.
     */
    @Test(expected = ForbiddenException.class)
    public void findCurrentUserShouldThrowExceptionWhenUserIsNotLoggedIn() {
        // Act
        userService.findCurrentUser();
    }

    /**
     * Test is supervisor of building when user is supervisor.
     * This should return true.
     */
    @Test
    public void isSupervisorOfShouldReturnTrueWhenUserIsSupervisorOfBuilding() {
        // Arrange
        SecurityUtil.setAuthentication(DataUtil.EXISTING_BUILDING_SUPERVISOR_USERNAME);

        // Act
        final boolean isSupervisor = userService.isSupervisorOf(DataUtil.EXISTING_BUILDING_ID);
        assertTrue(isSupervisor);
    }

    /**
     * Test is supervisor of building when user is supervisor.
     * This should return true.
     */
    @Test
    public void isSupervisorOfShouldReturnFalseWhenUserIsNotSupervisorOfBuilding() {
        // Arrange
        SecurityUtil.setAuthentication(DataUtil.EXISTING_BUILDING_COMPANY_USERNAME);

        // Act
        final boolean isSupervisor = userService.isSupervisorOf(DataUtil.EXISTING_BUILDING_ID);
        assertFalse(isSupervisor);
    }

    /**
     * Test is tenant of building when user is tenant.
     * This should return true.
     */
    @Test
    public void isTenantOfShouldReturnTrueWhenUserIsTenantOfBuilding() {
        // Arrange
        SecurityUtil.setAuthentication(DataUtil.EXISTING_BUILDING_TENANT_USERNAME);

        // Act
        final boolean isTenant = userService.isTenantOf(DataUtil.EXISTING_BUILDING_ID);
        assertTrue(isTenant);
    }

    /**
     * Test is tenant of building when user is not tenant.
     * This should return false.
     */
    @Test
    public void isTenantOfShouldReturnFalseWhenUserIsNotTenantOfBuilding() {
        // Arrange
        SecurityUtil.setAuthentication(DataUtil.EXISTING_BUILDING_NON_TENANT_USERNAME);

        // Act
        final boolean isTenant = userService.isTenantOf(DataUtil.EXISTING_BUILDING_ID);
        assertFalse(isTenant);
    }

    /**
     * Test is company of building when company is assigned to building.
     * This should return true.
     */
    @Test
    public void isCompanyOfShouldReturnTrueWhenCompanyIsAssignedToBuilding() {
        // Arrange
        SecurityUtil.setAuthentication(DataUtil.EXISTING_BUILDING_COMPANY_USERNAME);

        // Act
        final boolean isAssigned = userService.isCompanyOf(DataUtil.EXISTING_BUILDING_ID);
        assertTrue(isAssigned);
    }

    /**
     * Test is company of building when company is not assigned to building.
     * This should return false.
     */
    @Test
    public void isCompanyOfShouldReturnFalseWhenCompanyIsNotAssignedToBuilding() {
        // Arrange
        SecurityUtil.setAuthentication(DataUtil.EXISTING_BUILDING_NON_COMPANY_USERNAME);

        // Act
        final boolean isAssigned = userService.isCompanyOf(DataUtil.EXISTING_BUILDING_ID);
        assertFalse(isAssigned);
    }

    /**
     * Test has access to building when user is tenant of this building.
     * This should return true.
     */
    @Test
    public void hasAccessToShouldReturnTrueWhenUserIsTenant() {
        // Arrange
        SecurityUtil.setAuthentication(DataUtil.EXISTING_BUILDING_TENANT_USERNAME);

        // Act
        final boolean hasAccessTo = userService.hasAccessTo(DataUtil.EXISTING_BUILDING_ID);
        assertTrue(hasAccessTo);
    }

    /**
     * Test has access to building when user is supervisor of this building.
     * This should return true.
     */
    @Test
    public void hasAccessToShouldReturnTrueWhenUserIsSupervisor() {
        // Arrange
        SecurityUtil.setAuthentication(DataUtil.EXISTING_BUILDING_SUPERVISOR_USERNAME);

        // Act
        final boolean hasAccessTo = userService.hasAccessTo(DataUtil.EXISTING_BUILDING_ID);
        assertTrue(hasAccessTo);
    }

    /**
     * Test has access to building when user is tenant of another building.
     * This should return false.
     */
    @Test
    public void hasAccessToShouldReturnFalseWhenUserIsTenantOfAnotherBuilding() {
        // Arrange
        SecurityUtil.setAuthentication(DataUtil.EXISTING_BUILDING_NON_TENANT_USERNAME);

        // Act
        final boolean hasAccessTo = userService.hasAccessTo(DataUtil.EXISTING_BUILDING_ID);
        assertFalse(hasAccessTo);
    }

    /**
     * Test can comment when user is authenticated as a company assigned to this building.
     * This should return true.
     */
    @Test
    public void canCommentShouldReturnTrueWhenUserIsCompanyAssignedToBuilding() {
        // Arrange
        SecurityUtil.setAuthentication(DataUtil.EXISTING_BUILDING_COMPANY_USERNAME);

        // Act
        final boolean canComment = userService.hasAccessToBuildingMalfunctions(DataUtil.EXISTING_BUILDING_ID);
        assertTrue(canComment);
    }

    /**
     * Test can comment when user is authenticated as a tenant of another building.
     * This should return false.
     */
    @Test
    public void canCommentShouldReturnFalseWhenUserIsTenantOfAnotherBuilding() {
        // Arrange
        SecurityUtil.setAuthentication(DataUtil.EXISTING_BUILDING_NON_TENANT_USERNAME);

        // Act
        final boolean canComment = userService.hasAccessToBuildingMalfunctions(DataUtil.EXISTING_BUILDING_ID);
        assertFalse(canComment);
    }

    /**
     * Test check can supervise when user is tenant of this building.
     * This should pass.
     */
    @Test
    public void checkCanSuperviseShouldPassWhenUserIsTenantOfBuilding() {
        // Act
        userService.checkCanSupervise(userService.findTenantById(DataUtil.EXISTING_TENANT_ID), DataUtil.EXISTING_BUILDING_ID);
    }

    /**
     * Test check can supervise when user is tenant of another building.
     * This should throw BadRequest exception.
     */
    @Test(expected = BadRequestException.class)
    public void checkCanSuperviseShouldThrowExceptionWhenUserIsTenantOfAnotherBuilding() {
        // Act
        userService.checkCanSupervise(userService.findTenantById(DataUtil.EXISTING_BUILDING_NON_TENANT_ID), DataUtil.EXISTING_BUILDING_ID);
    }

    /**
     * Test check can supervise when company is not in housekeeping (e.g. company manages heating).
     * This should throw BadRequest exception.
     */
    @Test(expected = BadRequestException.class)
    public void checkCanSuperviseShouldThrowExceptionWhenCompanyIsNotInHousekeeping() {
        // Act
        userService.checkCanSupervise(userService.findCompanyById(DataUtil.EXISTING_COMPANY_ID), DataUtil.EXISTING_BUILDING_ID);
    }

    /**
     * Test check is username unique when no other user with the same username exists.
     * This should pass.
     */
    @Test
    public void checkIsUsernameUniqueShouldPassWhenUsernameIsUnique() {
        // Act
        userService.checkIsUsernameUnique(DataUtil.NON_EXISTING_USERNAME);
    }

    /**
     * Test check is username unique when user with the same username already exists.
     * This should throw BadRequest exception.
     */
    @Test(expected = BadRequestException.class)
    public void checkIsUsernameUniqueShouldThrowExceptionWhenUsernameIsTaken() {
        // Act
        userService.checkIsUsernameUnique(DataUtil.EXISTING_ADMIN_USERNAME);
    }

    /**
     * Tests if user is creator of announcement when he is a creator.
     * This should return true.
     */
    @Test
    public void isCreatorOfAnnouncementShouldReturnTrueWhenUserIsCreator() {
        // Arrange
        final User user = userService.findById(DataUtil.EXISTING_TENANT_ID);
        final Announcement announcement = AnnouncementData.getValidAnnouncement(user, buildingService.findById(DataUtil.EXISTING_BUILDING_ID));
        announcementRepository.save(announcement);

        SecurityUtil.setAuthentication(user.getUsername());

        // Act
        final boolean isCreator = userService.isCreatorOf(announcement);

        // Assert
        assertTrue(isCreator);
    }

    /**
     * Tests if user is creator of announcement when he is not a creator.
     * This should return false.
     */
    @Test
    public void isCreatorOfAnnouncementShouldReturnFalseWhenUserIsNotCreator() {
        // Arrange
        final Announcement announcement = AnnouncementData.getValidAnnouncement(userService.findById(DataUtil.EXISTING_TENANT_ID),
                buildingService.findById(DataUtil.EXISTING_BUILDING_ID));
        announcementRepository.save(announcement);

        SecurityUtil.setAuthentication(userService.findById(DataUtil.EXISTING_TENANT_SUPERVISOR_ID).getUsername());

        // Act
        final boolean isCreator = userService.isCreatorOf(announcement);

        // Assert
        assertFalse(isCreator);
    }

    /**
     * Tests if user is creator of malfunction when he is a creator.
     * This should return true.
     */
    @Test
    public void isCreatorOfMalfunctionShouldReturnTrueWhenUserIsCreator() {
        // Arrange
        final Tenant user = userService.findTenantById(DataUtil.EXISTING_TENANT_SUPERVISOR_ID);
        final Malfunction malfunction = malfunctionRepository.findById(DataUtil.EXISTING_MALFUNCTION_ID).orElseGet(null);
        malfunction.setCreator(user);
        malfunctionRepository.save(malfunction);

        SecurityUtil.setAuthentication(user.getUsername());

        // Act
        final boolean result = userService.isCreatorOf(malfunctionService.findById(DataUtil.EXISTING_MALFUNCTION_ID));

        // Assert
        assertTrue(result);
    }

    /**
     * Tests if user is creator of malfunction when he is not a creator.
     * This should return false.
     */
    @Test
    public void isCreatorOfMalfunctionShouldReturnFalseWhenUserIsNotCreator() {
        // Arrange
        final Tenant user = userService.findTenantById(DataUtil.EXISTING_TENANT_SUPERVISOR_ID);
        final Malfunction malfunction = malfunctionRepository.findById(DataUtil.EXISTING_MALFUNCTION_ID).orElseGet(null);
        malfunction.setCreator(user);
        malfunctionRepository.save(malfunction);

        SecurityUtil.setAuthentication(userRepository.findById(DataUtil.EXISTING_TENANT_ID).orElseGet(null).getUsername());

        // Act
        final boolean result = userService.isCreatorOf(malfunctionService.findById(DataUtil.EXISTING_MALFUNCTION_ID));

        // Assert
        assertFalse(result);
    }

    /**
     * Tests if user is assignee for malfunction when he is an assignee.
     * This should return true.
     */
    @Test
    public void isAssigneeOfMalfunctionShouldReturnTrueWhenUserIsAssignee() {
        // Arrange
        final Tenant user = userService.findTenantById(DataUtil.EXISTING_TENANT_SUPERVISOR_ID);
        final Malfunction malfunction = malfunctionRepository.findById(DataUtil.EXISTING_MALFUNCTION_ID).orElseGet(null);
        malfunction.setAssignee(user);
        malfunctionRepository.save(malfunction);

        SecurityUtil.setAuthentication(user.getUsername());

        // Act
        final boolean result = userService.isAssigneeOf(malfunctionService.findById(DataUtil.EXISTING_MALFUNCTION_ID));

        // Assert
        assertTrue(result);
    }

    /**
     * Tests if user is assignee for malfunction when he is not an assignee.
     * This should return false.
     */
    @Test
    public void isAssigneeOfMalfunctionShouldReturnFalseWhenUserIsNotAssignee() {
        // Arrange
        final Tenant user = userService.findTenantById(DataUtil.EXISTING_TENANT_SUPERVISOR_ID);
        final Malfunction malfunction = malfunctionRepository.findById(DataUtil.EXISTING_MALFUNCTION_ID).orElseGet(null);
        malfunction.setAssignee(user);
        malfunctionRepository.save(malfunction);

        SecurityUtil.setAuthentication(userRepository.findById(DataUtil.EXISTING_TENANT_ID).orElseGet(null).getUsername());

        // Act
        final boolean result = userService.isAssigneeOf(malfunctionService.findById(DataUtil.EXISTING_MALFUNCTION_ID));

        // Assert
        assertFalse(result);
    }
}
