package com.rookies4.every_moment.chat.repo;

import com.rookies4.every_moment.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    default Long normalizeA(Long a, Long b){ return Math.min(a,b); }
    default Long normalizeB(Long a, Long b){ return Math.max(a,b); }

    Optional<ChatRoom> findByUserA_IdAndUserB_Id(Long userAId, Long userBId);

    @Query("""
        select r from ChatRoom r
        where (r.userA.id = :a and r.userB.id = :b)
           or (r.userA.id = :b and r.userB.id = :a)
    """)
    Optional<ChatRoom> findOneOnOne(@Param("a") Long a, @Param("b") Long b);

    // 내 방 목록(간단)
    @Query("select r from ChatRoom r where r.userA.id = :userId or r.userB.id = :userId")
    List<ChatRoom> findByParticipantId(@Param("userId") Long userId);

    // ✅ N+1 방지: 방 + 참여자 두 명을 함께 로드
    @Query("""
        select distinct r
        from ChatRoom r
        join fetch r.userA a
        join fetch r.userB b
    """)
    List<ChatRoom> findAllWithUsers();

    // ✅ 내 방만(참여자까지 함께)
    @Query("""
        select distinct r
        from ChatRoom r
        join fetch r.userA a
        join fetch r.userB b
        where a.id = :userId or b.id = :userId
    """)
    List<ChatRoom> findByParticipantIdWithUsers(@Param("userId") Long userId);

    // ✅ 단건 + 참여자까지 함께
    @Query("""
        select r
        from ChatRoom r
        join fetch r.userA a
        join fetch r.userB b
        where r.id = :roomId
    """)
    Optional<ChatRoom> findByIdWithUsers(@Param("roomId") Long roomId);
}
