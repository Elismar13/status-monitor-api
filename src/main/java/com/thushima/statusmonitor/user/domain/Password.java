package com.thushima.statusmonitor.user.domain;

import java.util.regex.Pattern;

public record Password(String value) {
    private static final Pattern BCRYPT_PATTERN =
            Pattern.compile("\\A\\$2[ayb]\\$\\d{2}\\$[./0-9A-Za-z]{53}");

    public Password {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        if (!isHashed(value) && value.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
    }

    public Password(String hashedValue, boolean isHashed) {
        this(hashedValue);
        if (!isHashed) {
            throw new IllegalArgumentException("Hashed password expected");
        }
    }

    public static boolean isHashed(String value) {
        return BCRYPT_PATTERN.matcher(value).matches();
    }

}