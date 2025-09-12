package com.rookies4.every_moment.chat.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rookies4.every_moment.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity @Table(name="chat_message")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChatMessage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // JSON 변환 시 이 필드를 무시하도록 설정
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="room_id", nullable=false)
    private ChatRoom room;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;


    @CreationTimestamp
    @Column(nullable=false) private Instant createdAt = Instant.now();
    private Instant readAt;

    //JSON 변환시 LAZY로 딜레이 떄문에 오류가 남
    // JSON 변환 시 이 필드를 무시하도록 설정
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private UserEntity sender;

    @Transient
    public Long getSenderId() {
        return sender != null ? sender.getId() : null;
    }
}
