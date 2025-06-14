package com.yl.station3.dto.room;

import com.yl.station3.domain.room.RoomType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@NoArgsConstructor
public class RoomUpdateRequest {

    @NotBlank(message = "방 제목은 필수입니다")
    @Size(max = 100, message = "방 제목은 100자 이하여야 합니다")
    private String title;

    @Size(max = 1000, message = "방 설명은 1000자 이하여야 합니다")
    private String description;

    @NotBlank(message = "주소는 필수입니다")
    @Size(max = 200, message = "주소는 200자 이하여야 합니다")
    private String address;

    @NotNull(message = "방 유형은 필수입니다")
    private RoomType roomType;

    @NotEmpty(message = "거래 정보는 최소 하나 이상 등록해야 합니다")
    @Valid
    private List<RoomDealRequest> roomDeals;

    public RoomUpdateRequest(String title, String description, String address, 
                           RoomType roomType, List<RoomDealRequest> roomDeals) {
        this.title = title;
        this.description = description;
        this.address = address;
        this.roomType = roomType;
        this.roomDeals = roomDeals;
    }
}
