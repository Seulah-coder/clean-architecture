# Lecture Enrollment System Architecture Documentation

## Overview

이 프로젝트는 특강 신청 시스템으로, Clean Architecture와 Layered Architecture를 기반으로 설계되었습니다. Spring Boot를 사용하여 구현되었으며, JPA와 H2 데이터베이스를 사용합니다.

## Architecture Principles

### Clean Architecture

- **Domain Layer**: 비즈니스 로직의 핵심. 외부 의존성이 없으며, 엔티티와 도메인 규칙을 포함합니다.
- **Application Layer**: Use Cases를 구현. 도메인 레이어에 의존하며, 트랜잭션과 비즈니스 로직을 조율합니다.
- **Infrastructure Layer**: 외부 시스템과의 인터페이스. 데이터베이스, 외부 API 등을 처리합니다.
- **Presentation Layer**: 사용자 인터페이스. REST API 컨트롤러를 포함합니다.

### Layered Architecture

프로젝트는 다음과 같은 레이어로 구성됩니다:

1. **Presentation Layer** (`presentation/`)
2. **Application Layer** (`application/`)
3. **Domain Layer** (`domain/`)
4. **Infrastructure Layer** (`infrastructure/`)

## Package Structure

```
src/main/java/io/code/lecture/
├── common/                    # 공통 유틸리티, 설정, 예외
│   ├── config/               # Spring 설정
│   └── exception/            # 커스텀 예외
├── domain/                   # 도메인 레이어
│   ├── enrollment/           # 수강 신청 도메인
│   ├── lecture/              # 특강 도메인
│   └── user/                 # 사용자 도메인
├── application/              # 애플리케이션 레이어
│   ├── enrollment/           # 수강 신청 Use Cases
│   └── lecture/              # 특강 Use Cases
├── infrastructure/           # 인프라스트럭처 레이어
│   ├── lock/                 # 동시성 제어
│   └── persistence/          # 데이터 영속성
└── presentation/             # 프레젠테이션 레이어
    ├── api/                  # REST API 컨트롤러
    └── exception/            # 예외 핸들러
```

## Layer Details

### Domain Layer (`domain/`)

- **Entities**: 비즈니스 객체 (Enrollment, Lecture, User)
- **Repository Interfaces**: 데이터 액세스 추상화
- **Domain Logic**: 엔티티 내 비즈니스 규칙

**Key Classes:**

- `Enrollment.java`: 수강 신청 엔티티
- `Lecture.java`: 특강 엔티티 (정원 관리 로직 포함)
- `EnrollmentRepository.java`: 수강 신청 리포지토리 인터페이스

### Application Layer (`application/`)

- **Services**: Use Cases 구현
- **DTOs**: 데이터 전송 객체
- **Commands/Queries**: 입력 데이터 구조

**Key Classes:**

- `EnrollmentService.java`: 수강 신청 비즈니스 로직
- `EnrollmentCommand.java`: 신청 명령 DTO
- `EnrollmentInfo.java`: 응답 정보 DTO

### Infrastructure Layer (`infrastructure/`)

- **Repository Implementations**: JPA 구현
- **Entities**: JPA 엔티티
- **External Interfaces**: 데이터베이스, 캐시 등

**Key Classes:**

- `EnrollmentRepositoryImpl.java`: JPA 기반 리포지토리 구현
- `EnrollmentEntity.java`: JPA 엔티티
- `EnrollmentJpaRepository.java`: Spring Data JPA 리포지토리

### Presentation Layer (`presentation/`)

- **Controllers**: REST API 엔드포인트
- **Request/Response DTOs**: HTTP 요청/응답 구조
- **Exception Handlers**: 예외 처리

**Key Classes:**

- `EnrollmentController.java`: 수강 신청 API 컨트롤러
- `EnrollmentRequest.java`: HTTP 요청 DTO

## Dependency Flow

```
Presentation → Application → Domain ← Infrastructure
```

- Presentation은 Application에 의존
- Application은 Domain에 의존
- Infrastructure는 Domain에 의존 (Dependency Inversion)
- Domain은 아무것에도 의존하지 않음

## Key Design Patterns

### Repository Pattern

- Domain 레이어에서 데이터 액세스를 추상화
- Infrastructure 레이어에서 구체적인 구현 제공

### Dependency Inversion Principle

- 고수준 모듈(Domain)이 저수준 모듈(Infrastructure)에 의존하지 않음
- 둘 다 추상화(인터페이스)에 의존

### Service Layer Pattern

- Application 레이어에서 비즈니스 로직을 캡슐화
- 트랜잭션 관리와 Use Case 조율

## Technologies Used

- **Framework**: Spring Boot 3.5.9
- **Language**: Java 17
- **Database**: H2 (In-memory)
- **ORM**: Spring Data JPA
- **Build Tool**: Gradle
- **Testing**: JUnit 5

## Testing Strategy

- **Unit Tests**: 각 레이어 별 단위 테스트
- **Integration Tests**: 레이어 간 통합 테스트
- **Concurrency Tests**: 동시성 시나리오 테스트

## Concurrency Handling

- JPA Optimistic Locking
- Distributed Lock (Redisson, 예정)
- Transaction Isolation Levels

## API Endpoints

- `POST /api/enrollments`: 특강 신청
- `GET /api/enrollments?userId={id}`: 사용자별 신청 목록 조회

## Build and Run

```bash
./gradlew build
./gradlew bootRun
```

## Test Execution

```bash
./gradlew test
```

테스트 결과는 `build/reports/tests/test/index.html`에서 확인 가능합니다.
