package com.example.multiplethreads.controller;

import com.example.multiplethreads.service.ThreadCompletionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/threads")
@RequiredArgsConstructor
public class ThreadController {

    private final ThreadCompletionService threadCompletionService;

    /**
     * CompletableFuture를 사용한 여러 스레드 작업 실행
     */
    @GetMapping("/completable-future")
    public ResponseEntity<Map<String, Object>> runCompletableFutureTasks() {
        log.info("CompletableFuture를 사용한 여러 스레드 작업을 시작합니다.");
        
        long startTime = System.currentTimeMillis();
        
        try {
            threadCompletionService.processMultipleTasksAndWait();
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "모든 작업이 완료되었습니다.");
            response.put("totalDuration", duration + "ms");
            response.put("method", "CompletableFuture");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("작업 실행 중 오류 발생", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "작업 실행 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * CountDownLatch를 사용한 여러 스레드 작업 실행
     */
    @GetMapping("/countdown-latch")
    public ResponseEntity<Map<String, Object>> runCountDownLatchTasks() {
        log.info("CountDownLatch를 사용한 여러 스레드 작업을 시작합니다.");
        
        long startTime = System.currentTimeMillis();
        
        try {
            threadCompletionService.processWithCountDownLatch();
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "모든 작업이 완료되었습니다.");
            response.put("totalDuration", duration + "ms");
            response.put("method", "CountDownLatch");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("작업 실행 중 오류 발생", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "작업 실행 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 시스템 정보 조회
     */
    @GetMapping("/system-info")
    public ResponseEntity<Map<String, Object>> getSystemInfo() {
        Map<String, Object> systemInfo = new HashMap<>();
        systemInfo.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        systemInfo.put("totalMemory", Runtime.getRuntime().totalMemory());
        systemInfo.put("freeMemory", Runtime.getRuntime().freeMemory());
        systemInfo.put("maxMemory", Runtime.getRuntime().maxMemory());
        systemInfo.put("javaVersion", System.getProperty("java.version"));
        systemInfo.put("osName", System.getProperty("os.name"));
        
        return ResponseEntity.ok(systemInfo);
    }

    /**
     * 헬스체크
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", String.valueOf(System.currentTimeMillis()));
        
        return ResponseEntity.ok(health);
    }
}
