package com.yl.station3.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yl.station3.domain.room.DealType;
import com.yl.station3.domain.room.Room;
import com.yl.station3.domain.room.RoomType;
import com.yl.station3.repository.condition.RoomCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

import static com.yl.station3.domain.room.QRoom.room;
import static com.yl.station3.domain.room.QRoomDeal.roomDeal;

@Repository
@RequiredArgsConstructor
public class RoomRepositoryImpl implements RoomRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Room> findRoomsByCondition(RoomCondition condition) {
        return queryFactory
                .selectFrom(room)
                .distinct()
                .join(room.roomDeals, roomDeal)
                .where(condition.build())
                .fetch();
    }
}
