//package com.coursify.util;
//
//import com.coursify.exception.UnauthorizedException;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//
//public class SecurityUtils {
//    private SecurityUtils() {}
//    public static Long getCurrentUserId() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth == null || !auth.isAuthenticated()) {
//            throw new UnauthorizedException("Not authenticated.");
//        }
//        Object principal = auth.getPrincipal();
//        if (principal instanceof UserDetails ud) {
//            try {
//                return Long.parseLong(ud.getUsername());
//            } catch (NumberFormatException e) {
//                throw new UnauthorizedException("Cannot resolve user ID from principal.");
//            }
//        }
//        if (principal instanceof String s) {
//            try {
//                return Long.parseLong(s);
//            } catch (NumberFormatException e) {
//                throw new UnauthorizedException("Cannot resolve user ID from principal.");
//            }
//        }
//
//        throw new UnauthorizedException("Unsupported principal type: " + principal.getClass());
//    }
//}

package com.coursify.util;

import com.coursify.domain.User;
import com.coursify.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    private SecurityUtils() {}

    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("Not authenticated.");
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof User user) {
            return user.getId(); // ✅ direct, no parsing needed
        }
        throw new UnauthorizedException("Unsupported principal type: " + principal.getClass());
    }
}