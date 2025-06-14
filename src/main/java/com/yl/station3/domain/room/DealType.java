package com.yl.station3.domain.room;

import lombok.Getter;

@Getter
public enum DealType {
    MONTHLY_RENT("월세"),
    JEONSE("전세");

    private final String description;

    DealType(String description) {
        this.description = description;
    }
}
