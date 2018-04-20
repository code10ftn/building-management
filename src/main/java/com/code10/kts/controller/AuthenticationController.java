package com.code10.kts.controller;

import com.code10.kts.model.domain.user.User;
import com.code10.kts.model.dto.AuthenticationRequest;
import com.code10.kts.model.dto.AuthenticationResponse;
import com.code10.kts.security.TokenUtils;
import com.code10.kts.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * REST controller for authentication.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final UserDetailsService userDetailsService;

    private final UserService userService;

    private final TokenUtils tokenUtils;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService,
                                    UserService userService, TokenUtils tokenUtils) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.tokenUtils = tokenUtils;
    }

    /**
     * POST /api/auth
     * Authenticates a user in the system.
     *
     * @param authenticationRequest DTO with user's login credentials
     * @return ResponseEntity with a JSON Web Token
     */
    @PostMapping
    public ResponseEntity authenticate(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final User user = userService.findByUsername(userDetails.getUsername());
        final String token = tokenUtils.generateToken(userDetails);
        return new ResponseEntity<>(new AuthenticationResponse(user, token), HttpStatus.OK);
    }

    /**
     * GET /api/auth
     * Checks if username is taken.
     *
     * @param username username
     * @return ResponseEntity with true if taken
     */
    @GetMapping
    public ResponseEntity usernameTaken(@RequestParam String username) {
        return new ResponseEntity<>(userService.usernameTaken(username), HttpStatus.OK);
    }

    /**
     * GET /api/auth/me
     * Gets User object of user that's sending the request.
     *
     * @return User
     */
    @GetMapping("/me")
    public ResponseEntity findCurrentUser() {
        return new ResponseEntity<>(userService.findCurrentUser(), HttpStatus.OK);
    }
}
