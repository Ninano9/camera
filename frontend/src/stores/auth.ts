import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { apiClient } from '../api';
import type { User, LoginRequest, RegisterRequest, LoginResponse } from '../types';

export const useAuthStore = defineStore('auth', () => {
  // State
  const user = ref<User | null>(null);
  const accessToken = ref<string | null>(localStorage.getItem('accessToken'));
  const refreshToken = ref<string | null>(localStorage.getItem('refreshToken'));
  const isLoading = ref(false);
  const error = ref<string | null>(null);

  // Getters
  const isAuthenticated = computed(() => !!accessToken.value && !!user.value);
  const userDisplayName = computed(() => user.value?.displayName || user.value?.email || '사용자');

  // Actions
  const login = async (credentials: LoginRequest): Promise<void> => {
    isLoading.value = true;
    error.value = null;

    try {
      const response = await apiClient.login(credentials);
      const loginData: LoginResponse = response.data;

      // 토큰 저장
      accessToken.value = loginData.accessToken;
      refreshToken.value = loginData.refreshToken;
      localStorage.setItem('accessToken', loginData.accessToken);
      localStorage.setItem('refreshToken', loginData.refreshToken);

      // 사용자 정보 저장
      user.value = loginData.user;

      console.log('로그인 성공:', user.value);
    } catch (err: any) {
      error.value = err.response?.data?.message || '로그인에 실패했습니다.';
      throw error.value;
    } finally {
      isLoading.value = false;
    }
  };

  const register = async (userData: RegisterRequest): Promise<void> => {
    isLoading.value = true;
    error.value = null;

    try {
      const response = await apiClient.register(userData);
      console.log('회원가입 성공:', response.data);
      
      // 회원가입 후 자동 로그인
      await login({
        email: userData.email,
        password: userData.password
      });
    } catch (err: any) {
      error.value = err.response?.data?.message || '회원가입에 실패했습니다.';
      throw error.value;
    } finally {
      isLoading.value = false;
    }
  };

  const logout = async (): Promise<void> => {
    isLoading.value = true;

    try {
      if (accessToken.value) {
        await apiClient.logout();
      }
    } catch (err) {
      console.error('로그아웃 API 호출 실패:', err);
    } finally {
      // 로컬 상태 초기화
      clearAuth();
      isLoading.value = false;
    }
  };

  const clearAuth = (): void => {
    user.value = null;
    accessToken.value = null;
    refreshToken.value = null;
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
  };

  const getCurrentUser = async (): Promise<void> => {
    if (!accessToken.value) return;

    isLoading.value = true;
    error.value = null;

    try {
      const response = await apiClient.getCurrentUser();
      user.value = response.data;
    } catch (err: any) {
      if (err.response?.status === 401) {
        clearAuth();
      } else {
        error.value = err.response?.data?.message || '사용자 정보 조회에 실패했습니다.';
      }
    } finally {
      isLoading.value = false;
    }
  };

  const updateUser = async (userData: Partial<User>): Promise<void> => {
    isLoading.value = true;
    error.value = null;

    try {
      const response = await apiClient.updateCurrentUser(userData);
      user.value = response.data;
    } catch (err: any) {
      error.value = err.response?.data?.message || '사용자 정보 업데이트에 실패했습니다.';
      throw error.value;
    } finally {
      isLoading.value = false;
    }
  };

  // 초기화 - 페이지 로드 시 토큰이 있으면 사용자 정보 조회
  const initialize = async (): Promise<void> => {
    if (accessToken.value) {
      await getCurrentUser();
    }
  };

  return {
    // State
    user,
    accessToken,
    refreshToken,
    isLoading,
    error,
    
    // Getters
    isAuthenticated,
    userDisplayName,
    
    // Actions
    login,
    register,
    logout,
    clearAuth,
    getCurrentUser,
    updateUser,
    initialize
  };
});
