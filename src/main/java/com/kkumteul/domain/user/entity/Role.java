package com.kkumteul.domain.user.entity;

public enum Role {
    ROLE_ADMIN,
    ROLE_USER;

    public static Role from(String s) {
        return Role.valueOf(s.toUpperCase());
    }
}