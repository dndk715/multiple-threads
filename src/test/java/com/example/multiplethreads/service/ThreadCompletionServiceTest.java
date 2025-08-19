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
    void testCreateFilesWithServiceAndCompress() {
        // 테스트 실행
        assertDoesNotThrow(() -> {
            threadCompletionService.createFilesWithServiceAndCompress();
        });
    }

    @Test
    void testExecuteAllTasksWithFailure() {
        // 실패 시나리오 테스트 - IOException이 발생해야 함
        assertThrows(Exception.class, () -> {
            threadCompletionService.executeAllTasksWithFailure();
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
