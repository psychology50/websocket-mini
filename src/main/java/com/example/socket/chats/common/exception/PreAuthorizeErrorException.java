package com.example.socket.chats.common.exception;

import com.example.socket.config.exception.CausedBy;
import com.example.socket.config.exception.GlobalErrorException;

public class PreAuthorizeErrorException extends GlobalErrorException {
    private final PreAuthorizeErrorCode errorCode;

    public PreAuthorizeErrorException(PreAuthorizeErrorCode preAuthorizeErrorCode) {
        super(preAuthorizeErrorCode);
        this.errorCode = preAuthorizeErrorCode;
    }

    public CausedBy causedBy() {
        return errorCode.causedBy();
    }

    public PreAuthorizeErrorCode getErrorCode() {
        return errorCode;
    }
}
