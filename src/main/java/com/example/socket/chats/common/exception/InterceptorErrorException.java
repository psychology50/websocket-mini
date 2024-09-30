package com.example.socket.chats.common.exception;

import com.example.socket.config.exception.BaseErrorCode;
import com.example.socket.config.exception.CausedBy;
import com.example.socket.config.exception.GlobalErrorException;

public class InterceptorErrorException extends GlobalErrorException {
    private final InterceptorErrorCode errorCode;

    public InterceptorErrorException(InterceptorErrorCode baseErrorCode) {
        super(baseErrorCode);
        this.errorCode = baseErrorCode;
    }

    public CausedBy causedBy() {
        return errorCode.causedBy();
    }

    public InterceptorErrorCode getErrorCode() {
        return errorCode;
    }
}
