package com.example.socket.domains.chatroom.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    public boolean isExists(Long chatRoomId, Long userId) {
        return true; // 일단 무조건 허용.
    }
}
