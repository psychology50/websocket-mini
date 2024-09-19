package com.example.socket.chats.common.security.principle;

import com.example.socket.domains.user.domain.User;
import com.example.socket.domains.user.type.Role;
import lombok.Builder;
import lombok.Getter;

import java.security.Principal;

@Getter
public class UserPrincipal implements Principal {
    private final Long userId;
    private final String name;
    private final String username;
    private final String profileImageUrl;
    private final Role role;
    private final boolean isChatNotify;

    @Builder
    private UserPrincipal(Long userId, String name, String username, String profileImageUrl, Role role, boolean isChatNotify) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
        this.isChatNotify = isChatNotify;
    }

    public static UserPrincipal from(User user) {
        return UserPrincipal.builder()
                .userId(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .profileImageUrl(user.getProfileImageUrl())
                .role(user.getRole())
                .isChatNotify(user.getNotifySetting().isChatNotify())
                .build();
    }

    @Override
    public int hashCode() {
        int result = userId.hashCode() * 31;
        return result + username.hashCode() * 31;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        UserPrincipal that = (UserPrincipal) obj;
        return userId.equals(that.userId);
    }

    @Override
    public String toString() {
        return "UserPrincipal{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                ", role=" + role +
                ", isChatNotify=" + isChatNotify +
                '}';
    }
}