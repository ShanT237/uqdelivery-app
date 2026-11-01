package com.uqdelivery.backend.infrastructure.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private final JwtTokenProvider tokenProvider =
            new JwtTokenProvider("test-secret-key-with-at-least-32-characters", 60_000);

    @Test
    void generatedTokenContainsEmailAndRoleAndIsValid() {
        String token = tokenProvider.generateToken("ana@example.com", "USER");

        assertTrue(tokenProvider.isTokenValid(token));
        assertEquals("ana@example.com", tokenProvider.getEmailFromToken(token));
        assertEquals("USER", tokenProvider.getRoleFromToken(token));
    }

    @Test
    void malformedTokenIsInvalid() {
        assertFalse(tokenProvider.isTokenValid("not-a-jwt"));
    }
}
