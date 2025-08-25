# 카메라 제스처/시선 인식 기반 인터랙션 플랫폼

> 사용자의 카메라를 통해 손 제스처와 시선을 인식하여 웹 페이지 상호작용, OS 수준 동작, 앱별 커스텀 매핑을 제공하는 플랫폼

## 🚀 배포 링크 (Render)

- **프론트엔드**: https://gesture-frontend.onrender.com
- **백엔드 API**: https://gesture-backend.onrender.com
- **API 문서**: https://gesture-backend.onrender.com/health

## 프로젝트 구조

```
camera/
├── backend/           # Spring Boot API 서버
├── frontend/          # Vue 3 + Vite + TypeScript 웹 앱
├── companion-app/     # Java 로컬 컴패니언 (키보드/마우스 에뮬레이션)
├── docs/             # 문서 및 가이드
├── render.yaml       # Render 배포 설정
└── README.md
```

## 주요 기능

- 🖐️ **실시간 손 제스처 인식** (MediaPipe Hands, 30 FPS)
- 👁️ **시선 추적 및 눈 깜빡임 감지** (MediaPipe FaceMesh)
- 🎯 **매핑 엔진** (제스처/시선 → 액션 바인딩)
- 📱 **프로파일/프리셋** (코딩, 프레젠테이션, 미디어 등)
- 🔒 **로컬 컴패니언** (안전한 OS 수준 입력 제어)
- 🌐 **웹 기반 UI** (Vue 3, 다크모드, i18n)

## 기술 스택

### Frontend
- **Framework**: Vue 3 (Composition API) + Vite + TypeScript
- **State Management**: Pinia
- **Routing**: Vue Router 4
- **ML Libraries**: MediaPipe Tasks Vision, TensorFlow.js
- **HTTP Client**: Axios
- **Styling**: CSS3 (Custom Properties, Grid, Flexbox)

### Backend
- **Framework**: Spring Boot 3.2
- **Security**: Spring Security + JWT
- **Database**: PostgreSQL + Spring Data JPA
- **Real-time**: WebSocket
- **Cache**: Redis (optional)

### Companion App
- **Language**: Java 21
- **Input Control**: AWT Robot
- **Communication**: WebSocket (mTLS)

### DevOps & Deployment
- **Platform**: Render
- **Database**: PostgreSQL (Managed)
- **Environment**: Production-ready with health checks

## 🛠️ 로컬 개발 환경 설정

### 사전 요구사항
- **Java**: JDK 21+
- **Node.js**: 16+
- **Docker**: Docker Desktop
- **웹캠**: 720p 이상 권장

### 1. 저장소 클론
```bash
git clone <repository-url>
cd camera
```

### 2. 데이터베이스 실행
```bash
docker-compose up -d postgres
```

### 3. 백엔드 실행
```bash
cd backend
./gradlew bootRun
# 또는 Windows에서: gradlew.bat bootRun
```

### 4. 프론트엔드 실행
```bash
cd frontend
npm install
npm run dev
```

### 5. 컴패니언 앱 실행 (선택사항)
```bash
cd companion-app
# 개발 예정
```

## 📦 Render 배포

### 자동 배포
1. GitHub에 코드 push
2. Render에서 자동으로 빌드 및 배포
3. `render.yaml` 설정에 따라 다음 서비스들이 생성됨:
   - PostgreSQL 데이터베이스
   - Spring Boot 백엔드 API
   - Vue.js 프론트엔드 (정적 사이트)

### 환경 변수 설정
Render 대시보드에서 다음 환경 변수들이 자동 설정됨:
- `DATABASE_URL`: PostgreSQL 연결 문자열
- `JWT_SECRET`: JWT 토큰 암호화 키
- `CORS_ALLOWED_ORIGINS`: CORS 허용 도메인
- `VITE_API_BASE_URL`: 프론트엔드 API 기본 URL

## 🔒 보안 및 프라이버시

- 📹 **카메라 영상**: 기본적으로 로컬에서만 처리, 서버 전송 안함
- 🔐 **통신 보안**: HTTPS/WSS, JWT 토큰 기반 인증
- 🛡️ **권한 관리**: 최소 권한 원칙, 명시적 사용자 동의
- 📊 **텔레메트리**: 옵트인 방식, 익명화된 성능 데이터만 수집

## 🎯 사용 사례

### 💻 코딩 보조
- 시선으로 탭 전환
- 제스처로 단축키 실행 (Ctrl+S, Ctrl+P 등)
- 주먹 쥐기로 저장, V자로 검색

### 📊 프레젠테이션 제어
- 좌/우 스와이프로 슬라이드 넘기기
- 핀치 홀드로 레이저 포인터 모드
- 엄지 업으로 포인터 토글

### ♿ 접근성 향상
- 시선 커서로 마우스 대체
- 짧은/긴 깜빡임으로 클릭/더블클릭
- 손 제스처로 키보드 단축키

### 🎵 미디어 제어
- V자 제스처로 재생/일시정지
- 엄지 좌/우로 볼륨 조절
- 손바닥 펼침으로 정지

## 📚 API 문서

### 인증 API
- `POST /auth/login` - 로그인
- `POST /auth/register` - 회원가입
- `POST /auth/refresh` - 토큰 갱신
- `POST /auth/logout` - 로그아웃

### 사용자 API
- `GET /users/me` - 현재 사용자 정보
- `PUT /users/me` - 사용자 정보 업데이트

### 프로파일 API
- `GET /profiles` - 프로파일 목록
- `POST /profiles` - 프로파일 생성
- `GET /profiles/{id}` - 프로파일 상세
- `PUT /profiles/{id}` - 프로파일 업데이트
- `DELETE /profiles/{id}` - 프로파일 삭제

### 헬스체크
- `GET /health` - 서비스 상태 확인
- `GET /health/ready` - 준비 상태 확인
- `GET /health/live` - 생존 상태 확인

## 🔄 개발 로드맵

### ✅ 완료
- [x] 프로젝트 구조 설정
- [x] Spring Boot 백엔드 API
- [x] Vue 3 프론트엔드 기본 구조
- [x] Render 배포 설정
- [x] 인증 시스템 (JWT)
- [x] 프로파일 관리

### 🚧 진행 중
- [ ] MediaPipe 손 제스처 인식
- [ ] 시선 추적 및 깜빡임 감지
- [ ] 제스처 매핑 엔진
- [ ] 기본 프리셋 구현

### 📋 예정
- [ ] Java 컴패니언 앱
- [ ] WebSocket 실시간 통신
- [ ] 텔레메트리 수집
- [ ] 성능 최적화
- [ ] 접근성 기능 강화

## 🤝 기여 방법

1. 이슈 등록 또는 기존 이슈 확인
2. 브랜치 생성 (`git checkout -b feature/새기능`)
3. 변경사항 커밋 (`git commit -m '새 기능 추가'`)
4. 브랜치에 푸시 (`git push origin feature/새기능`)
5. Pull Request 생성

## 📄 라이선스

MIT License - 자세한 내용은 [LICENSE](LICENSE) 파일 참조

## 🆘 지원

- **이슈 리포트**: GitHub Issues
- **문의**: 프로젝트 메인테이너에게 연락
- **문서**: `/docs` 폴더의 상세 가이드 참조

---

**Made with ❤️ for better human-computer interaction** 😊😊