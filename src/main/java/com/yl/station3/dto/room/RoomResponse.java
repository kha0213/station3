package com.yl.station3.dto.room;

import com.yl.station3.domain.room.Room;
import com.yl.station3.domain.room.RoomType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class RoomResponse {
    private Long id;
    private String title;
    private String description;
    private String address;
    private RoomType roomType;
    private String ownerName;
    private String ownerEmail;
    private List<RoomDealResponse> roomDeals;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RoomResponse(Room room) {
        this.id = room.getId();
        this.title = room.getTitle();
        this.description = room.getDescription();
        this.address = room.getAddress();
        this.roomType = room.getRoomType();
        this.ownerName = room.getUser().getName();
        this.ownerEmail = room.getUser().getEmail();
        this.roomDeals = room.getRoomDeals().stream()
                .map(deal -> new RoomDealResponse(
                        deal.getId(),
                        deal.getDealType(),
                        deal.getDeposit(),
                        deal.getMonthlyRent()
                ))
                .collect(Collectors.toList());
        this.createdAt = room.getCreatedAt();
        this.updatedAt = room.getUpdatedAt();
    }
}
