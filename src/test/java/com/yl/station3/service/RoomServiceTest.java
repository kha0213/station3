package com.yl.station3.service;

import com.yl.station3.TestHelper;
import com.yl.station3.domain.room.DealType;
import com.yl.station3.domain.room.Room;
import com.yl.station3.domain.room.RoomDeal;
import com.yl.station3.domain.room.RoomType;
import com.yl.station3.domain.user.User;
import com.yl.station3.dto.common.PageResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
    @DisplayName("내 방 목록 조회 성공 (페이징)")
    void getMyRooms_Success() {
        // given
        List<Room> rooms = Arrays.asList(testRoom);
        Page<Room> roomPage = new PageImpl<>(rooms, PageRequest.of(0, 10), 1);
        when(authService.getCurrentUser("test@example.com")).thenReturn(testUser);
        when(roomRepository.findByUser(testUser, any(Pageable.class))).thenReturn(roomPage);

        // when
        PageResponse<RoomResponse> responses = roomService.getMyRooms("test@example.com", PageRequest.of(0, 10));

        // then
        assertThat(responses).isNotNull();
        assertThat(responses.getContent()).hasSize(1);
        assertThat(responses.getContent().get(0).getTitle()).isEqualTo(testRoom.getTitle());
        assertThat(responses.getTotalElements()).isEqualTo(1);
        assertThat(responses.getTotalPages()).isEqualTo(1);

        verify(authService).getCurrentUser("test@example.com");
        verify(roomRepository).findByUser(testUser, any(Pageable.class));
    }

    @Test
    @DisplayName("전체 방 목록 조회 성공 (페이징)")
    void getAllRooms_Success() {
        // given
        List<Room> rooms = Arrays.asList(testRoom);
        Page<Room> roomPage = new PageImpl<>(rooms, PageRequest.of(0, 20), 1);
        when(roomRepository.findAll(any(Pageable.class))).thenReturn(roomPage);

        // when
        PageResponse<RoomResponse> responses = roomService.getAllRooms(PageRequest.of(0, 20));

        // then
        assertThat(responses).isNotNull();
        assertThat(responses.getContent()).hasSize(1);
        assertThat(responses.getContent().get(0).getTitle()).isEqualTo(testRoom.getTitle());
        assertThat(responses.getTotalElements()).isEqualTo(1);
        assertThat(responses.getTotalPages()).isEqualTo(1);

        verify(roomRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("방 검색 성공 (페이징)")
    void searchRooms_Success() {
        // given
        List<Room> rooms = Arrays.asList(testRoom);
        Page<Room> roomPage = new PageImpl<>(rooms, PageRequest.of(0, 20), 1);
        when(roomRepository.findRoomsByCondition(any(RoomCondition.class), any(Pageable.class))).thenReturn(roomPage);

        // when
        PageResponse<RoomResponse> responses = roomService.searchRooms(searchRequest, PageRequest.of(0, 20));

        // then
        assertThat(responses).isNotNull();
        assertThat(responses.getContent()).hasSize(1);
        assertThat(responses.getContent().get(0).getTitle()).isEqualTo(testRoom.getTitle());
        assertThat(responses.getTotalElements()).isEqualTo(1);
        assertThat(responses.getTotalPages()).isEqualTo(1);

        verify(roomRepository).findRoomsByCondition(any(RoomCondition.class), any(Pageable.class));
    }

    @Test
    @DisplayName("빈 검색 조건으로 방 검색 (페이징)")
    void searchRooms_EmptyConditions() {
        // given
        RoomSearchRequest emptyRequest = new RoomSearchRequest();
        List<Room> rooms = Arrays.asList(testRoom);
        Page<Room> roomPage = new PageImpl<>(rooms, PageRequest.of(0, 20), 1);
        when(roomRepository.findRoomsByCondition(any(RoomCondition.class), any(Pageable.class))).thenReturn(roomPage);

        // when
        PageResponse<RoomResponse> responses = roomService.searchRooms(emptyRequest, PageRequest.of(0, 20));

        // then
        assertThat(responses).isNotNull();
        assertThat(responses.getContent()).hasSize(1);
        assertThat(responses.getTotalElements()).isEqualTo(1);
        verify(roomRepository).findRoomsByCondition(any(RoomCondition.class), any(Pageable.class));
    }

    @Test
    @DisplayName("페이징 파라미터 테스트")
    void testPagingParameters() {
        // given
        List<Room> rooms = Arrays.asList(testRoom);
        Pageable pageable = PageRequest.of(1, 5); // 2페이지, 5개씩
        Page<Room> roomPage = new PageImpl<>(rooms, pageable, 10); // 총 10개
        when(roomRepository.findAll(pageable)).thenReturn(roomPage);

        // when
        PageResponse<RoomResponse> responses = roomService.getAllRooms(pageable);

        // then
        assertThat(responses.getPage()).isEqualTo(2); // 1-based로 1+1=2
        assertThat(responses.getSize()).isEqualTo(5); // 페이지 크기
        assertThat(responses.getTotalElements()).isEqualTo(10); // 전체 요소 수
        assertThat(responses.getTotalPages()).isEqualTo(2); // 전체 페이지 수
        assertThat(responses.isFirst()).isFalse(); // 첫번째 페이지 아님
        assertThat(responses.isLast()).isTrue(); // 마지막 페이지
        assertThat(responses.isHasPrevious()).isTrue(); // 이전 페이지 있음
        assertThat(responses.isHasNext()).isFalse(); // 다음 페이지 없음

        verify(roomRepository).findAll(pageable);
    }

    @Test
    @DisplayName("빈 페이지 결과 테스트")
    void testEmptyPageResult() {
        // given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Room> emptyPage = new PageImpl<>(Arrays.asList(), pageable, 0);
        when(roomRepository.findAll(pageable)).thenReturn(emptyPage);

        // when
        PageResponse<RoomResponse> responses = roomService.getAllRooms(pageable);

        // then
        assertThat(responses.getContent()).isEmpty();
        assertThat(responses.getTotalElements()).isEqualTo(0);
        assertThat(responses.getTotalPages()).isEqualTo(0);
        assertThat(responses.isFirst()).isTrue();
        assertThat(responses.isLast()).isTrue();
        assertThat(responses.isHasPrevious()).isFalse();
        assertThat(responses.isHasNext()).isFalse();

        verify(roomRepository).findAll(pageable);
    }
}
