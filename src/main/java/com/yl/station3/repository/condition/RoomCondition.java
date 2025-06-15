package com.yl.station3.repository.condition;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.yl.station3.domain.room.DealType;
import com.yl.station3.domain.room.RoomType;
import lombok.Builder;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

import static com.yl.station3.domain.room.QRoom.room;
import static com.yl.station3.domain.room.QRoomDeal.roomDeal;

@EqualsAndHashCode
@Builder
public class RoomCondition {
    private RoomType roomType;
    private DealType dealType;
    private BigDecimal minDeposit;
    private BigDecimal maxDeposit;
    private BigDecimal minRent;
    private BigDecimal maxRent;

    private BooleanExpression roomTypeEquals() {
        if (roomType == null) {
            return null;
        }
        return room.roomType.eq(this.roomType);
    }

    private BooleanExpression dealTypeEquals() {
        if (dealType == null) {
            return null;
        }
        return roomDeal.dealType.eq(this.dealType);
    }

    private BooleanExpression depositGreaterThan() {
        if (minDeposit == null) {
            return null;
        }
        return roomDeal.deposit.goe(this.minDeposit);
    }

    private BooleanExpression depositLessThan() {
        if (maxDeposit == null) {
            return null;
        }
        return roomDeal.deposit.loe(this.maxDeposit);
    }

    private BooleanExpression rentGreaterThan() {
        if (minRent == null) {
            return null;
        }
        return roomDeal.monthlyRent.goe(this.minRent);
    }

    private BooleanExpression rentLessThan() {
        if (maxRent == null) {
            return null;
        }
        return roomDeal.monthlyRent.loe(this.maxRent);
    }

    public Predicate build() {
        return ExpressionUtils.allOf(
                roomTypeEquals(),
                dealTypeEquals(),
                depositGreaterThan(),
                depositLessThan(),
                rentGreaterThan(),
                rentLessThan()
        );
    }
}
