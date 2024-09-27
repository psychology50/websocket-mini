package com.example.socket.chats.common.aop;

import com.example.socket.chats.common.annotation.PreAuthorize;
import com.example.socket.chats.common.util.PreAuthorizeSpELParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.security.Principal;
import java.util.stream.Stream;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PreAuthorizeAspect {
    private final ApplicationContext applicationContext;

    /**
     * {@link PreAuthorize} 어노테이션이 붙은 메서드를 가로채고 인증/인가를 수행합니다.
     *
     * @param joinPoint 가로챈 메서드의 실행 지점
     * @return 인증/인가가 성공하면 원래 메서드의 실행 결과, 실패하면 UnauthorizedResponse
     * @throws Throwable 메서드 실행 중 발생한 예외
     */
    @Around("@annotation(com.example.socket.chats.common.annotation.PreAuthorize)")
    public Object execute(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        PreAuthorize preAuthorize = method.getAnnotation(PreAuthorize.class);

        Principal principal = extractPrincipal(joinPoint.getArgs());
        log.info("principal: {}", principal);

        boolean isAuthorized = PreAuthorizeSpELParser.evaluate(preAuthorize.value(), method, joinPoint.getArgs(), applicationContext);
        log.info("isAuthorized: {}", isAuthorized);

        if (!isAuthorized) {
            handleUnauthorized(principal, preAuthorize);
        }

        return joinPoint.proceed();
    }

    /**
     * 메서드 인자에서 Principal 객체를 추출합니다.
     *
     * @param args 메서드 인자 배열
     * @return 찾은 Principal 객체, 없으면 null
     */
    private Principal extractPrincipal(Object[] args) {
        return Stream.of(args)
                .filter(arg -> arg instanceof Principal)
                .map(arg -> (Principal) arg)
                .findFirst()
                .orElse(null);
    }

    /**
     * 인증/인가 실패 시 처리합니다.
     *
     * @param principal
     * @param preAuthorize
     */
    private void handleUnauthorized(Principal principal, PreAuthorize preAuthorize) {
        // 사용자가 isAuthenticate()를 했다면 인증 실패, isAnonymouse()를 했다면 익명 실패 예외를 반환해야함.
        if (preAuthorize.value().contains(PreAuthorizeSpELParser.SpELFunction.IS_AUTHENTICATED.getName())) {
            log.warn("인증 실패: {}", principal);
//                throw new PreAuthorizeErrorException(PreAuthorizeErrorCode.UNAUTHENTICATED);
        } else if (preAuthorize.value().contains(PreAuthorizeSpELParser.SpELFunction.IS_ANONYMOUS.getName())) {
            log.warn("익명 실패: {}", principal);
//                throw new PreAuthorizeErrorException(PreAuthorizeErrorCode.UNANNOYMOUS);
        }
    }
}