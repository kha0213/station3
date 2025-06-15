# Station3 과제 부동산 중계 플랫폼

### 1. 애플리케이션 실행
```bash
./gradlew bootRun
```

### 2. 접속 정보
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console
- **서버**: http://localhost:8080

## 테스트 계정

| 이메일 | 비밀번호 | 이름 | 방 개수 |
|--------|---------|------|--------|
| test@test.com | 1q2w3e4r! | 김영롱 | 2개 |
| admin@station3.co.kr | admin123 | 관리자 | 2개 |
| user@test.com | 1234 | 김영롱2 | 3개 |

## 데이터베이스
### H2 Console 접속
- **URL**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: 

## API 엔드포인트
### 인증 API
- `POST /api/auth/signup` - 회원가입
- `POST /api/auth/login` - 로그인

### 방 관리 API (로그인 필요)
- `POST /api/rooms` - 방 등록
- `GET /api/rooms/{id}` - 방 상세 조회
- `PUT /api/rooms/{id}` - 방 수정
- `DELETE /api/rooms/{id}` - 방 삭제
- `GET /api/rooms/my` - 내 방 목록
- `GET /api/rooms` - 전체 방 목록
- `GET /api/rooms/search` - 방 검색

## 인증 방법
1. 로그인으로 JWT 토큰 획득
2. 요청 헤더에 Bearer 토큰 입력:
   ```
   Authorization: Bearer {JWT_TOKEN}
   ```