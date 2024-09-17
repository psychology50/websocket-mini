package com.example.socket.external_api.common.security.jwt.refresh;

import com.example.socket.infra.common.jwt.JwtClaims;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static com.example.socket.external_api.common.security.jwt.refresh.RefreshTokenClaimKeys.ROLE;
import static com.example.socket.external_api.common.security.jwt.refresh.RefreshTokenClaimKeys.USER_ID;


@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RefreshTokenClaim implements JwtClaims {
    private final Map<String, ?> claims;

    public static RefreshTokenClaim of(Long userId, String role) {
        Map<String, Object> claims = Map.of(
                USER_ID.getValue(), userId.toString(),
                ROLE.getValue(), role
        );
        return new RefreshTokenClaim(claims);
    }

    @Override
    public Map<String, ?> getClaims() {
        return claims;
    }
}
