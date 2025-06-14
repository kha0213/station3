package com.yl.station3.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yl.station3.domain.room.DealType;
import com.yl.station3.domain.room.Room;
import com.yl.station3.domain.room.RoomType;
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
    public List<Room> findRoomsWithFilters(RoomType roomType,
                                          DealType dealType,
                                          BigDecimal minDeposit,
                                          BigDecimal maxDeposit,
                                          BigDecimal minRent,
                                          BigDecimal maxRent) {

        BooleanBuilder builder = new BooleanBuilder();

        // 방 유형 필터
        if (roomType != null) {
            builder.and(room.roomType.eq(roomType));
        }

        // 거래 유형 필터
        if (dealType != null) {
            builder.and(roomDeal.dealType.eq(dealType));
        }

        // 보증금 범위 필터
        if (minDeposit != null) {
            builder.and(roomDeal.deposit.goe(minDeposit));
        }
        if (maxDeposit != null) {
            builder.and(roomDeal.deposit.loe(maxDeposit));
        }

        // 월세 범위 필터 (전세의 경우 monthlyRent가 null일 수 있음)
        if (minRent != null) {
            builder.and(roomDeal.monthlyRent.isNull().or(roomDeal.monthlyRent.goe(minRent)));
        }
        if (maxRent != null) {
            builder.and(roomDeal.monthlyRent.isNull().or(roomDeal.monthlyRent.loe(maxRent)));
        }

        return queryFactory
                .selectFrom(room)
                .distinct()
                .join(room.roomDeals, roomDeal)
                .where(builder)
                .fetch();
    }
}
