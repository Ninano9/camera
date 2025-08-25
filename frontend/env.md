# 프론트엔드 환경변수 설정

Render에서 다음 환경변수들을 설정하세요:

## 필수 환경변수

```
VITE_API_BASE_URL=https://your-backend-domain.onrender.com/api
VITE_WS_BASE_URL=wss://your-backend-domain.onrender.com/ws
```

## 선택적 환경변수

```
VITE_APP_NAME=제스처 인식 플랫폼
VITE_APP_VERSION=0.1.0
NODE_VERSION=18
```

## 개발 환경용

```
VITE_API_BASE_URL=http://localhost:8080/api
VITE_WS_BASE_URL=ws://localhost:8080/ws
```

## Render 설정 방법

1. Render 대시보드에서 프론트엔드 서비스 선택
2. Environment 탭으로 이동  
3. "From .env" 버튼 클릭
4. 위의 환경변수들을 입력

## 주의사항

- `VITE_` 접두사가 있는 변수만 클라이언트에서 접근 가능
- 민감한 정보는 프론트엔드 환경변수에 넣지 마세요
