package com.yl.station3.repository;

import com.yl.station3.domain.room.DealType;
import com.yl.station3.domain.room.Room;
import com.yl.station3.domain.room.RoomType;

import java.math.BigDecimal;
import java.util.List;

public interface RoomRepositoryCustom {
    
    List<Room> findRoomsWithFilters(
            RoomType roomType,
            DealType dealType,
            BigDecimal minDeposit,
            BigDecimal maxDeposit,
            BigDecimal minRent,
            BigDecimal maxRent
    );
}
