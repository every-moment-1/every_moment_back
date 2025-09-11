package com.rookies4.every_moment.chat;

import java.security.Principal;

public record UserPrincipal(String userEmail) implements Principal {
    @Override public String getName() { return String.valueOf(userEmail); }
}