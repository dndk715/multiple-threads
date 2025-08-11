package com.example.multiplethreads.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "logging.level.com.example.multiplethreads=DEBUG"
})
class ThreadCompletionServiceTest {

    @Autowired
    private ThreadCompletionService threadCompletionService;

    @Test
    void testProcessMultipleTasksAndWait() {
        // 테스트 실행
        assertDoesNotThrow(() -> {
            threadCompletionService.processMultipleTasksAndWait();
        });
    }

    @Test
    void testProcessWithCountDownLatch() {
        // 테스트 실행
        assertDoesNotThrow(() -> {
            threadCompletionService.processWithCountDownLatch();
        });
    }

    @Test
    void testShutdown() {
        // 테스트 실행
        assertDoesNotThrow(() -> {
            threadCompletionService.shutdown();
        });
    }
}
