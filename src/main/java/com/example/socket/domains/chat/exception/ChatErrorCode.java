package com.example.socket.domains.chat.exception;

import com.example.socket.config.exception.BaseErrorCode;
import com.example.socket.config.exception.CausedBy;
import com.example.socket.config.exception.ReasonCode;
import com.example.socket.config.exception.StatusCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatErrorCode implements BaseErrorCode {
    SAVE_ERROR(StatusCode.INTERNAL_SERVER_ERROR, ReasonCode.UNEXPECTED_ERROR, "채팅 저장 중 오류가 발생하였습니다."),
    ;

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
