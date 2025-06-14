package com.yl.station3.dto.room;

import com.yl.station3.domain.room.DealType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class RoomDealRequest {

    @NotNull(message = "거래 유형은 필수입니다")
    private DealType dealType;

    @NotNull(message = "보증금은 필수입니다")
    @Positive(message = "보증금은 0보다 커야 합니다")
    private BigDecimal deposit;

    private BigDecimal monthlyRent; // 전세의 경우 null 가능

    public RoomDealRequest(DealType dealType, BigDecimal deposit, BigDecimal monthlyRent) {
        this.dealType = dealType;
        this.deposit = deposit;
        this.monthlyRent = monthlyRent;
    }
}
