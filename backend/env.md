# 백엔드 환경변수 설정

Render에서 다음 환경변수들을 설정하세요:

## 필수 환경변수

```
DATABASE_URL=postgresql://username:password@host:port/database
JWT_SECRET=your-secret-key-here
SPRING_PROFILES_ACTIVE=prod
PORT=8080
```

## CORS 설정

```
CORS_ALLOWED_ORIGINS=https://your-frontend-domain.onrender.com
WS_ALLOWED_ORIGINS=https://your-frontend-domain.onrender.com
```

## JWT 설정

```
JWT_ISSUER_URI=https://your-backend-domain.onrender.com
```

## 선택적 환경변수

```
REDIS_HOST=your-redis-host
REDIS_PORT=6379
```

## Render 설정 방법

1. Render 대시보드에서 백엔드 서비스 선택
2. Environment 탭으로 이동
3. "From .env" 버튼 클릭
4. 위의 환경변수들을 입력
