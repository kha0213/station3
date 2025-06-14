package com.yl.station3.repository;

import com.yl.station3.domain.room.Room;
import com.yl.station3.domain.room.RoomType;
import com.yl.station3.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>, RoomRepositoryCustom {
    
    // 사용자별 방 목록 조회
    List<Room> findByUser(User user);
    
    // 방 유형별 조회
    List<Room> findByRoomType(RoomType roomType);
}
