package com.example.socket.chats.common.handler.exception;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;

/**
 * STOMP 인터셉터에서 발생한 예외를 처리하기 위한 인터페이스
 */
public interface StompExceptionHandler {
    /**
     * 해당 예외를 처리할 수 있는지 여부를 반환하는 메서드
     * @return true: 해당 예외를 처리할 수 있음, false: 해당 예외를 처리할 수 없음
     */
    boolean canHandle(Throwable cause);

    /**
     * 예외를 처리하는 메서드.
     * WebSocket 프로토콜에 의해 ERROR 커맨드를 사용하면, client와의 연결을 반드시 끊어야 한다.
     * 이를 원치 않는 경우, {@link StompCommand#ERROR}를 사용하여 Accessor를 설정해서는 안 된다.
     *
     * @param clientMessage {@link Message}: client로부터 받은 메시지
     * @param cause Throwable: 발생한 예외
     * @return {@link Message}: client에게 보낼 최종 메시지
     */
    @Nullable
    Message<byte[]> handle(@Nullable Message<byte[]> clientMessage, Throwable cause);

    /**
     * client로부터 받은 메시지의 HeaderAccessor에서 필요한 정보를 추출하는 편의용 메서드
     * 기본으로는 receiptId만을 추출하도록 구현되어 있으며, 필요한 정보가 있다면 해당 메서드를 구현하여 사용한다.
     *
     * @param clientMessage {@link Message}: client로부터 받은 메시지
     * @param errorHeaderAccessor {@link StompHeaderAccessor}: client에게 보낼 메시지를 생성하기 위한 errorHeaderAccessor
     */
    default void extractClientHeaderAccessor(@NonNull Message<byte[]> clientMessage, @NonNull StompHeaderAccessor errorHeaderAccessor) {
        StompHeaderAccessor clientHeaderAccessor = MessageHeaderAccessor.getAccessor(clientMessage, StompHeaderAccessor.class);

        if (clientHeaderAccessor != null) {
            String receiptId = clientHeaderAccessor.getReceipt();
            if (receiptId != null) {
                errorHeaderAccessor.setReceiptId(receiptId);
            }
        }
    }
}
