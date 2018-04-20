package com.code10.kts.service;

import com.code10.kts.model.domain.user.User;
import com.code10.kts.repository.UserRepository;
import com.code10.kts.service.impl.UserDetailsServiceImpl;
import com.code10.kts.util.DataUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests {@link UserDetailsServiceImpl} methods.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
@Transactional
public class UserDetailsServiceImplTest {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Tests load by username when user exists.
     * Should return user details.
     */
    @Test
    public void loadUserByUsernameShouldReturnUserDetailsWhenExists() {
        // Arrange
        final User user = userRepository.findById(DataUtil.EXISTING_TENANT_ID).orElseGet(null);

        // Act
        final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

        // Assert
        assertNotNull(userDetails);
        assertEquals(user.getUsername(), userDetails.getUsername());
    }

    /**
     * Tests load by username when user does not exist.
     * Should throw UsernameNotFoundException.
     */
    @Test(expected = UsernameNotFoundException.class)
    public void loadUserByUsernameShouldThrowExceptionWhenUserDoesNotExist() {
        // Arrange
        final User user = userRepository.findByUsernameIgnoreCase(DataUtil.EXISTING_ADMIN_USERNAME).orElseGet(null);
        userRepository.delete(user.getId());

        // Act
        final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

        // Assert
        assertNotNull(userDetails);
        assertEquals(user.getUsername(), userDetails.getUsername());
    }
}
