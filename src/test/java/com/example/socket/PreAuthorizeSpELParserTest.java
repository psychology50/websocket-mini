package com.example.socket;

import com.example.socket.chats.common.security.principal.UserPrincipal;
import com.example.socket.chats.common.util.PreAuthorizeSpELParser;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.GenericApplicationContext;

import java.lang.reflect.Method;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PreAuthorizeSpELParserTest {
    @Test
    void testThreadSafety() throws Exception {
        int threadCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        Method testMethod = TestController.class.getDeclaredMethod("testMethod", String.class);
        GenericApplicationContext applicationContext = new GenericApplicationContext();

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    Principal principal = index % 2 == 0 ? null : UserFixture.AUTHENTICATED.getPrincipal();
                    String expression = index % 2 == 0 ? "#isAnonymous(#principal)" : "#isAuthenticated(#principal)";
                    Object[] args = new Object[]{"principal"};

                    boolean result = PreAuthorizeSpELParser.evaluate(expression, testMethod, args, applicationContext);

                    if ((index % 2 == 0 && result) || (index % 2 != 0 && result)) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        assertEquals(threadCount, successCount.get(), "모든 평가가 완료되어야 합니다.");
    }

    private static class TestController {
        public void testMethod(String arg) {
            System.out.println("testMethod: " + arg);
        }
    }

    private enum UserFixture {
        AUTHENTICATED;

        public Principal getPrincipal() {
            return UserPrincipal.builder()
                    .userId(1L)
                    .name("jayang")
                    .expiresAt(LocalDateTime.now().plusMinutes(30))
                    .build();
        }
    }
}
