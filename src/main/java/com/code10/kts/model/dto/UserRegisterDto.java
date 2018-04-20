package com.code10.kts.model.dto;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Represents new user.
 */
public class UserRegisterDto {

    /**
     * User's username.
     */
    @NotEmpty
    protected String username;

    /**
     * User's password.
     */
    @NotEmpty
    protected String password;

    /**
     * User's email.
     */
    @NotEmpty
    protected String email;

    /**
     * User's phone number.
     */
    @NotEmpty
    protected String phoneNumber;

    public UserRegisterDto() {
    }

    public UserRegisterDto(String username, String password, String email, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
