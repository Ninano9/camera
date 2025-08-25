// 기본 타입 정의
export interface User {
  id: string;
  email: string;
  displayName?: string;
  isActive: boolean;
  createdAt: string;
  profiles?: ProfileSummary[];
}

export interface ProfileSummary {
  id: string;
  name: string;
  description?: string;
  isDefault: boolean;
  isActive: boolean;
  mappingCount: number;
}

export interface Profile {
  id: string;
  name: string;
  description?: string;
  context: Record<string, any>;
  isDefault: boolean;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
  mappings?: MappingSummary[];
}

export interface MappingSummary {
  id: string;
  name: string;
  condition: Record<string, any>;
  action: Record<string, any>;
  priority: number;
  enabled: boolean;
}

export interface Mapping {
  id: string;
  profileId: string;
  name: string;
  condition: Record<string, any>;
  action: Record<string, any>;
  priority: number;
  enabled: boolean;
  createdAt: string;
  updatedAt: string;
}

// 제스처 관련 타입
export interface HandLandmark {
  x: number;
  y: number;
  z: number;
}

export interface GestureResult {
  categoryName: string;
  score: number;
}

export interface HandGestureData {
  landmarks: HandLandmark[];
  gestures: GestureResult[];
  handedness: string;
}

export interface EyeData {
  leftEye: {
    topLeft: { x: number; y: number };
    topRight: { x: number; y: number };
    bottomLeft: { x: number; y: number };
    bottomRight: { x: number; y: number };
  };
  rightEye: {
    topLeft: { x: number; y: number };
    topRight: { x: number; y: number };
    bottomLeft: { x: number; y: number };
    bottomRight: { x: number; y: number };
  };
  ear: number; // Eye Aspect Ratio
  isBlinking: boolean;
}

export interface GazeData {
  x: number;
  y: number;
  confidence: number;
}

// 인증 관련 타입
export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  user: User;
}

export interface RegisterRequest {
  email: string;
  password: string;
  displayName?: string;
}

// API 응답 타입
export interface ApiResponse<T> {
  data?: T;
  message?: string;
  error?: string;
}

// 설정 관련 타입
export interface CameraSettings {
  deviceId?: string;
  width: number;
  height: number;
  fps: number;
}

export interface PerformanceSettings {
  maxFps: number;
  powerMode: 'low' | 'normal' | 'high';
  enableGPU: boolean;
}

export interface AppSettings {
  camera: CameraSettings;
  performance: PerformanceSettings;
  theme: 'light' | 'dark' | 'auto';
  language: 'ko' | 'en';
  telemetryEnabled: boolean;
}
