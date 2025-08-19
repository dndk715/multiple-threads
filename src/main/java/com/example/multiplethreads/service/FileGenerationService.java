package com.example.multiplethreads.service;

import com.example.multiplethreads.dto.FileTaskResult;
import com.example.multiplethreads.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
public class FileGenerationService {

    /**
     * 텍스트 보고서 파일 생성
     */
    public FileTaskResult createReportFile(int taskId) throws IOException {
        log.info("텍스트 보고서 파일 생성 시작 (작업 {})", taskId);
        
        String fileName = "report.txt";
        String content = "작업 " + taskId + "의 보고서 내용입니다.\n생성 시간: " + System.currentTimeMillis();
        
        Path tempFile = FileUtil.createTempFile("task_" + taskId + "_", ".txt", content);
        long fileSize = Files.size(tempFile);
        
        log.info("텍스트 보고서 파일 생성 완료: {} (크기: {} bytes)", fileName, fileSize);
        return new FileTaskResult(taskId, fileName, tempFile, "text", fileSize);
    }

    /**
     * CSV 데이터 파일 생성
     */
    public FileTaskResult createCsvFile(int taskId) throws IOException {
        log.info("CSV 데이터 파일 생성 시작 (작업 {})", taskId);
        
        String fileName = "data.csv";
        String content = "ID,Name,Value\n1,Item1,100\n2,Item2,200\n3,Item3,300";
        
        Path tempFile = FileUtil.createTempFile("task_" + taskId + "_", ".csv", content);
        long fileSize = Files.size(tempFile);
        
        log.info("CSV 데이터 파일 생성 완료: {} (크기: {} bytes)", fileName, fileSize);
        return new FileTaskResult(taskId, fileName, tempFile, "csv", fileSize);
    }

    /**
     * JSON 설정 파일 생성
     */
    public FileTaskResult createJsonFile(int taskId) throws IOException {
        log.info("JSON 설정 파일 생성 시작 (작업 {})", taskId);
        
        String fileName = "config.json";
        String content = "{\"taskId\": " + taskId + ", \"status\": \"completed\", \"timestamp\": " + System.currentTimeMillis() + "}";
        
        Path tempFile = FileUtil.createTempFile("task_" + taskId + "_", ".json", content);
        long fileSize = Files.size(tempFile);
        
        log.info("JSON 설정 파일 생성 완료: {} (크기: {} bytes)", fileName, fileSize);
        return new FileTaskResult(taskId, fileName, tempFile, "json", fileSize);
    }

    /**
     * 로그 파일 생성
     */
    public FileTaskResult createLogFile(int taskId) throws IOException {
        log.info("로그 파일 생성 시작 (작업 {})", taskId);
        
        String fileName = "log.log";
        String content = "[INFO] 작업 " + taskId + " 시작\n[INFO] 작업 " + taskId + " 완료\n[INFO] 타임스탬프: " + System.currentTimeMillis();
        
        Path tempFile = FileUtil.createTempFile("task_" + taskId + "_", ".log", content);
        long fileSize = Files.size(tempFile);
        
        log.info("로그 파일 생성 완료: {} (크기: {} bytes)", fileName, fileSize);
        return new FileTaskResult(taskId, fileName, tempFile, "log", fileSize);
    }

    /**
     * 마크다운 요약 파일 생성
     */
    public FileTaskResult createMarkdownFile(int taskId) throws IOException {
        log.info("마크다운 요약 파일 생성 시작 (작업 {})", taskId);
        
        String fileName = "summary.md";
        String content = "# 작업 " + taskId + " 요약\n\n- 상태: 완료\n- 타임스탬프: " + System.currentTimeMillis() + "\n- 생성자: FileGenerationService";
        
        Path tempFile = FileUtil.createTempFile("task_" + taskId + "_", ".md", content);
        long fileSize = Files.size(tempFile);
        
        log.info("마크다운 요약 파일 생성 완료: {} (크기: {} bytes)", fileName, fileSize);
        return new FileTaskResult(taskId, fileName, tempFile, "markdown", fileSize);
    }

    // ===== 테스트용 실패 시나리오 메서드들 =====

    /**
     * 의도적으로 실패하는 텍스트 보고서 파일 생성 (테스트용)
     */
    public FileTaskResult createFailingReportFile(int taskId) throws IOException {
        log.info("실패할 텍스트 보고서 파일 생성 시작 (작업 {})", taskId);
        
        // 의도적으로 예외 발생
        throw new IOException("의도적인 실패: 작업 " + taskId + "에서 파일 생성 실패");
    }

    /**
     * 의도적으로 실패하는 CSV 데이터 파일 생성 (테스트용)
     */
    public FileTaskResult createFailingCsvFile(int taskId) throws IOException {
        log.info("실패할 CSV 데이터 파일 생성 시작 (작업 {})", taskId);
        
        // 의도적으로 예외 발생
        throw new IOException("의도적인 실패: 작업 " + taskId + "에서 CSV 파일 생성 실패");
    }

    /**
     * 의도적으로 실패하는 JSON 설정 파일 생성 (테스트용)
     */
    public FileTaskResult createFailingJsonFile(int taskId) throws IOException {
        log.info("실패할 JSON 설정 파일 생성 시작 (작업 {})", taskId);
        
        // 의도적으로 예외 발생
        throw new IOException("의도적인 실패: 작업 " + taskId + "에서 JSON 파일 생성 실패");
    }
}
