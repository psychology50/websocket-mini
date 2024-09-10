package com.example.socket.infra.common.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * FCM 푸시 알림을 처리하는 핸들러
 */
@Slf4j
@RequiredArgsConstructor
public class FcmNotificationEventHandler implements NotificationEventHandler {
    @Async
    @Override
    @TransactionalEventListener
    public void handleEvent(NotificationEvent event) {
        log.debug("handleEvent: {}", event);
    }
}
