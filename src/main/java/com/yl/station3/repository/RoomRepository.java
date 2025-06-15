package com.yl.station3.repository;

import com.yl.station3.domain.room.Room;
import com.yl.station3.domain.room.RoomType;
import com.yl.station3.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>, RoomRepositoryCustom {
    
    // 사용자별 방 목록 조회 (페이징)
    Page<Room> findByUser(User user, Pageable pageable);
    
    // 방 유형별 조회 (페이징)
    Page<Room> findByRoomType(RoomType roomType, Pageable pageable);
    
    // 전체 방 목록 페이징 (JpaRepository에서 기본 제공)
    Page<Room> findAll(Pageable pageable);
}
