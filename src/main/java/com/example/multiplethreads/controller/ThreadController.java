package com.example.multiplethreads.controller;

import com.example.multiplethreads.service.ThreadCompletionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/threads")
@RequiredArgsConstructor
public class ThreadController {

    private final ThreadCompletionService threadCompletionService;

    /**
     * 각 task를 개별 서비스의 함수로 실행하고 압축하여 다운로드 (새로운 방식)
     */
    @GetMapping("/create-files-with-service-and-download")
    public ResponseEntity<byte[]> createFilesWithServiceAndDownload() {
        log.info("각 task를 개별 서비스의 함수로 실행하고 압축하여 다운로드합니다 (새로운 방식).");
        
        long startTime = System.currentTimeMillis();
        
        try {
            // 새로운 서비스 기반 파일 생성 및 압축
            byte[] zipData = threadCompletionService.createFilesWithServiceAndCompress();
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // 파일명에 타임스탬프 추가
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "service_generated_files_" + timestamp + ".zip";
            
            log.info("서비스 기반 파일 생성 및 압축 완료: {} (소요시간: {}ms)", filename, duration);
            
            // 파일 다운로드 응답 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(zipData.length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(zipData);
            
        } catch (IOException e) {
            log.error("서비스 기반 파일 생성 및 압축 중 오류 발생", e);
            
            // 오류 발생 시 JSON 응답으로 변경
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "서비스 기반 파일 생성 및 압축 중 오류가 발생했습니다: " + e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            // JSON 응답을 위한 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // JSON 문자열을 바이트 배열로 변환
            String jsonResponse = "{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}";
            byte[] jsonBytes = jsonResponse.getBytes();
            
            return ResponseEntity.internalServerError()
                    .headers(headers)
                    .body(jsonBytes);
        }
    }

    /**
     * 실패 시나리오 테스트: 일부 작업이 실패하는 경우 (테스트용)
     */
    @GetMapping("/create-files-with-failure-and-download")
    public ResponseEntity<byte[]> createFilesWithFailureAndDownload() {
        log.info("실패 시나리오 테스트: 일부 작업이 실패하는 경우를 테스트합니다.");
        
        long startTime = System.currentTimeMillis();
        
        try {
            // 실패 시나리오 테스트
            byte[] zipData = threadCompletionService.executeAllTasksWithFailure();
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // 파일명에 타임스탬프 추가
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "failure_test_files_" + timestamp + ".zip";
            
            log.info("실패 시나리오 테스트 완료: {} (소요시간: {}ms)", filename, duration);
            
            // 파일 다운로드 응답 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(zipData.length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(zipData);
            
        } catch (IOException e) {
            log.error("실패 시나리오 테스트 중 오류 발생", e);
            
            // 오류 발생 시 JSON 응답으로 변경
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "실패 시나리오 테스트 중 오류가 발생했습니다: " + e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            // JSON 응답을 위한 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // JSON 문자열을 바이트 배열로 변환
            String jsonResponse = "{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}";
            byte[] jsonBytes = jsonResponse.getBytes();
            
            return ResponseEntity.internalServerError()
                    .headers(headers)
                    .body(jsonBytes);
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
