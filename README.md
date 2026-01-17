# 🕒 Every Moment (모든 순간) - Backend

**모든 순간(Every Moment)**은 사용자의 성향과 설문을 바탕으로 최적의 인연을 연결하고, 실시간 소통을 지원하는 매칭 플랫폼의 백엔드 시스템입니다.

## 📝 프로젝트 개요
사용자가 진행한 설문 조사 결과를 분석하여 개인화된 매칭 점수를 산출하고, 이를 기반으로 최적의 상대를 추천합니다. 또한 매칭된 사용자 간의 실시간 채팅 기능과 게시판 중심의 커뮤니티 서비스를 제공하여 유기적인 사용자 경험을 지원합니다.

## ✨ 핵심 기능 (Key Features)

### 1. 지능형 매칭 시스템
* **사용자 성향 분석**: 설문(Survey) 데이터를 수집하고 사용자의 선호도(Preference)를 분석합니다.
* **알고리즘 기반 추천**: 사용자 간의 매칭 점수를 산출하여 맞춤형 추천 목록을 제공합니다.
* **매칭 결과 관리**: 매칭 수락/거절 상태를 실시간으로 관리하고 이력을 저장합니다.

### 2. 실시간 소통 서비스
* **실시간 채팅**: WebSocket 및 STOMP를 활용하여 지연 없는 실시간 메시지 전송 시스템을 구축했습니다.
* **채팅방 관리**: 1:1 채팅방 생성 및 메시지 이력 조회 기능을 제공합니다.

### 3. 커뮤니티 (Board)
* **게시글 및 댓글**: 사용자가 자유롭게 게시글을 작성하고 댓글을 달 수 있는 소셜 피드 환경을 지원합니다.
* **활동 로그**: 시스템 내 주요 게시판 활동에 대한 로그를 기록하여 데이터를 관리합니다.

### 4. 보안 및 인증
* **JWT 기반 인증**: 토큰 기반의 무상태(Stateless) 인증 아키텍처를 구현했습니다.
* **인가 시스템**: Spring Security를 통해 엔드포인트별 권한을 설정하고 접근을 제어합니다.

## 🛠 기술 스택 (Tech Stack)

* **언어**: Java 17
* **프레임워크**: Spring Boot 3.2.5
* **데이터베이스**: MySQL
* **지속성 프레임워크**: Spring Data JPA
* **실시간 통신**: WebSocket, STOMP
* **데이터 마이그레이션**: Flyway
* **문서화**: Springdoc-openapi (Swagger)

## 📂 프로젝트 구조 (Simplified)
```text
com.rookies4.every_moment
├── auth        # 인증 및 회원가입 관련 로직
├── board       # 게시판 및 댓글 도메인
├── chat        # 실시간 채팅 도메인
├── match       # 매칭 알고리즘 및 선호도 분석
└── security    # Security 및 JWT 설정
```

---

### DB 생성 명령어
* CREATE DATABASE IF NOT EXISTS dormdb
  CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

* CREATE USER IF NOT EXISTS 'dorm'@'localhost' IDENTIFIED BY 'dormpw';
* GRANT ALL PRIVILEGES ON dormdb.* TO 'dorm'@'localhost';
* FLUSH PRIVILEGES;


파일 구조 정리 및 데이터베이스 연결, 회원가입까지 확인하였습니다.   
(25.09.07 02:49   회원가입시 username 중복되면 오류 발생하여 보완 예정)    
(25.09.08 보완 완료)

* gender추가 관련
* V1이 이미 실행되면 오류가 남 
  * 해결방법 1. # --- Flyway ---
    spring.flyway.enabled=true 여기서 FALSE로 수정
  * 해결방법 2. DB삭제했다 다시 생성


POST 매핑     
회원가입    
/api/school/auth/register
```
{
  "username": "test1",
  "gender":"1",
  "email": "tester@example.com",
  "password": "P@ssw0rd!",
  "smoking": false
}
```
POST  매핑     
로그인     
/api/school/auth/login  

```
    {
      "email": "tester5@example.com",
      "password": "P@ssw0rd!"
    }
```

GET 매핑  
유저정보    
/api/school/user

    Bearer Token: 예시)eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0
    출력
    {
    "data": {
        "id": 4,
        "username": "test1222",
        "gender": 1,
        "email": "tester5@example.com",
        "smoking": false,
        "role": "ROLE_USER",
        "active": true,
        "createdAt": "2025-09-08T14:09:37"
        },
    "timestamp": "2025-09-08T14:15:45.454873600+09:00"
    }
    
