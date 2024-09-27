package com.example.socket.chats.common.event;

import com.example.socket.chats.dto.ServerSideMessage;
import org.springframework.context.ApplicationEvent;
import org.springframework.messaging.Message;

import java.util.Objects;

public class RefreshEvent<T extends ServerSideMessage> extends ApplicationEvent {
    private final Message<T> message;

    public RefreshEvent(Message<T> message) {
        super(message);
        this.message = message;
    }

    public static <T extends ServerSideMessage> RefreshEvent<T> of(Message<T> message) {
        return new RefreshEvent<>(message);
    }

    public Message<T> getMessage() {
        return message;
    }
}
