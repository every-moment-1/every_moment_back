package com.rookies4.every_moment.chat.service;

import com.rookies4.every_moment.chat.domain.ChatMessage;
import com.rookies4.every_moment.chat.domain.ChatRoom;
import com.rookies4.every_moment.chat.repo.ChatMessageRepository;
import com.rookies4.every_moment.chat.repo.ChatRoomRepository;
import com.rookies4.every_moment.entity.UserEntity;
import com.rookies4.every_moment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository roomRepo;
    private final ChatMessageRepository msgRepo;
    private final UserRepository userRepo;

    @Transactional
    public ChatRoom getOrCreateRoom(Long me, Long opponent) {
        Long aId = Math.min(me, opponent);
        Long bId = Math.max(me, opponent);

        // 연관필드의 id로 조회
        return roomRepo.findByUserA_IdAndUserB_Id(aId, bId)
                .orElseGet(() -> {
                    // 빌더에는 UserEntity를 넣어야 함
                    UserEntity a = userRepo.getReferenceById(aId);
                    UserEntity b = userRepo.getReferenceById(bId);
                    return roomRepo.save(ChatRoom.builder()
                            .userA(a)
                            .userB(b)
                            .build());
                });
    }

    @Transactional
    public ChatMessage saveMessage(ChatRoom room, Long senderId, String content) {
        UserEntity sender = userRepo.getReferenceById(senderId);
        return msgRepo.save(ChatMessage.builder()
                .room(room)
                .sender(sender)     // ← UserEntity
                .content(content)
                .build());
    }

    @Transactional(readOnly = true)
    public Page<ChatMessage> getMessages(Long roomId, int page, int size) {
        ChatRoom room = roomRepo.findById(roomId).orElseThrow();
        return msgRepo.findByRoomOrderByIdDesc(room, PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public ChatRoom getRoomIfMember(Long roomId, Long userId) {
        ChatRoom room = roomRepo.findById(roomId).orElseThrow();
        if (!room.isParticipant(userId)) throw new SecurityException("Not a member of this room");
        return room;
    }

    @Transactional(readOnly = true)
    public List<ChatRoom> getUserChatRooms(Long userId) {
        return roomRepo.findByParticipantId(userId);
    }
}