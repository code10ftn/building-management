package com.code10.kts.util;

import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Utility class for mocking authentication related objects.
 */
public class SecurityUtil {

    /**
     * Mocks the {@link SecurityContext} which is used for holding the currently logged in user.
     *
     * @param username user's username
     */
    public static void setAuthentication(String username) {
        final Authentication authentication = Mockito.mock(UsernamePasswordAuthenticationToken.class);
        final SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        final UserDetails userDetails = Mockito.mock(UserDetails.class);

        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
        Mockito.when(userDetails.getUsername()).thenReturn(username);

        SecurityContextHolder.setContext(securityContext);
    }
}
