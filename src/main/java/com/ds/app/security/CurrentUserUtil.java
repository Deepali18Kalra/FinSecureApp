package com.ds.app.security;

import java.security.Principal;

import org.springframework.stereotype.Component;

import com.ds.app.entity.AppUser;
import com.ds.app.repository.iAppUserRepository;

@Component
public class CurrentUserUtil {

    private final iAppUserRepository userRepository;

    public CurrentUserUtil(iAppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AppUser getCurrentUser(Principal principal) {
        if (principal == null) {
            throw new RuntimeException("No authenticated user found");
        }

        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found: " + principal.getName()));
    }
}