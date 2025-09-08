### 테이블 생성 명령어
* CREATE DATABASE IF NOT EXISTS dormdb
  CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

* CREATE USER IF NOT EXISTS 'dorm'@'localhost' IDENTIFIED BY 'dormpw';
* GRANT ALL PRIVILEGES ON dormdb.* TO 'dorm'@'localhost';
* FLUSH PRIVILEGES;


파일 구조 정리 및 데이터베이스 연결, 회원가입까지 확인하였습니다.\
(25.09.07 02:49   회원가입시 username 중복되면 오류 발생하여 보완 예정)

* gender추가 관련
* V1이 이미 실행되면 오류가 남 
  * 해결방법 1. # --- Flyway ---
    spring.flyway.enabled=true 여기서 FALSE로 수정
  * 해결방법 2. DB삭제했다 다시 생성