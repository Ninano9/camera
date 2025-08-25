<template>
  <div class="camera-view">
    <div class="container">
      <h1>🎥 제스처 인식</h1>
      <p>카메라를 통한 실시간 손 제스처 및 시선 인식</p>
      
      <div class="camera-container">
        <div class="camera-placeholder">
          <div class="camera-icon">📹</div>
          <h2>카메라 준비</h2>
          <p>브라우저에서 카메라 권한을 허용하고 제스처 인식을 시작하세요.</p>
          
          <div class="feature-list">
            <div class="feature active">✋ 손 제스처 인식</div>
            <div class="feature active">👁️ 시선 추적</div>
            <div class="feature active">🎯 실시간 매핑</div>
          </div>
          
          <div class="camera-actions">
            <button class="btn btn-primary btn-lg">
              📹 카메라 시작
            </button>
            <router-link to="/guide" class="btn btn-outline">
              📚 사용 가이드
            </router-link>
          </div>
        </div>
      </div>
      
      <!-- 제스처 상태 표시 -->
      <div class="status-panel">
        <div class="status-item">
          <span class="status-label">카메라 상태:</span>
          <span class="status-value pending">대기 중</span>
        </div>
        <div class="status-item">
          <span class="status-label">제스처 인식:</span>
          <span class="status-value pending">비활성</span>
        </div>
        <div class="status-item">
          <span class="status-label">시선 추적:</span>
          <span class="status-value pending">비활성</span>
        </div>
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

.camera-container {
  margin-bottom: 3rem;
}

.camera-placeholder {
  background: var(--bg-color);
  border-radius: 1rem;
  padding: 4rem 2rem;
  text-align: center;
  box-shadow: var(--shadow);
}

.camera-icon {
  font-size: 4rem;
  margin-bottom: 1.5rem;
}

.camera-placeholder h2 {
  font-size: 2rem;
  margin-bottom: 1rem;
  color: var(--text-color);
}

.camera-placeholder p {
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
  border: 2px solid transparent;
}

.feature.active {
  background: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}

.camera-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}

.btn-lg {
  padding: 0.875rem 2rem;
  font-size: 1rem;
}

.status-panel {
  background: var(--bg-color);
  border-radius: 1rem;
  padding: 2rem;
  box-shadow: var(--shadow);
}

.status-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  padding: 0.75rem 0;
  border-bottom: 1px solid var(--border-color);
}

.status-item:last-child {
  margin-bottom: 0;
  border-bottom: none;
}

.status-label {
  font-weight: 500;
  color: var(--text-color);
}

.status-value {
  padding: 0.25rem 0.75rem;
  border-radius: 0.375rem;
  font-size: 0.875rem;
  font-weight: 500;
}

.status-value.pending {
  background: var(--bg-secondary);
  color: var(--text-secondary);
}

.status-value.active {
  background: var(--success-color);
  color: white;
}

.status-value.error {
  background: var(--error-color);
  color: white;
}

@media (max-width: 768px) {
  .feature-list {
    flex-direction: column;
    align-items: center;
    gap: 1rem;
  }
  
  .camera-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .status-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }
}
</style>
