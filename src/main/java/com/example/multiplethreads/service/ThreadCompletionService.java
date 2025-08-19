package com.example.multiplethreads.service;

import com.example.multiplethreads.dto.FileTaskResult;
import com.example.multiplethreads.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ThreadCompletionService {

    private final FileGenerationService fileGenerationService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(
        Runtime.getRuntime().availableProcessors()
    );

    /**
     * 각 task를 개별 서비스의 함수로 실행하고 압축하여 반환
     */
    public byte[] createFilesWithServiceAndCompress() throws IOException {
        log.info("각 task를 개별 서비스의 함수로 실행하고 압축합니다...");
        
        List<Path> filePaths = new ArrayList<>();
        
        try {
            // 모든 task 실행
            FileTaskResult[] results = executeAllTasks();
            
            // 모든 task가 성공했으므로 모든 파일을 압축
            for (FileTaskResult result : results) {
                filePaths.add(result.getFilePath());
                log.info("파일 생성 성공: {} (크기: {} bytes)", result.getFileName(), result.getFileSize());
            }
            
            log.info("총 {} 개의 파일을 압축합니다.", filePaths.size());
            
            // 파일들을 ZIP으로 압축
            byte[] zipData = FileUtil.createZipArchive(filePaths);
            log.info("압축 완료: {} bytes", zipData.length);
            
            return zipData;
            
        } finally {
            // 임시 파일들 정리
            if (!filePaths.isEmpty()) {
                FileUtil.cleanupTempFiles(filePaths);
                log.info("임시 파일 {} 개를 정리했습니다.", filePaths.size());
            }
        }
    }

    /**
     * 각 task를 개별 서비스의 함수로 실행
     */
    private FileTaskResult[] executeAllTasks() throws IOException {
        log.info("각 task를 개별 서비스의 함수로 실행합니다...");
        
        // 각 task를 개별 서비스의 함수로 실행
        CompletableFuture<FileTaskResult> task1 = CompletableFuture.supplyAsync(() -> {
            try {
                return fileGenerationService.createReportFile(1);
            } catch (IOException e) {
                log.error("Task 1 실행 중 오류 발생", e);
                return new FileTaskResult(1, "파일 생성 오류: " + e.getMessage());
            }
        }, executorService);

        CompletableFuture<FileTaskResult> task2 = CompletableFuture.supplyAsync(() -> {
            try {
                return fileGenerationService.createCsvFile(2);
            } catch (IOException e) {
                log.error("Task 2 실행 중 오류 발생", e);
                return new FileTaskResult(2, "파일 생성 오류: " + e.getMessage());
            }
        }, executorService);

        CompletableFuture<FileTaskResult> task3 = CompletableFuture.supplyAsync(() -> {
            try {
                return fileGenerationService.createJsonFile(3);
            } catch (IOException e) {
                log.error("Task 3 실행 중 오류 발생", e);
                return new FileTaskResult(3, "파일 생성 오류: " + e.getMessage());
            }
        }, executorService);

        CompletableFuture<FileTaskResult> task4 = CompletableFuture.supplyAsync(() -> {
            try {
                return fileGenerationService.createLogFile(4);
            } catch (IOException e) {
                log.error("Task 4 실행 중 오류 발생", e);
                return new FileTaskResult(4, "파일 생성 오류: " + e.getMessage());
            }
        }, executorService);

        CompletableFuture<FileTaskResult> task5 = CompletableFuture.supplyAsync(() -> {
            try {
                return fileGenerationService.createMarkdownFile(5);
            } catch (IOException e) {
                log.error("Task 5 실행 중 오류 발생", e);
                return new FileTaskResult(5, "파일 생성 오류: " + e.getMessage());
            }
        }, executorService);

        // 모든 task 완료 대기
        CompletableFuture<Void> allTasks = CompletableFuture.allOf(task1, task2, task3, task4, task5);
        
        try {
            // 모든 task가 완료될 때까지 기다림
            allTasks.get(30, TimeUnit.SECONDS);
            
            // 결과 수집
            FileTaskResult[] results = new FileTaskResult[5];
            results[0] = task1.get();
            results[1] = task2.get();
            results[2] = task3.get();
            results[3] = task4.get();
            results[4] = task5.get();
            
            // 실패한 task가 있는지 확인
            List<String> failedTasks = new ArrayList<>();
            for (FileTaskResult result : results) {
                if (!result.isSuccess()) {
                    failedTasks.add("작업 " + result.getTaskId() + ": " + result.getErrorMessage());
                }
            }
            
            // 실패한 task가 있으면 전체 작업 실패
            if (!failedTasks.isEmpty()) {
                String errorMessage = "다음 작업들이 실패했습니다: " + String.join(", ", failedTasks);
                log.error(errorMessage);
                throw new IOException(errorMessage);
            }
            
            log.info("모든 task 실행 완료");
            return results;
            
        } catch (Exception e) {
            log.error("Task 실행 중 오류 발생", e);
            throw new IOException("Task 실행 실패: " + e.getMessage(), e);
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

    // ===== 테스트용 실패 시나리오 메서드 =====

    /**
     * 일부 작업이 실패하는 시나리오 테스트 (테스트용)
     */
    public byte[] executeAllTasksWithFailure() throws IOException {
        log.info("일부 작업이 실패하는 시나리오를 테스트합니다...");
        
        List<Path> filePaths = new ArrayList<>();
        
        try {
            // 일부 작업은 성공, 일부는 실패하도록 설정
            CompletableFuture<FileTaskResult> task1 = CompletableFuture.supplyAsync(() -> {
                try {
                    return fileGenerationService.createReportFile(1); // 성공
                } catch (IOException e) {
                    log.error("Task 1 실행 중 오류 발생", e);
                    throw new CompletionException(e); // 예외를 그대로 전파
                }
            }, executorService);

            CompletableFuture<FileTaskResult> task2 = CompletableFuture.supplyAsync(() -> {
                try {
                    return fileGenerationService.createFailingCsvFile(2); // 의도적 실패
                } catch (IOException e) {
                    log.error("Task 2 실행 중 오류 발생", e);
                    throw new CompletionException(e); // 예외를 그대로 전파
                }
            }, executorService);

            CompletableFuture<FileTaskResult> task3 = CompletableFuture.supplyAsync(() -> {
                try {
                    return fileGenerationService.createJsonFile(3); // 성공
                } catch (IOException e) {
                    log.error("Task 3 실행 중 오류 발생", e);
                    throw new CompletionException(e); // 예외를 그대로 전파
                }
            }, executorService);

            CompletableFuture<FileTaskResult> task4 = CompletableFuture.supplyAsync(() -> {
                try {
                    return fileGenerationService.createLogFile(4); // 성공
                } catch (IOException e) {
                    log.error("Task 4 실행 중 오류 발생", e);
                    throw new CompletionException(e); // 예외를 그대로 전파
                }
            }, executorService);

            CompletableFuture<FileTaskResult> task5 = CompletableFuture.supplyAsync(() -> {
                try {
                    return fileGenerationService.createMarkdownFile(5); // 성공
                } catch (IOException e) {
                    log.error("Task 5 실행 중 오류 발생", e);
                    throw new CompletionException(e); // 예외를 그대로 전파
                }
            }, executorService);

            // 모든 task 완료 대기
            CompletableFuture<Void> allTasks = CompletableFuture.allOf(task1, task2, task3, task4, task5);
            
            try {
                // 모든 task가 완료될 때까지 기다림
                allTasks.get(30, TimeUnit.SECONDS);
                
                // 결과 수집
                FileTaskResult[] results = new FileTaskResult[5];
                results[0] = task1.get();
                results[1] = task2.get();
                results[2] = task3.get();
                results[3] = task4.get();
                results[4] = task5.get();
                
                log.info("모든 task 실행 완료");
                
                // 모든 task가 성공했으므로 모든 파일을 압축
                for (FileTaskResult result : results) {
                    filePaths.add(result.getFilePath());
                    log.info("파일 생성 성공: {} (크기: {} bytes)", 
                            result.getFileName(), result.getFileSize());
                }
                byte[] zipData = FileUtil.createZipArchive(filePaths);
                log.info("압축 완료: {} bytes", zipData.length);
                
                return zipData;
                
            } catch (Exception e) {
                log.error("Task 실행 중 오류 발생", e);
                throw new IOException("Task 실행 실패: " + e.getMessage(), e);
            }
            
        } finally {
            // 임시 파일들 정리
            if (!filePaths.isEmpty()) {
                FileUtil.cleanupTempFiles(filePaths);
                log.info("임시 파일 {} 개를 정리했습니다.", filePaths.size());
            }
        }
    }
}
