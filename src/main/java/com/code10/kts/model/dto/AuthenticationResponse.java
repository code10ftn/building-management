package com.code10.kts.model.dto;

import com.code10.kts.model.domain.user.User;

/**
 * Represents authentication response.
 * After user successfully logs this response is sent.
 */
public class AuthenticationResponse {

    /**
     * User that requested authentication.
     */
    private User user;

    /**
     * User's token.
     */
    private String token;

    public AuthenticationResponse() {
    }

    public AuthenticationResponse(User user, String token) {
        this.user = user;
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
