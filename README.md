# Multiple Threads Application

Spring Boot를 사용한 멀티스레드 작업 처리 애플리케이션입니다.

## 주요 기능

### 1. 기존 기능
- **CompletableFuture를 사용한 멀티스레드 작업**: 여러 작업을 병렬로 실행하고 완료를 기다림
- **CountDownLatch를 사용한 멀티스레드 작업**: CountDownLatch를 활용한 작업 동기화
- **시스템 정보 조회**: JVM 및 OS 정보 확인
- **헬스체크**: 애플리케이션 상태 확인

### 2. 새로 추가된 기능
- **파일 생성 및 압축 다운로드**: 각 스레드가 다른 종류의 파일을 생성하고, 모든 스레드 완료 후 ZIP으로 압축하여 다운로드

## API 엔드포인트

### 파일 생성 및 다운로드
```
GET /api/threads/create-files-and-download
```
- 각 스레드가 다른 종류의 파일을 생성
- 모든 스레드 완료 후 ZIP 파일로 압축
- 파일 다운로드 응답으로 반환

### 기존 엔드포인트
```
GET /api/threads/completable-future    # CompletableFuture 사용
GET /api/threads/countdown-latch       # CountDownLatch 사용
GET /api/threads/system-info           # 시스템 정보
GET /api/threads/health                # 헬스체크
```

## 생성되는 파일 종류

각 스레드는 다음과 같은 다른 종류의 파일을 생성합니다:

1. **report.txt** - 텍스트 보고서
2. **data.csv** - CSV 데이터 파일
3. **config.json** - JSON 설정 파일
4. **log.log** - 로그 파일
5. **summary.md** - 마크다운 요약 파일

## 기술 스택

- **Java 17**
- **Spring Boot 3.2.0**
- **Maven**
- **Lombok**

## 실행 방법

1. 프로젝트 클론
```bash
git clone <repository-url>
cd multiple-threads
```

2. Maven으로 빌드
```bash
mvn clean package
```

3. 애플리케이션 실행
```bash
java -jar target/multiple-threads-1.0.0.jar
```

4. 브라우저에서 테스트
```
http://localhost:8080/api/threads/create-files-and-download
```

## 동작 원리

1. **병렬 파일 생성**: 5개의 스레드가 동시에 각각 다른 종류의 파일을 생성
2. **작업 완료 대기**: `CompletableFuture.allOf()`를 사용하여 모든 스레드 완료 대기
3. **파일 압축**: 성공적으로 생성된 모든 파일을 ZIP으로 압축
4. **다운로드 응답**: 압축된 ZIP 파일을 HTTP 응답으로 반환하여 브라우저에서 자동 다운로드
5. **리소스 정리**: 임시 파일들을 자동으로 삭제

## 주의사항

- 각 스레드는 1-3초의 랜덤 지연을 가져 실제 작업 시뮬레이션
- 임시 파일들은 자동으로 정리되므로 디스크 공간을 차지하지 않음
- 오류 발생 시 JSON 형태의 에러 응답 반환
