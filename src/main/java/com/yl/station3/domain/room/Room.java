package com.yl.station3.domain.room;

import com.yl.station3.common.BaseEntity;
import com.yl.station3.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rooms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", nullable = false)
    private RoomType roomType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomDeal> roomDeals = new ArrayList<>();

    @Builder
    public Room(String title, String description, String address, RoomType roomType, User user) {
        this.title = title;
        this.description = description;
        this.address = address;
        this.roomType = roomType;
        this.user = user;
    }

    public void update(String title, String description, String address, RoomType roomType) {
        this.title = title;
        this.description = description;
        this.address = address;
        this.roomType = roomType;
    }

    public void addRoomDeal(RoomDeal roomDeal) {
        this.roomDeals.add(roomDeal);
        roomDeal.setRoom(this);
    }
}
