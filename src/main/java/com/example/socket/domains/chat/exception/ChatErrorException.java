package com.example.socket.domains.chat.exception;

import com.example.socket.config.exception.BaseErrorCode;
import com.example.socket.config.exception.CausedBy;
import com.example.socket.config.exception.GlobalErrorException;

public class ChatErrorException extends GlobalErrorException {
    private final ChatErrorCode errorCode;

    public ChatErrorException(ChatErrorCode chatErrorCode) {
        super(chatErrorCode);
        this.errorCode = chatErrorCode;
    }

    public CausedBy causedBy() {
        return errorCode.causedBy();
    }

    public ChatErrorCode getErrorCode() {
        return errorCode;
    }
}
