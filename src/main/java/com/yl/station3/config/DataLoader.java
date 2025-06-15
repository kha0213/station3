package com.yl.station3.config;

import com.yl.station3.domain.room.DealType;
import com.yl.station3.domain.room.Room;
import com.yl.station3.domain.room.RoomDeal;
import com.yl.station3.domain.room.RoomType;
import com.yl.station3.domain.user.User;
import com.yl.station3.repository.RoomRepository;
import com.yl.station3.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        if (userRepository.count() > 0) {
            log.info("테스트 데이터가 이미 존재합니다. 데이터 생성을 건너뜁니다.");
            return;
        }

        log.info("=== 테스트 데이터 생성 시작 ===");
        
        // 테스트 사용자들 생성
        User user1 = createTestUser("test@example.com", "password123", "김영롱");
        User user2 = createTestUser("admin@station3.co.kr", "admin123", "관리자");
        User user3 = createTestUser("user@test.com", "user123", "김영롱2");

        // 테스트 방들 생성
        createTestRooms(user1, user2, user3);

        log.info("=== 테스트 데이터 생성 완료 ===");
    }

    private User createTestUser(String email, String password, String name) {
        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .name(name)
                .build();
        
        User savedUser = userRepository.save(user);
        log.info("테스트 사용자 생성: {} ({})", name, email);
        return savedUser;
    }

    private void createTestRooms(User user1, User user2, User user3) {
        createRoom(user1, "깔끔한 원룸",
                "신축 건물의 원룸입니다.",
                "서울시 송파구 가락동 189-6",
                RoomType.ONE_ROOM,
                new RoomDealData[]{
                    new RoomDealData(DealType.MONTHLY_RENT, "1000", "50"),
                    new RoomDealData(DealType.JEONSE, "5000", null)
                });

        createRoom(user1, "넓은 투룸",
                "깨끗한 투룸입니다.",
                "서울시 서초구 서초동 111-1",
                RoomType.TWO_ROOM,
                new RoomDealData[]{
                    new RoomDealData(DealType.MONTHLY_RENT, "2000", "80"),
                    new RoomDealData(DealType.MONTHLY_RENT, "3000", "60"),
                    new RoomDealData(DealType.JEONSE, "8000", null)
                });

        // 관리자의 방들
        createRoom(user2, "큰 쓰리룸",
                "쓰리룸입니다.",
                "서울시 송파구 문정동 112-12",
                RoomType.THREE_ROOM,
                new RoomDealData[]{
                    new RoomDealData(DealType.JEONSE, "15000", null)
                });

        createRoom(user2, "원룸2",
                "원룸 2",
                "서울시 중구 중동 155-1",
                RoomType.ONE_ROOM,
                new RoomDealData[]{
                    new RoomDealData(DealType.MONTHLY_RENT, "500", "70"),
                    new RoomDealData(DealType.MONTHLY_RENT, "800", "60")
                });

        // 이영희의 방들
        createRoom(user3, "투룸2",
                "투룸입니다.",
                "서울시 댕댕구 댕댕동 23-1",
                RoomType.TWO_ROOM,
                new RoomDealData[]{
                    new RoomDealData(DealType.MONTHLY_RENT, "1500", "65"),
                    new RoomDealData(DealType.JEONSE, "7000", null)
                });

        createRoom(user3, "원룸3",
                "원룸입니다.",
                "서울시 댕댕구 댕동 1-2",
                RoomType.ONE_ROOM,
                new RoomDealData[]{
                    new RoomDealData(DealType.MONTHLY_RENT, "1200", "55")
                });

        createRoom(user3, "쓰리룸2",
                "",
                "인천시 중구 중동 1-2",
                RoomType.THREE_ROOM,
                new RoomDealData[]{
                    new RoomDealData(DealType.MONTHLY_RENT, "5000", "200"),
                    new RoomDealData(DealType.JEONSE, "25000", null)
                });
    }

    private void createRoom(User user, String title, String description, String address, 
                          RoomType roomType, RoomDealData[] dealData) {
        Room room = Room.builder()
                .title(title)
                .description(description)
                .address(address)
                .roomType(roomType)
                .user(user)
                .build();

        // 거래 정보 추가
        for (RoomDealData data : dealData) {
            RoomDeal roomDeal = RoomDeal.builder()
                    .dealType(data.dealType)
                    .deposit(new BigDecimal(data.deposit))
                    .monthlyRent(data.monthlyRent != null ? new BigDecimal(data.monthlyRent) : null)
                    .build();
            room.addRoomDeal(roomDeal);
        }

        roomRepository.save(room);
        log.info("테스트 방 생성: {} ({})", title, user.getName());
    }

    private static class RoomDealData {
        DealType dealType;
        String deposit;
        String monthlyRent;

        RoomDealData(DealType dealType, String deposit, String monthlyRent) {
            this.dealType = dealType;
            this.deposit = deposit;
            this.monthlyRent = monthlyRent;
        }
    }
}
