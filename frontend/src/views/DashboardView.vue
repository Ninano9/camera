<template>
  <div class="dashboard">
    <div class="container">
      <!-- Header -->
      <header class="dashboard-header">
        <div>
          <h1>대시보드</h1>
          <p>안녕하세요, {{ userDisplayName }}님! 👋</p>
        </div>
        <div class="header-actions">
          <router-link to="/camera" class="btn btn-primary">
            <span>📹</span>
            카메라 시작
          </router-link>
          <router-link to="/settings" class="btn btn-outline">
            ⚙️ 설정
          </router-link>
        </div>
      </header>

      <!-- Stats Cards -->
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon">📊</div>
          <div class="stat-content">
            <h3>총 프로파일</h3>
            <p class="stat-number">{{ profileCount }}</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">🎯</div>
          <div class="stat-content">
            <h3>활성 매핑</h3>
            <p class="stat-number">{{ activeMappingCount }}</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">⏱️</div>
          <div class="stat-content">
            <h3>총 사용 시간</h3>
            <p class="stat-number">24h 30m</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">🎮</div>
          <div class="stat-content">
            <h3>제스처 인식률</h3>
            <p class="stat-number">94.2%</p>
          </div>
        </div>
      </div>

      <!-- Quick Actions -->
      <section class="quick-actions">
        <h2>빠른 실행</h2>
        <div class="action-grid">
          <router-link to="/camera" class="action-card">
            <div class="action-icon">📹</div>
            <h3>카메라 시작</h3>
            <p>실시간 제스처 인식 시작</p>
          </router-link>
          <router-link to="/profiles" class="action-card">
            <div class="action-icon">📝</div>
            <h3>프로파일 관리</h3>
            <p>제스처 매핑 설정 관리</p>
          </router-link>
          <router-link to="/settings" class="action-card">
            <div class="action-icon">⚙️</div>
            <h3>설정</h3>
            <p>카메라 및 성능 설정</p>
          </router-link>
          <router-link to="/guide" class="action-card">
            <div class="action-icon">📚</div>
            <h3>사용 가이드</h3>
            <p>제스처 인식 사용법</p>
          </router-link>
        </div>
      </section>

      <!-- Recent Activity -->
      <section class="recent-activity">
        <h2>최근 활동</h2>
        <div class="activity-list">
          <div class="activity-item">
            <div class="activity-icon">✅</div>
            <div class="activity-content">
              <h4>프레젠테이션 프로파일 생성</h4>
              <p>2시간 전</p>
            </div>
          </div>
          <div class="activity-item">
            <div class="activity-icon">🎮</div>
            <div class="activity-content">
              <h4>제스처 인식 세션 완료</h4>
              <p>4시간 전 • 25분간</p>
            </div>
          </div>
          <div class="activity-item">
            <div class="activity-icon">📊</div>
            <div class="activity-content">
              <h4>코딩 프로파일 업데이트</h4>
              <p>1일 전</p>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

// 임시 데이터
const profileCount = ref(3)
const activeMappingCount = ref(12)
const userDisplayName = ref('사용자')

onMounted(() => {
  console.log('대시보드 로드됨')
})
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
  background-color: var(--bg-secondary);
  padding: 2rem 0;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  padding: 2rem;
  background: var(--bg-color);
  border-radius: 1rem;
  box-shadow: var(--shadow);
}

.dashboard-header h1 {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
  color: var(--text-color);
}

.dashboard-header p {
  color: var(--text-secondary);
  font-size: 1rem;
}

.header-actions {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
  margin-bottom: 3rem;
}

.stat-card {
  background: var(--bg-color);
  padding: 1.5rem;
  border-radius: 1rem;
  box-shadow: var(--shadow);
  display: flex;
  align-items: center;
  gap: 1rem;
}

.stat-icon {
  font-size: 2.5rem;
  padding: 0.75rem;
  background: var(--bg-secondary);
  border-radius: 0.5rem;
}

.stat-content h3 {
  font-size: 0.875rem;
  color: var(--text-secondary);
  margin-bottom: 0.25rem;
}

.stat-number {
  font-size: 1.75rem;
  font-weight: 700;
  color: var(--text-color);
}

.quick-actions, .recent-activity {
  margin-bottom: 3rem;
}

.quick-actions h2, .recent-activity h2 {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 1.5rem;
  color: var(--text-color);
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
}

.action-card {
  background: var(--bg-color);
  padding: 2rem;
  border-radius: 1rem;
  box-shadow: var(--shadow);
  text-decoration: none;
  color: inherit;
  transition: transform 0.3s ease;
  text-align: center;
}

.action-card:hover {
  transform: translateY(-3px);
}

.action-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.action-card h3 {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: var(--text-color);
}

.action-card p {
  color: var(--text-secondary);
  font-size: 0.875rem;
}

.activity-list {
  background: var(--bg-color);
  border-radius: 1rem;
  box-shadow: var(--shadow);
  overflow: hidden;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1.5rem;
  border-bottom: 1px solid var(--border-color);
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-icon {
  font-size: 1.5rem;
  padding: 0.5rem;
  background: var(--bg-secondary);
  border-radius: 0.5rem;
}

.activity-content h4 {
  font-size: 1rem;
  font-weight: 500;
  margin-bottom: 0.25rem;
  color: var(--text-color);
}

.activity-content p {
  font-size: 0.875rem;
  color: var(--text-secondary);
}

@media (max-width: 768px) {
  .dashboard {
    padding: 1rem 0;
  }
  
  .dashboard-header {
    flex-direction: column;
    gap: 1rem;
    text-align: center;
    padding: 1.5rem;
  }
  
  .header-actions {
    width: 100%;
    justify-content: center;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .action-grid {
    grid-template-columns: 1fr;
  }
}
</style>
