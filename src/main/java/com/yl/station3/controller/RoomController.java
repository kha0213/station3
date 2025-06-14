package com.yl.station3.controller;

import com.yl.station3.dto.room.*;
import com.yl.station3.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "Room", description = "방 관련 API")
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class RoomController {

    private final RoomService roomService;

    @Operation(summary = "방 등록", description = "새로운 방을 등록합니다.")
    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(
            @Valid @RequestBody RoomCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        RoomResponse response = roomService.createRoom(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "방 수정", description = "기존 방 정보를 수정합니다. (소유자만 가능)")
    @PutMapping("/{roomId}")
    public ResponseEntity<RoomResponse> updateRoom(
            @Parameter(description = "방 ID") @PathVariable Long roomId,
            @Valid @RequestBody RoomUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        RoomResponse response = roomService.updateRoom(roomId, request, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "방 삭제", description = "방을 삭제합니다. (소유자만 가능)")
    @DeleteMapping("/{roomId}")
    public ResponseEntity<String> deleteRoom(
            @Parameter(description = "방 ID") @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetails userDetails) {
        roomService.deleteRoom(roomId, userDetails.getUsername());
        return ResponseEntity.ok("방이 성공적으로 삭제되었습니다.");
    }

    @Operation(summary = "방 상세 조회", description = "특정 방의 상세 정보를 조회합니다.")
    @GetMapping("/{roomId}")
    public ResponseEntity<RoomResponse> getRoomById(
            @Parameter(description = "방 ID") @PathVariable Long roomId) {
        RoomResponse response = roomService.getRoomById(roomId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내 방 목록 조회", description = "현재 로그인한 사용자가 등록한 방 목록을 조회합니다.")
    @GetMapping("/my")
    public ResponseEntity<List<RoomResponse>> getMyRooms(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<RoomResponse> responses = roomService.getMyRooms(userDetails.getUsername());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "전체 방 목록 조회", description = "등록된 모든 방의 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<RoomResponse>> getAllRooms() {
        List<RoomResponse> responses = roomService.getAllRooms();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "방 검색", description = "조건에 따라 방을 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<List<RoomResponse>> searchRooms(
            @Parameter(description = "방 유형 (ONE_ROOM, TWO_ROOM, THREE_ROOM)") 
            @RequestParam(required = false) String roomType,
            
            @Parameter(description = "거래 유형 (MONTHLY_RENT, JEONSE)") 
            @RequestParam(required = false) String dealType,
            
            @Parameter(description = "최소 보증금") 
            @RequestParam(required = false) String minDeposit,
            
            @Parameter(description = "최대 보증금") 
            @RequestParam(required = false) String maxDeposit,
            
            @Parameter(description = "최소 월세") 
            @RequestParam(required = false) String minRent,
            
            @Parameter(description = "최대 월세") 
            @RequestParam(required = false) String maxRent) {

        RoomSearchRequest searchRequest = buildSearchRequest(
                roomType, dealType, minDeposit, maxDeposit, minRent, maxRent);
        
        List<RoomResponse> responses = roomService.searchRooms(searchRequest);
        return ResponseEntity.ok(responses);
    }

    private RoomSearchRequest buildSearchRequest(String roomType, String dealType,
                                               String minDeposit, String maxDeposit,
                                               String minRent, String maxRent) {
        RoomSearchRequest searchRequest = new RoomSearchRequest();
        
        // 문자열을 Enum으로 변환
        if (roomType != null && !roomType.isEmpty()) {
            try {
                searchRequest.setRoomType(com.yl.station3.domain.room.RoomType.valueOf(roomType.toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("잘못된 방 유형입니다: " + roomType);
            }
        }
        
        if (dealType != null && !dealType.isEmpty()) {
            try {
                searchRequest.setDealType(com.yl.station3.domain.room.DealType.valueOf(dealType.toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("잘못된 거래 유형입니다: " + dealType);
            }
        }
        
        // 문자열을 BigDecimal로 변환
        if (minDeposit != null && !minDeposit.isEmpty()) {
            try {
                searchRequest.setMinDeposit(new java.math.BigDecimal(minDeposit));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("잘못된 최소 보증금 형식입니다: " + minDeposit);
            }
        }
        
        if (maxDeposit != null && !maxDeposit.isEmpty()) {
            try {
                searchRequest.setMaxDeposit(new java.math.BigDecimal(maxDeposit));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("잘못된 최대 보증금 형식입니다: " + maxDeposit);
            }
        }
        
        if (minRent != null && !minRent.isEmpty()) {
            try {
                searchRequest.setMinRent(new java.math.BigDecimal(minRent));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("잘못된 최소 월세 형식입니다: " + minRent);
            }
        }
        
        if (maxRent != null && !maxRent.isEmpty()) {
            try {
                searchRequest.setMaxRent(new java.math.BigDecimal(maxRent));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("잘못된 최대 월세 형식입니다: " + maxRent);
            }
        }
        
        return searchRequest;
    }
}
