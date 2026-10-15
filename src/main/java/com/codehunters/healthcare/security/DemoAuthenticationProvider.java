package com.codehunters.healthcare.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * DEMO-ONLY: DemoAuthenticationProvider creates a demo session for evaluation/testing.
 * This provider is NOT used in production and must be removed before deployment.
 * Demo users are NOT stored in the database and only exist in the current session.
 */
@Component
public class DemoAuthenticationProvider implements AuthenticationProvider {

    // DEMO-ONLY: Hardcoded demo username — must be stripped before production
    private static final String DEMO_USERNAME = "demo";
    
    // DEMO-ONLY: Hardcoded demo password — must be stripped before production
    private static final String DEMO_PASSWORD = "demo";

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        // DEMO-ONLY: Check for demo credentials
        if (!DEMO_USERNAME.equals(username) || !DEMO_PASSWORD.equals(password)) {
            return null; // Not a demo authentication, delegate to other providers
        }

        // DEMO-ONLY: Create a demo patient session
        List<SimpleGrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_PATIENT"),
            new SimpleGrantedAuthority("DEMO_USER")
        );

        return new UsernamePasswordAuthenticationToken(
            "demo@carelink.app", // Use as principal (email-like identifier)
            null,
            authorities
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
