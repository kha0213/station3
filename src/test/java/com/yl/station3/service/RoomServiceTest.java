package com.yl.station3.service;

import com.yl.station3.TestHelper;
import com.yl.station3.domain.room.DealType;
import com.yl.station3.domain.room.Room;
import com.yl.station3.domain.room.RoomDeal;
import com.yl.station3.domain.room.RoomType;
import com.yl.station3.domain.user.User;
import com.yl.station3.dto.room.RoomCreateRequest;
import com.yl.station3.dto.room.RoomDealRequest;
import com.yl.station3.dto.room.RoomResponse;
import com.yl.station3.dto.room.RoomSearchRequest;
import com.yl.station3.dto.room.RoomUpdateRequest;
import com.yl.station3.exception.BusinessException;
import com.yl.station3.exception.ErrorCode;
import com.yl.station3.repository.RoomRepository;
import com.yl.station3.repository.condition.RoomCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoomService 단위 테스트")
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private RoomService roomService;

    private User testUser;
    private Room testRoom;
    private RoomCreateRequest createRequest;
    private RoomUpdateRequest updateRequest;
    private RoomSearchRequest searchRequest;

    @BeforeEach
    void setUp() {
        // 테스트 사용자
        testUser = User.builder()
                .email("test@example.com")
                .name("테스트 사용자")
                .build();
        TestHelper.setId(testUser, 1L);

        // 테스트 방
        testRoom = Room.builder()
                .title("테스트 방")
                .description("테스트 방 설명")
                .address("서울시 강남구")
                .roomType(RoomType.ONE_ROOM)
                .user(testUser)
                .build();
        testRoom.addRoomDeal(RoomDeal.builder()
                .dealType(DealType.MONTHLY_RENT)
                .deposit(new BigDecimal("1000"))
                .monthlyRent(new BigDecimal("50"))
                .build());

        // 방 생성 요청
        createRequest = new RoomCreateRequest();
        createRequest.setTitle("새로운 방");
        createRequest.setDescription("새로운 방 설명");
        createRequest.setAddress("서울시 강남구");
        createRequest.setRoomType(RoomType.ONE_ROOM);
        
        RoomDealRequest dealRequest = new RoomDealRequest();
        dealRequest.setDealType(DealType.MONTHLY_RENT);
        dealRequest.setDeposit(new BigDecimal("1000"));
        dealRequest.setMonthlyRent(new BigDecimal("50"));
        createRequest.setRoomDeals(Arrays.asList(dealRequest));

        // 방 수정 요청
        updateRequest = new RoomUpdateRequest();
        updateRequest.setTitle("수정된 방");
        updateRequest.setDescription("수정된 방 설명");
        updateRequest.setAddress("서울시 서초구");
        updateRequest.setRoomType(RoomType.TWO_ROOM);
        updateRequest.setRoomDeals(Arrays.asList(dealRequest));

        // 검색 요청
        searchRequest = new RoomSearchRequest();
        searchRequest.setRoomType(RoomType.ONE_ROOM);
        searchRequest.setDealType(DealType.MONTHLY_RENT);
        searchRequest.setMinDeposit(new BigDecimal("500"));
        searchRequest.setMaxDeposit(new BigDecimal("2000"));
    }

    @Test
    @DisplayName("방 생성 성공")
    void createRoom_Success() {
        // given
        when(authService.getCurrentUser("test@example.com")).thenReturn(testUser);
        when(roomRepository.save(any(Room.class))).thenReturn(testRoom);

        // when
        RoomResponse response = roomService.createRoom(createRequest, "test@example.com");

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo(testRoom.getTitle());
        assertThat(response.getAddress()).isEqualTo(testRoom.getAddress());
        assertThat(response.getRoomType()).isEqualTo(testRoom.getRoomType());

        verify(authService).getCurrentUser("test@example.com");
        verify(roomRepository).save(any(Room.class));
    }

    @Test
    @DisplayName("방 수정 성공")
    void updateRoom_Success() {
        // given
        Long roomId = 1L;
        when(authService.getCurrentUser("test@example.com")).thenReturn(testUser);
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));

        // when
        RoomResponse response = roomService.updateRoom(roomId, updateRequest, "test@example.com");

        // then
        assertThat(response).isNotNull();
        verify(authService).getCurrentUser("test@example.com");
        verify(roomRepository).findById(roomId);
    }

    @Test
    @DisplayName("방 수정 실패 - 방 없음")
    void updateRoom_Fail_RoomNotFound() {
        // given
        Long roomId = 1L;
        when(authService.getCurrentUser("test@example.com")).thenReturn(testUser);
        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> roomService.updateRoom(roomId, updateRequest, "test@example.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.ROOM_NOT_FOUND.getMessage());

        verify(authService).getCurrentUser("test@example.com");
        verify(roomRepository).findById(roomId);
    }

    @Test
    @DisplayName("방 수정 실패 - 권한 없음")
    void updateRoom_Fail_AccessDenied() {
        // given
        Long roomId = 1L;
        User otherUser = User.builder()
                .email("other@example.com")
                .name("다른 사용자")
                .build();
        TestHelper.setId(otherUser, 2L);
        
        when(authService.getCurrentUser("other@example.com")).thenReturn(otherUser);
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));

        // when & then
        assertThatThrownBy(() -> roomService.updateRoom(roomId, updateRequest, "other@example.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.ACCESS_DENIED.getMessage());

        verify(authService).getCurrentUser("other@example.com");
        verify(roomRepository).findById(roomId);
    }

    @Test
    @DisplayName("방 삭제 성공")
    void deleteRoom_Success() {
        // given
        Long roomId = 1L;
        when(authService.getCurrentUser("test@example.com")).thenReturn(testUser);
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));

        // when
        assertThatCode(() -> roomService.deleteRoom(roomId, "test@example.com"))
                .doesNotThrowAnyException();

        // then
        verify(authService).getCurrentUser("test@example.com");
        verify(roomRepository).findById(roomId);
        verify(roomRepository).delete(testRoom);
    }

    @Test
    @DisplayName("방 삭제 실패 - 권한 없음")
    void deleteRoom_Fail_AccessDenied() {
        // given
        Long roomId = 1L;
        User otherUser = User.builder()
                .email("other@example.com")
                .name("다른 사용자")
                .build();
        TestHelper.setId(otherUser, 2L);
        
        when(authService.getCurrentUser("other@example.com")).thenReturn(otherUser);
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));

        // when & then
        assertThatThrownBy(() -> roomService.deleteRoom(roomId, "other@example.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.ACCESS_DENIED.getMessage());

        verify(authService).getCurrentUser("other@example.com");
        verify(roomRepository).findById(roomId);
        verify(roomRepository, never()).delete(any(Room.class));
    }

    @Test
    @DisplayName("방 상세 조회 성공")
    void getRoomById_Success() {
        // given
        Long roomId = 1L;
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));

        // when
        RoomResponse response = roomService.getRoomById(roomId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo(testRoom.getTitle());
        assertThat(response.getAddress()).isEqualTo(testRoom.getAddress());

        verify(roomRepository).findById(roomId);
    }

    @Test
    @DisplayName("방 상세 조회 실패 - 방 없음")
    void getRoomById_Fail_RoomNotFound() {
        // given
        Long roomId = 1L;
        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> roomService.getRoomById(roomId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.ROOM_NOT_FOUND.getMessage());

        verify(roomRepository).findById(roomId);
    }

    @Test
    @DisplayName("내 방 목록 조회 성공")
    void getMyRooms_Success() {
        // given
        List<Room> rooms = Arrays.asList(testRoom);
        when(authService.getCurrentUser("test@example.com")).thenReturn(testUser);
        when(roomRepository.findByUser(testUser)).thenReturn(rooms);

        // when
        List<RoomResponse> responses = roomService.getMyRooms("test@example.com");

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getTitle()).isEqualTo(testRoom.getTitle());

        verify(authService).getCurrentUser("test@example.com");
        verify(roomRepository).findByUser(testUser);
    }

    @Test
    @DisplayName("전체 방 목록 조회 성공")
    void getAllRooms_Success() {
        // given
        List<Room> rooms = Arrays.asList(testRoom);
        when(roomRepository.findAll()).thenReturn(rooms);

        // when
        List<RoomResponse> responses = roomService.getAllRooms();

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getTitle()).isEqualTo(testRoom.getTitle());

        verify(roomRepository).findAll();
    }

    @Test
    @DisplayName("방 검색 성공")
    void searchRooms_Success() {
        // given
        List<Room> rooms = Arrays.asList(testRoom);
        RoomCondition condition = RoomCondition
                .builder()
                .roomType(RoomType.ONE_ROOM)
                .dealType(DealType.MONTHLY_RENT)
                .minDeposit(new BigDecimal("500"))
                .maxDeposit(new BigDecimal("2000"))
                .build();

        when(roomRepository.findRoomsByCondition(condition)).thenReturn(rooms);

        // when
        List<RoomResponse> responses = roomService.searchRooms(searchRequest);

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getTitle()).isEqualTo(testRoom.getTitle());

        verify(roomRepository).findRoomsByCondition(condition);
    }

    @Test
    @DisplayName("빈 검색 조건으로 방 검색")
    void searchRooms_EmptyConditions() {
        // given
        RoomSearchRequest emptyRequest = new RoomSearchRequest();
        List<Room> rooms = Arrays.asList(testRoom);
        RoomCondition condition = RoomCondition.builder().build();
        when(roomRepository.findRoomsByCondition(condition)).thenReturn(rooms);

        // when
        List<RoomResponse> responses = roomService.searchRooms(emptyRequest);

        // then
        assertThat(responses).hasSize(1);
        verify(roomRepository).findRoomsByCondition(condition);
    }
}
