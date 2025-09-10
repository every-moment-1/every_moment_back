-- 1:1 채팅방 (두 유저 조합 1개 고정)
DROP TABLE IF EXISTS chat_room;
CREATE TABLE chat_room (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_a_id    BIGINT NOT NULL,
  user_b_id    BIGINT NOT NULL,
  created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uq_one_on_one (user_a_id, user_b_id)
) ENGINE=InnoDB;

-- 메시지 로그
DROP TABLE IF EXISTS chat_message;
CREATE TABLE chat_message (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  room_id      BIGINT NOT NULL,
  sender_id    BIGINT NOT NULL,
  content      TEXT   NOT NULL,
  created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  read_at      TIMESTAMP NULL,
  CONSTRAINT fk_msg_room FOREIGN KEY (room_id) REFERENCES chat_room(id) ON DELETE CASCADE
) ENGINE=InnoDB;