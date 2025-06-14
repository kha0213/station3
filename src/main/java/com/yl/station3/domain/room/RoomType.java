package com.yl.station3.domain.room;

import lombok.Getter;

@Getter
public enum RoomType {
    ONE_ROOM("원룸"),
    TWO_ROOM("투룸"),
    THREE_ROOM("쓰리룸");

    private final String description;

    RoomType(String description) {
        this.description = description;
    }
}
