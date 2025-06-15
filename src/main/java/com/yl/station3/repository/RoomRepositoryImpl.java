package com.yl.station3.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yl.station3.domain.room.DealType;
import com.yl.station3.domain.room.Room;
import com.yl.station3.domain.room.RoomType;
import com.yl.station3.repository.condition.RoomCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
    public Page<Room> findRoomsByCondition(RoomCondition condition, Pageable pageable) {
        List<Room> content = queryFactory
                .selectFrom(room)
                .distinct()
                .join(room.roomDeals, roomDeal)
                .where(condition.build())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(room)
                .distinct()
                .join(room.roomDeals, roomDeal)
                .where(condition.build())
                .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Room> findRoomsWithFilters(RoomType roomType, DealType dealType,
                                          BigDecimal minDeposit, BigDecimal maxDeposit,
                                          BigDecimal minRent, BigDecimal maxRent,
                                          Pageable pageable) {
        BooleanBuilder builder = createFilterConditions(roomType, dealType, minDeposit, maxDeposit, minRent, maxRent);
        
        List<Room> content = queryFactory
                .selectFrom(room)
                .distinct()
                .join(room.roomDeals, roomDeal)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(room)
                .distinct()
                .join(room.roomDeals, roomDeal)
                .where(builder)
                .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanBuilder createFilterConditions(RoomType roomType, DealType dealType,
                                                 BigDecimal minDeposit, BigDecimal maxDeposit,
                                                 BigDecimal minRent, BigDecimal maxRent) {
        BooleanBuilder builder = new BooleanBuilder();

        if (roomType != null) {
            builder.and(room.roomType.eq(roomType));
        }

        if (dealType != null) {
            builder.and(roomDeal.dealType.eq(dealType));
        }

        if (minDeposit != null) {
            builder.and(roomDeal.deposit.goe(minDeposit));
        }

        if (maxDeposit != null) {
            builder.and(roomDeal.deposit.loe(maxDeposit));
        }

        if (minRent != null) {
            builder.and(roomDeal.monthlyRent.goe(minRent));
        }

        if (maxRent != null) {
            builder.and(roomDeal.monthlyRent.loe(maxRent));
        }

        return builder;
    }
}
