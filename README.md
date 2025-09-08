### 테이블 생성 명령어
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
    ```
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
    ```