package com.example.socket.chats.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

public record ErrorMessage(
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String code,
        String reason
) {
    public ErrorMessage {
        Objects.requireNonNull(reason, "reason must not be null");
    }

    public static ErrorMessage of(String reason) {
        return new ErrorMessage(null, reason);
    }

    public static ErrorMessage of(String code, String reason) {
        return new ErrorMessage(code, reason);
    }
}
