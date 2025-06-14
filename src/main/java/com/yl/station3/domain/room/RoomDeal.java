package com.yl.station3.domain.room;

import com.yl.station3.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "room_deals")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomDeal extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "deal_type", nullable = false)
    private DealType dealType;

    @Column(name = "deposit", nullable = false, precision = 10, scale = 0)
    private BigDecimal deposit; // 보증금

    @Column(name = "monthly_rent", precision = 10, scale = 0)
    private BigDecimal monthlyRent; // 월세 (전세의 경우 null)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @Builder
    public RoomDeal(DealType dealType, BigDecimal deposit, BigDecimal monthlyRent, Room room) {
        this.dealType = dealType;
        this.deposit = deposit;
        this.monthlyRent = monthlyRent;
        this.room = room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}
