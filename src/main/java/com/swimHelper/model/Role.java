package com.swimHelper.model;

import org.springframework.security.core.GrantedAuthority;

/**
 * Created by Marcin Szalek on 23.08.17.
 */
public enum Role implements GrantedAuthority {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    public static final String NAME_USER = "USER";
    public static final String NAME_ADMIN = "ADMIN";

    private final String value;

    Role(String value) {
        this.value = value;
    }

    @Override
    public String getAuthority() {
        return value;
    }
}
