package com.yl.station3.repository;

import com.yl.station3.domain.room.DealType;
import com.yl.station3.domain.room.Room;
import com.yl.station3.domain.room.RoomType;
import com.yl.station3.repository.condition.RoomCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface RoomRepositoryCustom {
    
    Page<Room> findRoomsByCondition(RoomCondition condition, Pageable pageable);
    
    Page<Room> findRoomsWithFilters(RoomType roomType, DealType dealType, 
                                   BigDecimal minDeposit, BigDecimal maxDeposit,
                                   BigDecimal minRent, BigDecimal maxRent,
                                   Pageable pageable);
}
