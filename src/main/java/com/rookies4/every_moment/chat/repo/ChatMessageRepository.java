package com.rookies4.every_moment.chat.repo;


import com.rookies4.every_moment.chat.domain.ChatMessage;
import com.rookies4.every_moment.chat.domain.ChatRoom;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Page<ChatMessage> findByRoomOrderByIdDesc(ChatRoom room, Pageable pageable);
}