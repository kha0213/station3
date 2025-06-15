package com.yl.station3.controller;

import com.yl.station3.dto.common.PageResponse;
import com.yl.station3.dto.room.RoomCreateRequest;
import com.yl.station3.dto.room.RoomResponse;
import com.yl.station3.dto.room.RoomSearchRequest;
import com.yl.station3.dto.room.RoomUpdateRequest;
import com.yl.station3.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "Room", description = "방 관련 API")
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class RoomController {

    private final RoomService roomService;

    @Operation(summary = "방 등록", description = "새로운 방을 등록합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoomResponse createRoom(
            @Valid @RequestBody RoomCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return roomService.createRoom(request, userDetails.getUsername());
    }

    @Operation(summary = "방 수정", description = "기존 방 정보를 수정합니다. (소유자만 가능)")
    @PutMapping("/{roomId}")
    public RoomResponse updateRoom(
            @Parameter(description = "방 ID") @PathVariable Long roomId,
            @Valid @RequestBody RoomUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return roomService.updateRoom(roomId, request, userDetails.getUsername());
    }

    @Operation(summary = "방 삭제", description = "방을 삭제합니다. (소유자만 가능)")
    @DeleteMapping("/{roomId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoom(
            @Parameter(description = "방 ID") @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetails userDetails) {
        roomService.deleteRoom(roomId, userDetails.getUsername());
    }

    @Operation(summary = "방 상세 조회", description = "특정 방의 상세 정보를 조회합니다.")
    @GetMapping("/{roomId}")
    public RoomResponse getRoomById(
            @Parameter(description = "방 ID") @PathVariable Long roomId) {
        return roomService.getRoomById(roomId);
    }

    @Operation(summary = "내 방 목록 조회", description = "현재 로그인한 사용자가 등록한 방 목록을 페이징하여 조회합니다.")
    @GetMapping("/my")
    public PageResponse<RoomResponse> getMyRooms(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "페이지 번호 (1부터 시작)")
            @PageableDefault(sort = "createdAt") Pageable pageable) {
        return roomService.getMyRooms(userDetails.getUsername(), pageable);
    }

    @Operation(summary = "전체 방 목록 조회", description = "등록된 모든 방의 목록을 페이징하여 조회합니다.")
    @GetMapping
    public PageResponse<RoomResponse> getAllRooms(
            @Parameter(description = "페이지 번호 (1부터 시작)")
            @PageableDefault(sort = "createdAt") Pageable pageable) {
        return roomService.getAllRooms(pageable);
    }

    @Operation(summary = "방 검색", description = "조건에 따라 방을 검색하고 페이징하여 결과를 반환합니다.")
    @GetMapping("/search")
    public PageResponse<RoomResponse> searchRooms(
            @ModelAttribute RoomSearchRequest searchRequest,
            @Parameter(description = "페이지 번호 (1부터 시작)")
            @PageableDefault(sort = "createdAt") Pageable pageable) {
        return roomService.searchRooms(searchRequest, pageable);
    }
}
