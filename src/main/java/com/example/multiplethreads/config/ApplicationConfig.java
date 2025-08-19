package com.example.multiplethreads.config;

import com.example.multiplethreads.service.ThreadCompletionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationConfig {

    private final ThreadCompletionService threadCompletionService;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("애플리케이션이 시작되었습니다.");
        log.info("사용 가능한 API 엔드포인트:");
        log.info("  GET  /api/threads/create-files-with-service-and-download - 서비스 기반 파일 생성 및 다운로드");
        log.info("  GET  /api/threads/create-files-with-failure-and-download - 실패 시나리오 테스트 (테스트용)");
        log.info("  GET  /api/threads/system-info - 시스템 정보 조회");
        log.info("  GET  /api/threads/health - 헬스체크");
    }

    @EventListener(ContextClosedEvent.class)
    public void onApplicationClosed() {
        log.info("애플리케이션이 종료됩니다. 리소스를 정리합니다...");
        threadCompletionService.shutdown();
        log.info("리소스 정리가 완료되었습니다.");
    }
}
