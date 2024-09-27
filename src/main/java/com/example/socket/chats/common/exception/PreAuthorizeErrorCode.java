package com.example.socket.chats.common.exception;

import com.example.socket.config.exception.BaseErrorCode;
import com.example.socket.config.exception.CausedBy;
import com.example.socket.config.exception.ReasonCode;
import com.example.socket.config.exception.StatusCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.example.socket.config.exception.ReasonCode.*;

@Getter
@RequiredArgsConstructor
public enum PreAuthorizeErrorCode implements BaseErrorCode {
    NOT_ANONYMOUS(StatusCode.UNAUTHORIZED, MISSING_OR_INVALID_AUTHENTICATION_CREDENTIALS, "인증되지 않은 사용자입니다."),
    UNAUTHENTICATED(StatusCode.UNAUTHORIZED, EXPIRED_OR_REVOKED_TOKEN, "인증되지 않은 사용자입니다."),
    FORBIDDEN(StatusCode.FORBIDDEN, ACCESS_TO_THE_REQUESTED_RESOURCE_IS_FORBIDDEN, "접근 권한이 없습니다.");

    private final StatusCode statusCode;
    private final ReasonCode reasonCode;
    private final String message;

    @Override
    public CausedBy causedBy() {
        return CausedBy.of(statusCode, reasonCode);
    }

    @Override
    public String getExplainError() throws NoSuchFieldError {
        return message;
    }
}
