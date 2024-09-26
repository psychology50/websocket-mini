package com.example.socket.chats.common.annotation;

import com.example.socket.chats.common.aop.PreAuthorizeAspect;
import com.example.socket.chats.common.util.PreAuthorizeSpELParser;

import java.lang.annotation.*;

/**
 * WebSocket Controller에 대한 인증 및 인가를 지정하는 어노테이션.
 * 이 어노테이션이 붙은 메서드는 {@link PreAuthorizeAspect}에 의해 처리됩니다.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PreAuthorize {
    /**
     * 인증/인가를 위한 SpEL 표현식.
     * 이 표현식은 {@link PreAuthorizeSpELParser}에 의해 평가됩니다.
     *
     * @return 평가할 SpEL 표현식
     */
    String value();
}
