package com.example.socket.domains.chat.repository;

import com.example.socket.domains.chat.domain.ChatCache;
import org.springframework.data.repository.CrudRepository;

public interface ChatCacheRepository extends CrudRepository<ChatCache, String> {
}
