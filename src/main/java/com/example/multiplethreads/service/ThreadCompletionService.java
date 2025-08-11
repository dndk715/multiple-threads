package com.example.multiplethreads.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ThreadCompletionService {

    private final ExecutorService executorService;

    public ThreadCompletionService() {
        // 스레드 풀 생성 (CPU 코어 수만큼)
        this.executorService = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors()
        );
    }

    /**
     * 여러 작업을 병렬로 실행하고 모든 작업이 완료된 후 결과를 처리
     */
    public void processMultipleTasksAndWait() {
        log.info("여러 작업을 병렬로 실행하고 완료를 기다립니다...");
        
        List<CompletableFuture<String>> futures = new ArrayList<>();
        
        // 여러 작업을 병렬로 실행
        for (int i = 1; i <= 5; i++) {
            final int taskId = i;
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                return executeTask(taskId);
            }, executorService);
            
            futures.add(future);
        }
        
        // 모든 작업이 완료될 때까지 기다림
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0])
        );
        
        // 모든 작업 완료 후 결과 처리
        allFutures.thenRun(() -> {
            log.info("모든 작업이 완료되었습니다! 결과를 처리합니다...");
            
            // 각 작업의 결과를 수집
            List<String> results = new ArrayList<>();
            for (CompletableFuture<String> future : futures) {
                try {
                    results.add(future.get());
                } catch (Exception e) {
                    log.error("작업 결과를 가져오는 중 오류 발생", e);
                }
            }
            
            // 모든 결과를 처리하는 로직
            processAllResults(results);
        }).join(); // 메인 스레드에서 완료를 기다림
        
        log.info("모든 작업과 후처리가 완료되었습니다.");
    }

    /**
     * 개별 작업 실행
     */
    private String executeTask(int taskId) {
        log.info("작업 {} 시작", taskId);
        
        try {
            // 작업 시뮬레이션 (1-3초 랜덤 지연)
            int delaySeconds = (int) (Math.random() * 3) + 1;
            Thread.sleep(delaySeconds * 1000L);
            
            String result = "작업 " + taskId + " 완료 (소요시간: " + delaySeconds + "초)";
            log.info(result);
            return result;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("작업 {} 중단됨", taskId, e);
            return "작업 " + taskId + " 중단됨";
        }
    }

    /**
     * 모든 결과를 처리하는 로직
     */
    private void processAllResults(List<String> results) {
        log.info("=== 모든 작업 결과 처리 시작 ===");
        log.info("총 {} 개의 작업이 완료되었습니다.", results.size());
        
        for (String result : results) {
            log.info("결과: {}", result);
        }
        
        // 여기에 실제 비즈니스 로직을 추가할 수 있습니다
        // 예: 데이터베이스 저장, 외부 API 호출, 알림 발송 등
        
        log.info("=== 모든 작업 결과 처리 완료 ===");
    }

    /**
     * CountDownLatch를 사용한 방법 (대안)
     */
    public void processWithCountDownLatch() {
        log.info("CountDownLatch를 사용하여 여러 작업을 처리합니다...");
        
        int taskCount = 5;
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(taskCount);
        List<String> results = new java.util.concurrent.CopyOnWriteArrayList<>();
        
        for (int i = 1; i <= taskCount; i++) {
            final int taskId = i;
            executorService.submit(() -> {
                try {
                    String result = executeTask(taskId);
                    results.add(result);
                } finally {
                    latch.countDown();
                }
            });
        }
        
        try {
            // 모든 작업이 완료될 때까지 기다림
            latch.await(30, TimeUnit.SECONDS);
            log.info("CountDownLatch: 모든 작업 완료!");
            processAllResults(results);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("CountDownLatch 대기 중 중단됨", e);
        }
    }

    /**
     * 애플리케이션 종료 시 리소스 정리
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
