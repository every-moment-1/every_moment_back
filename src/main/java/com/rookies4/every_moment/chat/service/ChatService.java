package com.rookies4.every_moment.chat.service;

import com.rookies4.every_moment.chat.domain.ChatMessage;
import com.rookies4.every_moment.chat.domain.ChatRoom;
import com.rookies4.every_moment.chat.repo.ChatMessageRepository;
import com.rookies4.every_moment.chat.repo.ChatRoomRepository;
import com.rookies4.every_moment.entity.UserEntity;
import com.rookies4.every_moment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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

        return roomRepo.findByUserA_IdAndUserB_Id(aId, bId)
                .orElseGet(() -> {
                    try {
                        UserEntity a = userRepo.getReferenceById(aId);
                        UserEntity b = userRepo.getReferenceById(bId);
                        return roomRepo.save(ChatRoom.builder()
                                .userA(a)
                                .userB(b)
                                .build());
                    } catch (DataIntegrityViolationException dup) {
                        return roomRepo.findByUserA_IdAndUserB_Id(aId, bId)
                                .orElseThrow(() -> dup);
                    }
                });
    }

    @Transactional
    public ChatMessage saveMessage(ChatRoom room, Long senderId, String content) {
        UserEntity sender = userRepo.getReferenceById(senderId);
        return msgRepo.save(ChatMessage.builder()
                .room(room)
                .sender(sender)
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

    /** ✅ 내 방 목록 (참여자까지 fetch 조인된 버전 사용) */
    @Transactional(readOnly = true)
    public List<ChatRoom> getUserChatRooms(Long userId) {
        return roomRepo.findByParticipantIdWithUsers(userId);
    }

    /** (기존 유지) 전체 방 - 단순 버전(필요 시 사용) */
    @Transactional(readOnly = true)
    public List<ChatRoom> getAllRooms() {
        return roomRepo.findAll();
    }

    /** ✅ 전체 방 (참여자까지 함께 로드) */
    @Transactional(readOnly = true)
    public List<ChatRoom> getAllRoomsWithUsers() {
        return roomRepo.findAllWithUsers();
    }

    /** ✅ 단건 조회도 참여자 함께 로드 */
    @Transactional(readOnly = true)
    public ChatRoom getRoomWithUsers(Long roomId) {
        return roomRepo.findByIdWithUsers(roomId).orElseThrow();
    }

    /** ✅ 관리자 여부(DB 기준) */
    @Transactional(readOnly = true)
    public boolean isAdminUser(Long userId) {
        return userRepo.findById(userId)
                .map(u -> {
                    String name = String.valueOf(u.getRole());
                    return "ADMIN".equalsIgnoreCase(name) || "ROLE_ADMIN".equalsIgnoreCase(name);
                })
                .orElse(false);
    }
}
