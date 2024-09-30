package com.example.socket.chats.common.security.authorize;

import com.example.socket.domains.chatroom.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Slf4j
@Component("chatRoomAccessChecker")
@RequiredArgsConstructor
public class ChatRoomAccessChecker implements ResourceAccessChecker {
    private final ChatRoomService chatRoomService;

    @Override
    public boolean hasPermission(String path, Principal principal) {
        return isChatRoomAccess(getChatRoomId(path), principal);
    }

    /**
     * path에서 chatRoomId를 추출한다.
     *
     * @param path : {@code /sub/chat.exchange/chat.room.{roomId} 포맷}
     * @return chatRoomId
     */
    private Long getChatRoomId(String path) {
        String[] split = path.split("\\.");
        return Long.parseLong(split[split.length - 1]);
    }

    private boolean isChatRoomAccess(Long chatRoomId, Principal principal) {
        return chatRoomService.isExists(chatRoomId, Long.parseLong(principal.getName()));
    }
}
