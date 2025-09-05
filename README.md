테이블 생성 명령어
```
CREATE DATABASE IF NOT EXISTS dormdb
  CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

CREATE USER IF NOT EXISTS 'dorm'@'localhost' IDENTIFIED BY 'dormpw';
GRANT ALL PRIVILEGES ON dormdb.* TO 'dorm'@'localhost';
FLUSH PRIVILEGES;
```
