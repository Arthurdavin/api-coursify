package com.coursify.util;

import com.coursify.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Utility for reading the authenticated user's ID from the Spring Security context.
 *
 * This works when your JwtAuthFilter sets the principal as either:
 *   (a) your custom UserDetailsImpl that has a getId() method, or
 *   (b) a standard UserDetails where getUsername() returns the user's ID as a string.
 *
 * Adjust the cast below to match how your JwtAuthFilter sets the principal.
 */
public class SecurityUtils {

    private SecurityUtils() {}

    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("Not authenticated.");
        }

        Object principal = auth.getPrincipal();

        // Option A: Your UserDetailsImpl implements a getId() method
        // Cast to your custom type, e.g.:
        // if (principal instanceof UserDetailsImpl u) return u.getId();

        // Option B: Username is stored as the user's ID (common pattern with JWT)
        if (principal instanceof UserDetails ud) {
            try {
                return Long.parseLong(ud.getUsername());
            } catch (NumberFormatException e) {
                throw new UnauthorizedException("Cannot resolve user ID from principal.");
            }
        }

        // Option C: Principal is stored directly as a string ID
        if (principal instanceof String s) {
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException e) {
                throw new UnauthorizedException("Cannot resolve user ID from principal.");
            }
        }

        throw new UnauthorizedException("Unsupported principal type: " + principal.getClass());
    }
}