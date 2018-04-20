package com.code10.kts.service;

import com.code10.kts.controller.exception.AuthorizationException;
import com.code10.kts.controller.exception.BadRequestException;
import com.code10.kts.controller.exception.ForbiddenException;
import com.code10.kts.controller.exception.NotFoundException;
import com.code10.kts.model.domain.Announcement;
import com.code10.kts.model.domain.Building;
import com.code10.kts.model.domain.Malfunction;
import com.code10.kts.model.domain.WorkArea;
import com.code10.kts.model.domain.user.Company;
import com.code10.kts.model.domain.user.Tenant;
import com.code10.kts.model.domain.user.User;
import com.code10.kts.repository.BuildingRepository;
import com.code10.kts.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for users' business logic.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    private final BuildingRepository buildingRepository;

    @Autowired
    public UserService(UserRepository userRepository, BuildingRepository buildingRepository) {
        this.userRepository = userRepository;
        this.buildingRepository = buildingRepository;
    }

    /**
     * Gets all users.
     *
     * @return list of all users
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Gets a user by its ID.
     *
     * @param id user ID
     * @return user with matching ID
     */
    public User findById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("No user with ID %s found!", id)));
    }

    /**
     * Gets a company by its ID.
     *
     * @param id company ID
     * @return company with matching ID
     */
    public Company findCompanyById(long id) {
        return (Company) findById(id);
    }

    /**
     * Gets a tenant by its ID.
     *
     * @param id tenant ID
     * @return tenant with matching ID
     */
    public Tenant findTenantById(long id) {
        return (Tenant) findById(id);
    }

    /**
     * Gets a user currently logged in.
     * If no user is logged in, throws {@link ForbiddenException}.
     *
     * @return user currently logged in
     */
    public User findCurrentUser() {
        final UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new ForbiddenException("User is not authenticated!");
        }

        final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return findByUsername(userDetails.getUsername());
    }

    /**
     * Gets a user by its username.
     *
     * @param username username
     * @return user with given username
     */
    public User findByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username).orElseThrow(AuthorizationException::new);
    }

    /**
     * Checks if current user is supervisor of a building.
     *
     * @param buildingId building ID
     * @return true if user is supervisor
     */
    public boolean isSupervisorOf(long buildingId) {
        final User user = findCurrentUser();
        final Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new NotFoundException(String.format("No building with ID %s found!", buildingId)));
        return building.getSupervisor() != null && user.getId().equals(building.getSupervisor().getId());
    }

    /**
     * Checks if current user is tenant of a building.
     *
     * @param buildingId building ID
     * @return true if user is tenant
     */
    public boolean isTenantOf(long buildingId) {
        final User user = findCurrentUser();
        if (user instanceof Tenant) {
            final Tenant tenant = (Tenant) user;
            return tenant.getApartment() != null && tenant.getApartment().getBuilding().getId() == buildingId;
        } else {
            return false;
        }
    }

    /**
     * Checks if current user is company assigned to a building.
     *
     * @param buildingId building ID
     * @return true if company is assigned to building
     */
    public boolean isCompanyOf(long buildingId) {
        final User user = findCurrentUser();

        if (user instanceof Company) {
            final Company company = (Company) user;
            return company.getBuildings().stream().anyMatch(b -> b.getId() == buildingId);
        } else {
            return false;
        }
    }

    /**
     * Checks if current user is either a supervisor or a tenant of a building.
     *
     * @param buildingId building ID
     * @return true if user is supervisor or tenant
     */
    public boolean hasAccessTo(long buildingId) {
        // Building's supervisor and tenants can access the building.
        return isSupervisorOf(buildingId) || isTenantOf(buildingId);
    }

    /**
     * Checks if user has access to a malfunction.
     *
     * @param buildingId building ID
     * @return true if user is supervisor or tenant of a building or a company assigned to building
     */
    public boolean hasAccessToBuildingMalfunctions(long buildingId) {
        return hasAccessTo(buildingId) || isCompanyOf(buildingId);
    }

    /**
     * Checks if user can be a supervisor of a building.
     * If user is not a company that does HOUSEKEEPING or a tenant from another building, {@link BadRequestException} is thrown.
     *
     * @param user       user ID
     * @param buildingId building ID
     */
    public void checkCanSupervise(User user, long buildingId) {
        // All companies and building's tenants can become a supervisor.
        if (user instanceof Tenant && (((Tenant) user).getApartment() == null || ((Tenant) user).getApartment().getBuilding().getId() != buildingId)) {
            throw new BadRequestException("Supervising tenant must live in the building!");
        }
        if (user instanceof Company && ((Company) user).getWorkArea() != WorkArea.HOUSEKEEPING) {
            throw new BadRequestException("Supervising company must be in the housekeeping work area!");
        }
    }

    /**
     * Checks if username is unique.
     * If user with the same username already exists {@link BadRequestException} is thrown.
     *
     * @param username username
     */
    public void checkIsUsernameUnique(String username) {
        final Optional user = userRepository.findByUsernameIgnoreCase(username);
        if (user.isPresent()) {
            throw new BadRequestException(String.format("Username '%s' is taken!", username));
        }
    }

    /**
     * Checks if username is taken.
     *
     * @param username username
     * @return true if taken
     */
    public boolean usernameTaken(String username) {
        final Optional user = userRepository.findByUsernameIgnoreCase(username);
        return user.isPresent();
    }

    /**
     * Checks if user is creator of a announcement.
     *
     * @param announcement announcement
     * @return true if user is creator of a announcement
     */
    public boolean isCreatorOf(Announcement announcement) {
        final User user = findCurrentUser();
        return announcement.getAuthor().getId().equals(user.getId());
    }

    /**
     * Checks if user is creator of a malfunction.
     *
     * @param malfunction malfunction
     * @return true if user is creator of a malfunction
     */
    public boolean isCreatorOf(Malfunction malfunction) {
        final User user = findCurrentUser();
        return malfunction.getCreator().getId().equals(user.getId());
    }

    /**
     * Checks if user is assignee of a malfunction.
     *
     * @param malfunction malfunction
     * @return true if user is assignee of a malfunction
     */
    public boolean isAssigneeOf(Malfunction malfunction) {
        final User user = findCurrentUser();
        return malfunction.getAssignee().getId().equals(user.getId());
    }
}
