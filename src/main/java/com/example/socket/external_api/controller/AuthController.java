package com.example.socket.external_api.controller;

import com.example.socket.domains.user.domain.User;
import com.example.socket.domains.user.service.UserService;
import com.example.socket.domains.user.type.Role;
import com.example.socket.external_api.common.security.jwt.access.AccessTokenClaim;
import com.example.socket.external_api.common.security.jwt.access.AccessTokenProvider;
import com.example.socket.external_api.common.security.jwt.refresh.RefreshTokenClaim;
import com.example.socket.external_api.common.security.jwt.refresh.RefreshTokenProvider;
import com.example.socket.infra.common.jwt.JwtClaims;
import com.example.socket.infra.common.jwt.JwtClaimsParserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/auth")
@CrossOrigin(originPatterns = "*", methods = {RequestMethod.GET, RequestMethod.POST}, allowedHeaders = "*", allowCredentials = "true")
public class AuthController {
    private final UserService userService;
    private final AccessTokenProvider accessTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;

    @GetMapping("/login/{userId}")
    public ResponseEntity<?> login(@RequestParam("userId") Long userId) {
        User user = userService.readById(userId);

        String accessToken = accessTokenProvider.generateToken(AccessTokenClaim.of(user.getId(), user.getRole().getType()));
        String refreshToken = refreshTokenProvider.generateToken(RefreshTokenClaim.of(user.getId(), user.getRole().getType()));

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofDays(7).toSeconds())
                .sameSite("None")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.COOKIE, cookie.toString())
                .body(Map.of("userId", userId));
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader("Authorization") String accessToken,
            @CookieValue("refreshToken") String refreshToken
    ) {
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "")
                .header(HttpHeaders.COOKIE, "refreshToken=; Max-Age=0")
                .body(Map.of("message", "로그아웃 되었습니다."));
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refresh(
            @CookieValue("refreshToken") String refreshToken
    ) {
        JwtClaims claims = refreshTokenProvider.getJwtClaimsFromToken(refreshToken);

        Long userId = JwtClaimsParserUtil.getClaimsValue(claims, "userId", Long::parseLong);
        String role = JwtClaimsParserUtil.getClaimsValue(claims, "role", String.class);

        String newAccessToken = accessTokenProvider.generateToken(AccessTokenClaim.of(userId, role));
        String newRefreshToken = refreshTokenProvider.generateToken(RefreshTokenClaim.of(userId, role));

        ResponseCookie cookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofDays(7).toSeconds())
                .sameSite("None")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, newAccessToken)
                .header(HttpHeaders.COOKIE, cookie.toString())
                .body(Map.of("userId", userId));
    }
}
