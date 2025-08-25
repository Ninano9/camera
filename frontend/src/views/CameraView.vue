<template>
  <div class="camera-view">
    <div class="container">
      <h1>카메라 제스처 인식</h1>
      <p>개발 예정입니다.</p>
      
      <div class="placeholder">
        <div class="placeholder-icon">📹</div>
        <h2>카메라 모듈</h2>
        <p>MediaPipe 손 제스처 인식이 여기에 구현될 예정입니다.</p>
        
        <div class="feature-list">
          <div class="feature">✋ 손 제스처 인식</div>
          <div class="feature">👁️ 시선 추적</div>
          <div class="feature">🎯 실시간 매핑</div>
        </div>
        
        <router-link to="/dashboard" class="btn btn-primary">
          대시보드로 돌아가기
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'

onMounted(() => {
  console.log('카메라 뷰 로드됨')
})
</script>

<style scoped>
.camera-view {
  min-height: 100vh;
  background-color: var(--bg-secondary);
  padding: 2rem 0;
}

.camera-view h1 {
  text-align: center;
  margin-bottom: 1rem;
  color: var(--text-color);
}

.camera-view > .container > p {
  text-align: center;
  color: var(--text-secondary);
  margin-bottom: 3rem;
}

.placeholder {
  background: var(--bg-color);
  border-radius: 1rem;
  padding: 4rem 2rem;
  text-align: center;
  box-shadow: var(--shadow);
}

.placeholder-icon {
  font-size: 4rem;
  margin-bottom: 1.5rem;
}

.placeholder h2 {
  font-size: 2rem;
  margin-bottom: 1rem;
  color: var(--text-color);
}

.placeholder p {
  color: var(--text-secondary);
  margin-bottom: 2rem;
  font-size: 1.1rem;
}

.feature-list {
  display: flex;
  justify-content: center;
  gap: 2rem;
  margin-bottom: 2rem;
  flex-wrap: wrap;
}

.feature {
  background: var(--bg-secondary);
  padding: 0.75rem 1.5rem;
  border-radius: 0.5rem;
  font-weight: 500;
  color: var(--text-color);
}

@media (max-width: 768px) {
  .feature-list {
    flex-direction: column;
    align-items: center;
    gap: 1rem;
  }
}
</style>
