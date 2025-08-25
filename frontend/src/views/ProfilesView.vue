<template>
  <div class="profiles-view">
    <div class="container">
      <div class="profiles-header">
        <h1>프로파일 관리</h1>
        <button class="btn btn-primary">
          <span>+</span>
          새 프로파일
        </button>
      </div>
      
      <div class="profiles-grid">
        <div v-for="profile in mockProfiles" :key="profile.id" class="profile-card">
          <div class="profile-header">
            <h3>{{ profile.name }}</h3>
            <span :class="['status', profile.isDefault ? 'default' : 'normal']">
              {{ profile.isDefault ? '기본' : '일반' }}
            </span>
          </div>
          <p class="profile-description">{{ profile.description }}</p>
          <div class="profile-stats">
            <span>매핑 {{ profile.mappingCount }}개</span>
            <span>{{ profile.isActive ? '활성' : '비활성' }}</span>
          </div>
          <div class="profile-actions">
            <button class="btn btn-outline">편집</button>
            <button class="btn btn-outline">복사</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const mockProfiles = ref([
  {
    id: '1',
    name: '기본 프로파일',
    description: '일반적인 웹 브라우징을 위한 기본 제스처 설정',
    isDefault: true,
    isActive: true,
    mappingCount: 8
  },
  {
    id: '2',
    name: '코딩 프로파일',
    description: 'IDE와 개발 도구를 위한 전문 제스처 설정',
    isDefault: false,
    isActive: true,
    mappingCount: 15
  },
  {
    id: '3',
    name: '프레젠테이션',
    description: '발표와 슬라이드 제어를 위한 제스처 설정',
    isDefault: false,
    isActive: false,
    mappingCount: 6
  }
])
</script>

<style scoped>
.profiles-view {
  min-height: 100vh;
  background-color: var(--bg-secondary);
  padding: 2rem 0;
}

.profiles-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.profiles-header h1 {
  color: var(--text-color);
  font-size: 2rem;
}

.profiles-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

.profile-card {
  background: var(--bg-color);
  border-radius: 1rem;
  padding: 1.5rem;
  box-shadow: var(--shadow);
  transition: transform 0.2s ease;
}

.profile-card:hover {
  transform: translateY(-2px);
}

.profile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.profile-header h3 {
  color: var(--text-color);
  font-size: 1.25rem;
  font-weight: 600;
}

.status {
  padding: 0.25rem 0.75rem;
  border-radius: 1rem;
  font-size: 0.75rem;
  font-weight: 500;
}

.status.default {
  background: var(--primary-color);
  color: white;
}

.status.normal {
  background: var(--bg-secondary);
  color: var(--text-secondary);
}

.profile-description {
  color: var(--text-secondary);
  margin-bottom: 1.5rem;
  line-height: 1.5;
}

.profile-stats {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.5rem;
  font-size: 0.875rem;
  color: var(--text-secondary);
}

.profile-actions {
  display: flex;
  gap: 0.75rem;
}

.profile-actions .btn {
  flex: 1;
  padding: 0.5rem;
  font-size: 0.875rem;
}

@media (max-width: 768px) {
  .profiles-header {
    flex-direction: column;
    gap: 1rem;
    text-align: center;
  }
  
  .profiles-grid {
    grid-template-columns: 1fr;
  }
}
</style>
