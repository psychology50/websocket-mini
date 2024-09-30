package com.example.socket.chats.common.interceptor.handler;

import com.example.socket.chats.common.exception.InterceptorErrorCode;
import com.example.socket.chats.common.exception.InterceptorErrorException;
import com.example.socket.chats.common.interceptor.handler.marker.SubscribeCommandHandler;
import com.example.socket.chats.common.security.authorize.ResourceAccessRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatExchangeAuthorizeHandler implements SubscribeCommandHandler {
    private static final String REQUEST_EXCHANGE_PREFIX = "/sub/";
    private static final String CONVERTED_EXCHANGE_PREFIX = "/exchange/chat.exchange/";
    private static final String PRIVATE_EXCHANGE_PREFIX = "/user/";

    private final ResourceAccessRegistry resourceAccessRegistry;

    @Override
    public boolean isSupport(StompCommand command) {
        return StompCommand.SUBSCRIBE.equals(command);
    }

    @Override
    public void handle(Message<?> message, StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();

        if (destination != null && destination.startsWith(PRIVATE_EXCHANGE_PREFIX)) { // "/user/"로 시작하는 경우는 bypass
            log.info("[Exchange 권한 검사] User {}에 대한 {} 권한 검사 통과", accessor.getUser().getName(), destination);
            return;
        }

        // 자원 검사 (구독할 수 없는 데이터라면? connection을 해제할 것인지? 아니면, 구독만 안 되게 할 것인지?)
        // 자원 검사를 위한 path에서 필요한 정보는 어떻게 추출할 것인지?
        // 예를 들어, /sub/chat.exchange/chat.room.1 이라면, client가 1번 채팅방 접근 가능 여부를 판단
        // 잘못 설계하면 추후 다른 path가 추가될 때마다 interceptor를 추가해야 하는데, 범용적으로 해결할 방법이 없을까???
        if (resourceAccessRegistry.getChecker(destination).hasPermission(destination, accessor.getUser())) {
            log.info("[Exchange 권한 검사] User {}에 대한 {} 권한 검사 통과", accessor.getUser().getName(), destination);
            String convertedDestination = convertDestination(destination);
            accessor.setDestination(convertedDestination);
        } else { // 권한이 없으면 connection은 유지하고, client에게 에러 메시지를 전달
            log.info("[Exchange 권한 검사] User {}에 대한 {} 권한 검사 실패", accessor.getUser().getName(), destination);
            throw new InterceptorErrorException(InterceptorErrorCode.UNAUTHORIZED_TO_SUBSCRIBE);
        }
    }

    private String convertDestination(String destination) {
        if (destination == null || !destination.startsWith(REQUEST_EXCHANGE_PREFIX)) {
            throw new InterceptorErrorException(InterceptorErrorCode.INVALID_DESTINATION); // 이것도 연결 끊지 말고, 구독 실패 에러 전달만
        }

        String convertedDestination = destination.replace(REQUEST_EXCHANGE_PREFIX, CONVERTED_EXCHANGE_PREFIX);
        log.info("[Exchange 변환 핸들러] destination={}, convertedDestination={}", destination, convertedDestination);

        return convertedDestination;
    }
}
