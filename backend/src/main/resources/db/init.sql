-- 데이터베이스 초기화 스크립트
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- users 테이블
CREATE TABLE IF NOT EXISTS app_user (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- profiles 테이블 (컨텍스트/앱별)
CREATE TABLE IF NOT EXISTS profile (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    context JSONB DEFAULT '{}'::jsonb,
    is_default BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- mappings 테이블 (제스처/시선 → 액션)
CREATE TABLE IF NOT EXISTS mapping (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    profile_id UUID NOT NULL REFERENCES profile(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    condition JSONB NOT NULL,  -- { gesture:"pinch", when:"presentation", ... }
    action JSONB NOT NULL,     -- { type:"keystroke", value:"CTRL+S" }
    priority INTEGER DEFAULT 100,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- telemetry 테이블 (옵트인)
CREATE TABLE IF NOT EXISTS telemetry (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID REFERENCES app_user(id) ON DELETE SET NULL,
    session_id VARCHAR(255),
    event_type VARCHAR(50) NOT NULL,
    payload JSONB,
    timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 인덱스 생성
CREATE INDEX IF NOT EXISTS idx_profile_user_id ON profile(user_id);
CREATE INDEX IF NOT EXISTS idx_mapping_profile_id ON mapping(profile_id);
CREATE INDEX IF NOT EXISTS idx_telemetry_user_id ON telemetry(user_id);
CREATE INDEX IF NOT EXISTS idx_telemetry_timestamp ON telemetry(timestamp);
CREATE INDEX IF NOT EXISTS idx_telemetry_event_type ON telemetry(event_type);

-- 기본 데이터 삽입
INSERT INTO app_user (email, password_hash, display_name) 
VALUES ('admin@gesture.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFDYZZ4NjVFNdv.q4hd.Dty', 'Admin User')
ON CONFLICT (email) DO NOTHING;

-- 기본 프로파일 생성
INSERT INTO profile (user_id, name, description, context, is_default)
SELECT 
    id,
    'Default Profile',
    '기본 제스처 프로파일',
    '{"type": "general", "app": "*"}'::jsonb,
    true
FROM app_user 
WHERE email = 'admin@gesture.com'
ON CONFLICT DO NOTHING;
