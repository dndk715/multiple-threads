# Multiple Threads Completion Project

스프링부트를 사용하여 여러 스레드가 모두 완료된 후에 로직을 처리하는 프로젝트입니다.

## 주요 기능

- **CompletableFuture**: 여러 비동기 작업을 병렬로 실행하고 모든 작업 완료를 기다림
- **CountDownLatch**: 대안적인 방법으로 여러 스레드의 완료를 기다림
- **스레드 풀 관리**: CPU 코어 수에 맞는 최적화된 스레드 풀 사용
- **REST API**: HTTP 엔드포인트를 통한 작업 실행 및 모니터링

## 기술 스택

- Java 17
- Spring Boot 3.2.0
- Maven
- Lombok

## 프로젝트 구조

```
src/main/java/com/example/multiplethreads/
├── MultipleThreadsApplication.java    # 메인 애플리케이션
├── controller/
│   └── ThreadController.java         # REST API 컨트롤러
├── service/
│   └── ThreadCompletionService.java  # 스레드 작업 관리 서비스
└── config/
    └── ApplicationConfig.java        # 애플리케이션 설정
```

## 실행 방법

### 1. 프로젝트 빌드
```bash
mvn clean compile
```

### 2. 애플리케이션 실행
```bash
mvn spring-boot:run
```

### 3. 애플리케이션 접속
- 애플리케이션: http://localhost:8080
- API 문서: http://localhost:8080/api/threads

## API 엔드포인트

### 1. CompletableFuture를 사용한 여러 스레드 작업
```bash
POST /api/threads/completable-future
```

**응답 예시:**
```json
{
  "status": "success",
  "message": "모든 작업이 완료되었습니다.",
  "totalDuration": "3247ms",
  "method": "CompletableFuture"
}
```

### 2. CountDownLatch를 사용한 여러 스레드 작업
```bash
POST /api/threads/countdown-latch
```

### 3. 시스템 정보 조회
```bash
GET /api/threads/system-info
```

### 4. 헬스체크
```bash
GET /api/threads/health
```

## 핵심 구현 내용

### CompletableFuture 방식
```java
// 여러 작업을 병렬로 실행
List<CompletableFuture<String>> futures = new ArrayList<>();
for (int i = 1; i <= 5; i++) {
    CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
        return executeTask(i);
    }, executorService);
    futures.add(future);
}

// 모든 작업이 완료될 때까지 기다림
CompletableFuture<Void> allFutures = CompletableFuture.allOf(
    futures.toArray(new CompletableFuture[0])
);

// 모든 작업 완료 후 결과 처리
allFutures.thenRun(() -> {
    // 결과 처리 로직
}).join();
```

### CountDownLatch 방식
```java
CountDownLatch latch = new CountDownLatch(taskCount);
List<String> results = new CopyOnWriteArrayList<>();

for (int i = 1; i <= taskCount; i++) {
    executorService.submit(() -> {
        try {
            String result = executeTask(i);
            results.add(result);
        } finally {
            latch.countDown();
        }
    });
}

// 모든 작업이 완료될 때까지 기다림
latch.await(30, TimeUnit.SECONDS);
```

## 장점

1. **병렬 처리**: 여러 작업을 동시에 실행하여 전체 처리 시간 단축
2. **완료 대기**: 모든 작업이 완료된 후에 후속 처리 보장
3. **에러 처리**: 개별 작업의 실패를 적절히 처리
4. **리소스 관리**: 스레드 풀을 통한 효율적인 스레드 관리
5. **확장성**: 작업 수와 스레드 풀 크기를 쉽게 조정 가능

## 사용 사례

- **데이터 처리**: 여러 데이터 소스에서 데이터를 병렬로 수집 후 통합 처리
- **파일 처리**: 여러 파일을 동시에 처리한 후 결과 통합
- **API 호출**: 여러 외부 API를 병렬로 호출한 후 응답 통합
- **배치 작업**: 여러 배치 작업을 동시에 실행한 후 완료 상태 확인

## 주의사항

1. **메모리 사용량**: 많은 작업을 동시에 실행할 때 메모리 사용량 증가
2. **스레드 풀 크기**: CPU 코어 수를 고려하여 적절한 스레드 풀 크기 설정
3. **타임아웃 설정**: 무한 대기를 방지하기 위한 적절한 타임아웃 설정
4. **예외 처리**: 개별 작업의 실패가 전체 프로세스에 미치는 영향 고려

## 테스트

### cURL을 사용한 테스트
```bash
# CompletableFuture 방식 테스트
curl -X POST http://localhost:8080/api/threads/completable-future

# CountDownLatch 방식 테스트
curl -X POST http://localhost:8080/api/threads/countdown-latch

# 시스템 정보 조회
curl http://localhost:8080/api/threads/system-info

# 헬스체크
curl http://localhost:8080/api/threads/health
```

## 로그 확인

애플리케이션 실행 시 콘솔에서 다음과 같은 로그를 확인할 수 있습니다:

```
2024-01-01 12:00:00 [main] INFO  c.e.m.MultipleThreadsApplication - Started MultipleThreadsApplication
2024-01-01 12:00:00 [main] INFO  c.e.m.config.ApplicationConfig - 애플리케이션이 시작되었습니다.
2024-01-01 12:00:00 [main] INFO  c.e.m.config.ApplicationConfig - 사용 가능한 API 엔드포인트:
2024-01-01 12:00:00 [main] INFO  c.e.m.config.ApplicationConfig -   POST /api/threads/completable-future
2024-01-01 12:00:00 [main] INFO  c.e.m.config.ApplicationConfig -   POST /api/threads/countdown-latch
```
