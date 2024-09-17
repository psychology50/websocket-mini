package com.example.socket.infra.common.jwt;

import java.util.Map;

public interface JwtClaims {
    Map<String, ?> getClaims();
}
