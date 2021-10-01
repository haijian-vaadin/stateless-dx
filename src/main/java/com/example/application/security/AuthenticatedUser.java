package com.example.application.security;

import java.util.Collections;
import java.util.Optional;

import com.example.application.data.entity.User;
import com.example.application.data.service.UserRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.jwt.Jwt;

@Component
public class AuthenticatedUser {

    @Autowired
    private UserRepository userRepository;

    private UserDetails getAuthenticatedUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Object principal = context.getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) context.getAuthentication().getPrincipal();
            return userDetails;
        } else if (principal instanceof Jwt) {
            String userName = ((Jwt) principal).getClaim("sub");
            return new org.springframework.security.core.userdetails.User(userName, "", Collections.emptyList());
        }
        return null;
    }

    public Optional<User> get() {
        UserDetails details = getAuthenticatedUser();
        if (details == null) {
            return Optional.empty();
        }
        return Optional.of(userRepository.findByUsername(details.getUsername()));
    }

    public void logout() {
        UI.getCurrent().getPage().setLocation(SecurityConfiguration.LOGOUT_URL);
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(VaadinServletRequest.getCurrent().getHttpServletRequest(), null, null);
    }

}
