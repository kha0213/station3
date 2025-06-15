package com.yl.station3.service;

import com.yl.station3.domain.room.Room;
import com.yl.station3.domain.room.RoomDeal;
import com.yl.station3.domain.user.User;
import com.yl.station3.dto.common.PageResponse;
import com.yl.station3.dto.room.RoomCreateRequest;
import com.yl.station3.dto.room.RoomResponse;
import com.yl.station3.dto.room.RoomSearchRequest;
import com.yl.station3.dto.room.RoomUpdateRequest;
import com.yl.station3.exception.BusinessException;
import com.yl.station3.exception.ErrorCode;
import com.yl.station3.repository.RoomRepository;
import com.yl.station3.repository.condition.RoomCondition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomService {

    private final RoomRepository roomRepository;
    private final AuthService authService;

    @Transactional
    public RoomResponse createRoom(RoomCreateRequest request, String userEmail) {
        User user = authService.getCurrentUser(userEmail);

        // 방 생성
        Room room = Room.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .address(request.getAddress())
                .roomType(request.getRoomType())
                .user(user)
                .build();

        // 거래 정보 추가
        request.getRoomDeals().forEach(dealRequest -> {
            RoomDeal roomDeal = RoomDeal.builder()
                    .dealType(dealRequest.getDealType())
                    .deposit(dealRequest.getDeposit())
                    .monthlyRent(dealRequest.getMonthlyRent())
                    .build();
            room.addRoomDeal(roomDeal);
        });

        Room savedRoom = roomRepository.save(room);
        log.info("새 방 등록: {} by {}", savedRoom.getTitle(), userEmail);

        return new RoomResponse(savedRoom);
    }

    @Transactional
    public RoomResponse updateRoom(Long roomId, RoomUpdateRequest request, String userEmail) {
        User user = authService.getCurrentUser(userEmail);
        Room room = getRoomByIdAndUser(roomId, user);

        // 방 정보 업데이트
        room.update(request.getTitle(), request.getDescription(), 
                   request.getAddress(), request.getRoomType());

        // 기존 거래 정보 삭제 후 새로 추가
        room.getRoomDeals().clear();
        request.getRoomDeals().forEach(dealRequest -> {
            RoomDeal roomDeal = RoomDeal.builder()
                    .dealType(dealRequest.getDealType())
                    .deposit(dealRequest.getDeposit())
                    .monthlyRent(dealRequest.getMonthlyRent())
                    .build();
            room.addRoomDeal(roomDeal);
        });

        log.info("방 정보 수정: {} by {}", room.getTitle(), userEmail);
        return new RoomResponse(room);
    }

    @Transactional
    public void deleteRoom(Long roomId, String userEmail) {
        User user = authService.getCurrentUser(userEmail);
        Room room = getRoomByIdAndUser(roomId, user);

        roomRepository.delete(room);
        log.info("방 삭제: {} by {}", room.getTitle(), userEmail);
    }

    public RoomResponse getRoomById(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));
        
        return new RoomResponse(room);
    }

    public PageResponse<RoomResponse> getMyRooms(String userEmail, Pageable pageable) {
        User user = authService.getCurrentUser(userEmail);
        Page<Room> roomPage = roomRepository.findByUser(user, pageable);
        
        Page<RoomResponse> responsePage = roomPage.map(RoomResponse::new);
        return PageResponse.of(responsePage);
    }

    public PageResponse<RoomResponse> getAllRooms(Pageable pageable) {
        Page<Room> roomPage = roomRepository.findAll(pageable);
        
        Page<RoomResponse> responsePage = roomPage.map(RoomResponse::new);
        return PageResponse.of(responsePage);
    }

    public PageResponse<RoomResponse> searchRooms(RoomSearchRequest searchRequest, Pageable pageable) {
        RoomCondition condition = RoomCondition.builder()
                .roomType(searchRequest.getRoomType())
                .dealType(searchRequest.getDealType())
                .minDeposit(searchRequest.getMinDeposit())
                .maxDeposit(searchRequest.getMaxDeposit())
                .minRent(searchRequest.getMinRent())
                .maxRent(searchRequest.getMaxRent())
                .build();
        Page<Room> roomPage = roomRepository.findRoomsByCondition(condition, pageable);

        Page<RoomResponse> responsePage = roomPage.map(RoomResponse::new);
        return PageResponse.of(responsePage);
    }

    private Room getRoomByIdAndUser(Long roomId, User user) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        if (!room.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        return room;
    }
}
