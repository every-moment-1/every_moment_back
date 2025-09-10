package com.rookies4.every_moment.chat.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rookies4.every_moment.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(
        name = "chat_room",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_one_on_one",
                        columnNames = {"user_a_id", "user_b_id"}
                )
        },
        indexes = {
                @Index(name = "idx_chatroom_user_a", columnList = "user_a_id"),
                @Index(name = "idx_chatroom_user_b", columnList = "user_b_id")
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Json 변환 시 이 필드를 무시하도록 설정
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_a_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_chatroom_user_a"))
    private UserEntity userA;

    // Json 변환 시 이 필드를 무시하도록 설정
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_b_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_chatroom_user_b"))
    private UserEntity userB;
    @Transient
    public Long getUserAId() {
        return userA != null ? userA.getId() : null;
    }

    @Transient
    public Long getUserBId() {
        return userB != null ? userB.getId() : null;
    }
    public boolean isParticipant(Long userId) {
        return (userA != null && Objects.equals(userA.getId(), userId))
                || (userB != null && Objects.equals(userB.getId(), userId));
    }
}
