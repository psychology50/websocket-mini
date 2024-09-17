package com.example.socket.infra.common.exception;

import com.example.socket.config.exception.CausedBy;
import com.example.socket.config.exception.GlobalErrorException;

public class JwtErrorException extends GlobalErrorException {
    private final JwtErrorCode errorCode;

    public JwtErrorException(JwtErrorCode jwtErrorCode) {
        super(jwtErrorCode);
        this.errorCode = jwtErrorCode;
    }

    public CausedBy causedBy() {
        return errorCode.causedBy();
    }

    public JwtErrorCode getErrorCode() {
        return errorCode;
    }
}
