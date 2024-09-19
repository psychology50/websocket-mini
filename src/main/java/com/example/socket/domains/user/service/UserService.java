package com.example.socket.domains.user.service;

import com.example.socket.domains.user.domain.User;
import com.example.socket.domains.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Cacheable(value = "users", key = "#id", unless = "#result == null")
    public User readById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));
    }

    // 캐시 수정할 때는 CachePut을 사용한다.
    @CachePut(value = "users", key = "#user.id", unless = "#result == null")
    public User updateUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));
        user.updateUsername(username);
        return userRepository.save(user);
    }

    @CacheEvict(value = "users", key = "#id", condition = "#result == true")
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
