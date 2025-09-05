package com.rookies4.every_moment.user;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository users;

    public UserService(UserRepository users) {
        this.users = users;
    }

    public User getCurrentUser(Authentication auth) {
        String email = auth.getName(); // we set email as principal username
        return users.findByEmail(email).orElseThrow();
    }
}