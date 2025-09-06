테이블 생성 명령어
```
CREATE DATABASE IF NOT EXISTS dormdb
  CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

CREATE USER IF NOT EXISTS 'dorm'@'localhost' IDENTIFIED BY 'dormpw';
GRANT ALL PRIVILEGES ON dormdb.* TO 'dorm'@'localhost';
FLUSH PRIVILEGES;
```

파일 구조 정리 및 데이터베이스 연결, 회원가입까지 확인하였습니다.
(username 중복시 오류 발생하여 수정 예정)