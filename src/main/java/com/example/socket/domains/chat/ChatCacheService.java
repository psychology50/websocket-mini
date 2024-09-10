package com.example.socket.domains.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatCacheService {
    private final ChatCacheRepository chatCacheRepository;

    public ChatCache save(ChatCache chatCache) {
        return chatCacheRepository.save(chatCache);
    }
}
