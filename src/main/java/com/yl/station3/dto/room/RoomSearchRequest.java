package com.yl.station3.dto.room;

import com.yl.station3.domain.room.DealType;
import com.yl.station3.domain.room.RoomType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class RoomSearchRequest {
    private RoomType roomType;
    private DealType dealType;
    private BigDecimal minDeposit;
    private BigDecimal maxDeposit;
    private BigDecimal minRent;
    private BigDecimal maxRent;

    public RoomSearchRequest(RoomType roomType, DealType dealType, 
                           BigDecimal minDeposit, BigDecimal maxDeposit,
                           BigDecimal minRent, BigDecimal maxRent) {
        this.roomType = roomType;
        this.dealType = dealType;
        this.minDeposit = minDeposit;
        this.maxDeposit = maxDeposit;
        this.minRent = minRent;
        this.maxRent = maxRent;
    }
}
