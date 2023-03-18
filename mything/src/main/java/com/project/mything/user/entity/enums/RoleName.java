package com.project.mything.user.entity.enums;

import lombok.Getter;

@Getter
public enum RoleName {
    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN");

    private final String name;

    RoleName(String name) {
        this.name = name;
    }
}
