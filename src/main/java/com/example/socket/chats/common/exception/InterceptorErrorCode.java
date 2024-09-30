package com.example.socket.chats.common.exception;

import com.example.socket.config.exception.BaseErrorCode;
import com.example.socket.config.exception.CausedBy;
import com.example.socket.config.exception.ReasonCode;
import com.example.socket.config.exception.StatusCode;

public enum InterceptorErrorCode implements BaseErrorCode {
    // 400  
    INVALID_DESTINATION(StatusCode.BAD_REQUEST, ReasonCode.INVALID_REQUEST, "유효하지 않은 목적지입니다"),
    
    // 403
    UNAUTHORIZED_TO_SUBSCRIBE(StatusCode.FORBIDDEN, ReasonCode.ACCESS_TO_THE_REQUESTED_RESOURCE_IS_FORBIDDEN, "해당 주제에 대한 구독 권한이 없습니다")
    ;

    private StatusCode statusCode;
    private ReasonCode reasonCode;
    private String message;

    InterceptorErrorCode(StatusCode statusCode, ReasonCode reasonCode, String message) {
        this.statusCode = statusCode;
        this.reasonCode = reasonCode;
        this.message = message;
    }

    @Override
    public CausedBy causedBy() {
        return CausedBy.of(statusCode, reasonCode);
    }

    @Override
    public String getExplainError() throws NoSuchFieldError {
        return message;
    }
}
