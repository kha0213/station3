package com.yl.station3.service;

import com.yl.station3.domain.room.Room;
import com.yl.station3.domain.room.RoomDeal;
import com.yl.station3.domain.user.User;
import com.yl.station3.dto.room.*;
import com.yl.station3.exception.BusinessException;
import com.yl.station3.exception.ErrorCode;
import com.yl.station3.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public List<RoomResponse> getMyRooms(String userEmail) {
        User user = authService.getCurrentUser(userEmail);
        List<Room> rooms = roomRepository.findByUser(user);
        
        return rooms.stream()
                .map(RoomResponse::new)
                .collect(Collectors.toList());
    }

    public List<RoomResponse> getAllRooms() {
        List<Room> rooms = roomRepository.findAll();
        
        return rooms.stream()
                .map(RoomResponse::new)
                .collect(Collectors.toList());
    }

    public List<RoomResponse> searchRooms(RoomSearchRequest searchRequest) {
        List<Room> rooms = roomRepository.findRoomsWithFilters(
                searchRequest.getRoomType(),
                searchRequest.getDealType(),
                searchRequest.getMinDeposit(),
                searchRequest.getMaxDeposit(),
                searchRequest.getMinRent(),
                searchRequest.getMaxRent()
        );

        return rooms.stream()
                .map(RoomResponse::new)
                .collect(Collectors.toList());
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
