import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '../stores/auth';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'Home',
      component: () => import('../views/HomeView.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('../views/LoginView.vue'),
      meta: { requiresAuth: false, hideForAuth: true }
    },
    {
      path: '/register',
      name: 'Register', 
      component: () => import('../views/RegisterView.vue'),
      meta: { requiresAuth: false, hideForAuth: true }
    },
    {
      path: '/dashboard',
      name: 'Dashboard',
      component: () => import('../views/DashboardView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/camera',
      name: 'Camera',
      component: () => import('../views/CameraView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/profiles',
      name: 'Profiles',
      component: () => import('../views/ProfilesView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/profiles/:id',
      name: 'ProfileDetail',
      component: () => import('../views/ProfileDetailView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/settings',
      name: 'Settings',
      component: () => import('../views/SettingsView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/onboarding',
      name: 'Onboarding',
      component: () => import('../views/OnboardingView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'NotFound',
      component: () => import('../views/NotFoundView.vue'),
      meta: { requiresAuth: false }
    }
  ]
});

// 네비게이션 가드
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore();
  
  // 인증이 필요한 페이지인지 확인
  if (to.meta.requiresAuth) {
    if (!authStore.isAuthenticated) {
      // 토큰이 있으면 사용자 정보 조회 시도
      if (authStore.accessToken) {
        try {
          await authStore.getCurrentUser();
        } catch (error) {
          authStore.clearAuth();
        }
      }
      
      // 여전히 인증되지 않았다면 로그인 페이지로
      if (!authStore.isAuthenticated) {
        return next({ name: 'Login', query: { redirect: to.fullPath } });
      }
    }
  }
  
  // 인증된 사용자가 로그인/회원가입 페이지에 접근하는 경우
  if (to.meta.hideForAuth && authStore.isAuthenticated) {
    return next({ name: 'Dashboard' });
  }
  
  next();
});

export default router;
