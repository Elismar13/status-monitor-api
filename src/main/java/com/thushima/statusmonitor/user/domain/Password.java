package com.thushima.statusmonitor.user.domain;

import org.springframework.security.crypto.bcrypt.BCrypt;

public record Password(String value) {
    public Password {
        if (value == null || value.length() < 8) {
            throw new IllegalArgumentException("Invalid password.");
        }
    }

    public Password(String hashedValue, boolean alreadyHashed) {
        this(hashedValue);
    }

    public String hash() {
        return BCrypt.hashpw(value, BCrypt.gensalt());
    }
}