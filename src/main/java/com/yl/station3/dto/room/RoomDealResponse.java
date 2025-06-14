package com.yl.station3.dto.room;

import com.yl.station3.domain.room.DealType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class RoomDealResponse {
    private Long id;
    private DealType dealType;
    private BigDecimal deposit;
    private BigDecimal monthlyRent;
}
