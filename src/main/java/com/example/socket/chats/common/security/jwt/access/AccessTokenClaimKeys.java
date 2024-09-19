package com.example.socket.chats.common.security.jwt.access;

public enum AccessTokenClaimKeys {
    USER_ID("id"),
    ROLE("role");

    private final String value;

    AccessTokenClaimKeys(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}