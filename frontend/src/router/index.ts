import { createRouter, createWebHistory } from 'vue-router';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'Home',
      component: () => import('../views/HomeView.vue')
    },
    {
      path: '/camera',
      name: 'Camera',
      component: () => import('../views/CameraView.vue')
    },
    {
      path: '/profiles',
      name: 'Profiles',
      component: () => import('../views/ProfilesView.vue')
    },
    {
      path: '/profiles/:id',
      name: 'ProfileDetail',
      component: () => import('../views/ProfileDetailView.vue')
    },
    {
      path: '/settings',
      name: 'Settings',
      component: () => import('../views/SettingsView.vue')
    },
    {
      path: '/guide',
      name: 'Guide',
      component: () => import('../views/GuideView.vue')
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'NotFound',
      component: () => import('../views/NotFoundView.vue')
    }
  ]
});

// 모든 페이지에 자유롭게 접근 가능 (인증 없음)

export default router;
