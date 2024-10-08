package com.example.socket.infra.common.exception;

import com.example.socket.config.exception.CausedBy;
import com.example.socket.config.exception.GlobalErrorException;

public class MessageBrokerErrorException extends GlobalErrorException {
    private final MessageBrokerErrorCode errorCode;

    public MessageBrokerErrorException(MessageBrokerErrorCode messageBrokerErrorCode) {
        super(messageBrokerErrorCode);
        this.errorCode = messageBrokerErrorCode;
    }

    public CausedBy causedBy() {
        return errorCode.causedBy();
    }

    public MessageBrokerErrorCode getErrorCode() {
        return errorCode;
    }
}
