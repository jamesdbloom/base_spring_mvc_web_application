package org.jamesdbloom.security;

import org.jamesdbloom.domain.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * @author jamesdbloom
 */
@Component
public class SpringSecurityUserContext {

    public User getCurrentUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null || authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }
        return (User) authentication.getPrincipal();
    }

    public void setCurrentUser(User user) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
