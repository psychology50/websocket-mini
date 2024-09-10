package com.example.socket.domains.user.repository;

import com.example.socket.domains.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
