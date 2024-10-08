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

    public boolean hasPermission(Long chatRoomId, Long userId) {
        return chatRoomService.isExists(chatRoomId, userId);
    }

    /**
     * path에서 chatRoomId를 추출한다.
     *
     * @param path : {@code /sub/chat.room.{roomId} 포맷}
     * @return chatRoomId
     */
    private Long getChatRoomId(String path) {
        String[] split = path.split("\\.");
        return Long.parseLong(split[split.length - 1]);
    }

    private boolean isChatRoomAccess(Long chatRoomId, Principal principal) {
        return chatRoomService.isExists(chatRoomId, 1L); // Long.parseLong(principal.getName())가 안 되므로, 일단 1L 고정
    }
}
