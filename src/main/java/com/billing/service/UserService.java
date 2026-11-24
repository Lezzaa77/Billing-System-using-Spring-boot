package com.billing.service;

import com.billing.dto.SignupForm;
import com.billing.model.AppUser;
import com.billing.repository.UserRepository;
import java.util.Locale;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean emailTaken(String email) {
        return users.existsByEmail(normalize(email));
    }

    @Transactional
    public void register(SignupForm form) {
        AppUser user = new AppUser();
        user.setFullName(form.getFullName().trim());
        user.setEmail(normalize(form.getEmail()));
        user.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        users.save(user);
    }

    public Optional<AppUser> findByEmail(String email) {
        return users.findByEmail(normalize(email));
    }

    private static String normalize(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}
