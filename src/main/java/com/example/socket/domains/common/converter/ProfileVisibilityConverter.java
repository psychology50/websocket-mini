package com.example.socket.domains.common.converter;

import com.example.socket.domains.user.type.ProfileVisibility;
import jakarta.persistence.Converter;

@Converter
public class ProfileVisibilityConverter extends AbstractLegacyEnumAttributeConverter<ProfileVisibility> {
    private static final String ENUM_NAME = "프로필 공개 범위";

    public ProfileVisibilityConverter() {
        super(ProfileVisibility.class, false, ENUM_NAME);
    }
}
