package com.example.socket.chats.common.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompCommandHandlerFactory {
    private Map<StompCommand, List<StompCommandHandler>> handlers = new EnumMap<>(StompCommand.class);

    @Autowired
    public StompCommandHandlerFactory(List<StompCommandHandler> allHandlers) {
        for (StompCommandHandler handler: allHandlers) {
            for (StompCommand command: StompCommand.values()) {
                log.info("StompCommandHandlerFactory: command={}", command);
                if (handler.supports(command)) {
                    handlers.computeIfAbsent(command, key -> new ArrayList<>()).add(handler);
                }
            }
        }

        log.info("StompCommandHandlerFactory: handlers={}", handlers);
    }

    public List<StompCommandHandler> getHandlers(StompCommand command) {
        return handlers.getOrDefault(command, List.of());
    }
}
