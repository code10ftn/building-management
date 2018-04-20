package com.code10.kts.model.factory;

import com.code10.kts.model.domain.user.User;
import com.code10.kts.model.security.UserDetailsImpl;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Factory for creating instance of {@link UserDetailsImpl}.
 */
public class UserDetailsFactory {

    private UserDetailsFactory() {
    }

    /**
     * Creates UserDetailsImpl from a user.
     *
     * @param user user model
     * @return UserDetailsImpl
     */
    public static UserDetailsImpl create(User user) {
        Collection<? extends GrantedAuthority> authorities;
        try {
            authorities = user.getAuthorities().stream().map(a -> new SimpleGrantedAuthority(a.toString())).collect(Collectors.toList());
        } catch (Exception e) {
            authorities = null;
        }

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getLastPasswordReset(),
                authorities
        );
    }
}
