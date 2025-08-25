import axios, { AxiosInstance, AxiosResponse } from 'axios';
import type { 
  User, 
  Profile, 
  Mapping, 
  LoginRequest, 
  LoginResponse, 
  RegisterRequest,
  ApiResponse
} from '../types';

// API 클라이언트 설정
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

class ApiClient {
  private client: AxiosInstance;

  constructor() {
    this.client = axios.create({
      baseURL: API_BASE_URL,
      timeout: 10000,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // 요청 인터셉터 - 토큰 추가
    this.client.interceptors.request.use(
      (config) => {
        const token = localStorage.getItem('accessToken');
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => Promise.reject(error)
    );

    // 응답 인터셉터 - 토큰 만료 처리
    this.client.interceptors.response.use(
      (response) => response,
      async (error) => {
        const originalRequest = error.config;

        if (error.response?.status === 401 && !originalRequest._retry) {
          originalRequest._retry = true;

          try {
            const refreshToken = localStorage.getItem('refreshToken');
            if (refreshToken) {
              const response = await this.refreshToken(refreshToken);
              localStorage.setItem('accessToken', response.data.accessToken);
              localStorage.setItem('refreshToken', response.data.refreshToken);
              
              originalRequest.headers.Authorization = `Bearer ${response.data.accessToken}`;
              return this.client(originalRequest);
            }
          } catch (refreshError) {
            // 리프레시 토큰도 만료된 경우 로그아웃 처리
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
            window.location.href = '/login';
          }
        }

        return Promise.reject(error);
      }
    );
  }

  // 인증 API
  async login(request: LoginRequest): Promise<AxiosResponse<LoginResponse>> {
    return this.client.post('/auth/login', request);
  }

  async register(request: RegisterRequest): Promise<AxiosResponse<User>> {
    return this.client.post('/auth/register', request);
  }

  async refreshToken(refreshToken: string): Promise<AxiosResponse<LoginResponse>> {
    return this.client.post('/auth/refresh', null, {
      headers: { Authorization: `Bearer ${refreshToken}` }
    });
  }

  async logout(): Promise<AxiosResponse<void>> {
    return this.client.post('/auth/logout');
  }

  // 사용자 API
  async getCurrentUser(): Promise<AxiosResponse<User>> {
    return this.client.get('/users/me');
  }

  async updateCurrentUser(data: Partial<User>): Promise<AxiosResponse<User>> {
    return this.client.put('/users/me', data);
  }

  // 프로파일 API
  async getProfiles(): Promise<AxiosResponse<Profile[]>> {
    return this.client.get('/profiles');
  }

  async getProfile(id: string): Promise<AxiosResponse<Profile>> {
    return this.client.get(`/profiles/${id}`);
  }

  async createProfile(data: Omit<Profile, 'id' | 'createdAt' | 'updatedAt'>): Promise<AxiosResponse<Profile>> {
    return this.client.post('/profiles', data);
  }

  async updateProfile(id: string, data: Partial<Profile>): Promise<AxiosResponse<Profile>> {
    return this.client.put(`/profiles/${id}`, data);
  }

  async deleteProfile(id: string): Promise<AxiosResponse<void>> {
    return this.client.delete(`/profiles/${id}`);
  }

  async getDefaultProfile(): Promise<AxiosResponse<Profile>> {
    return this.client.get('/profiles/default');
  }

  // 매핑 API
  async getMappings(profileId: string): Promise<AxiosResponse<Mapping[]>> {
    return this.client.get(`/mappings?profileId=${profileId}`);
  }

  async createMapping(profileId: string, data: Omit<Mapping, 'id' | 'profileId' | 'createdAt' | 'updatedAt'>): Promise<AxiosResponse<Mapping>> {
    return this.client.post(`/profiles/${profileId}/mappings`, data);
  }

  async updateMapping(id: string, data: Partial<Mapping>): Promise<AxiosResponse<Mapping>> {
    return this.client.put(`/mappings/${id}`, data);
  }

  async deleteMapping(id: string): Promise<AxiosResponse<void>> {
    return this.client.delete(`/mappings/${id}`);
  }
}

export const apiClient = new ApiClient();
export default apiClient;
