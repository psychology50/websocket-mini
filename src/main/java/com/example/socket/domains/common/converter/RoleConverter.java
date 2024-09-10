package com.example.socket.domains.common.converter;

import com.example.socket.domains.user.type.Role;
import jakarta.persistence.Converter;

@Converter
public class RoleConverter extends AbstractLegacyEnumAttributeConverter<Role> {
    private static final String ENUM_NAME = "유저 권한";

    public RoleConverter() {
        super(Role.class, false, ENUM_NAME);
    }
}
