package com.billing.controller;

import com.billing.model.AppUser;
import com.billing.repository.UserRepository;
import java.security.Principal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Puts the logged-in user's name into every page (the top bar shows it). */
@ControllerAdvice
public class GlobalModelAttributes {

    private final UserRepository users;

    public GlobalModelAttributes(UserRepository users) {
        this.users = users;
    }

    @ModelAttribute("currentUserName")
    public String currentUserName(Principal principal) {
        if (principal == null) {
            return null;
        }
        return users.findByEmail(principal.getName())
                .map(AppUser::getFullName)
                .orElse(principal.getName());
    }
}
