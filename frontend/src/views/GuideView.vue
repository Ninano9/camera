<template>
  <div class="guide-view">
    <div class="container">
      <!-- 헤더 -->
      <header class="guide-header">
        <h1>🎯 제스처 인식 사용 가이드</h1>
        <p>카메라를 통한 손 제스처와 시선 인식 사용법을 안내합니다</p>
      </header>

      <!-- 시작하기 -->
      <section class="guide-section">
        <h2>🚀 빠른 시작</h2>
        <div class="steps-grid">
          <div class="step">
            <div class="step-number">1</div>
            <h3>카메라 권한 허용</h3>
            <p>브라우저에서 카메라 접근을 허용해주세요</p>
          </div>
          <div class="step">
            <div class="step-number">2</div>
            <h3>제스처 인식 시작</h3>
            <p>카메라 페이지에서 제스처 인식을 활성화하세요</p>
          </div>
          <div class="step">
            <div class="step-number">3</div>
            <h3>손가락 제스처 시작</h3>
            <p>손가락 움직임으로 마우스를 제어해보세요</p>
          </div>
        </div>
      </section>

      <!-- 기본 제스처 -->
      <section class="guide-section">
        <h2>✋ 손가락 제스처</h2>
        <div class="gestures-grid">
          <div class="gesture-card">
            <div class="gesture-icon">👆</div>
            <h3>검지 하나</h3>
            <p>마우스 포인터 이동 + 까딱으로 좌클릭</p>
          </div>
          <div class="gesture-card">
            <div class="gesture-icon">🖕</div>
            <h3>중지 하나</h3>
            <p>까딱으로 우클릭</p>
          </div>
          <div class="gesture-card">
            <div class="gesture-icon">✌️</div>
            <h3>검지 + 중지</h3>
            <p>위아래로 움직여 스크롤</p>
          </div>
        </div>
      </section>

      <!-- 상세 사용법 -->
      <section class="guide-section">
        <h2>📖 상세 사용법</h2>
        <div class="usage-grid">
          <div class="usage-card">
            <h3>👆 마우스 포인터 + 좌클릭</h3>
            <p><strong>검지만 펼치기:</strong> 손을 움직이면 마우스 커서가 따라 움직입니다</p>
            <p><strong>검지 까딱:</strong> 검지를 아래로 살짝 움직였다가 다시 올리면 좌클릭됩니다</p>
          </div>
          <div class="usage-card">
            <h3>🖕 우클릭</h3>
            <p><strong>중지만 펼치기:</strong> 중지 하나만 세우고 까딱하면 우클릭됩니다</p>
          </div>
          <div class="usage-card">
            <h3>✌️ 스크롤</h3>
            <p><strong>검지+중지 펼치기:</strong> 두 손가락을 동시에 위아래로 움직이면 스크롤됩니다</p>
            <p>위로 올리면 위로 스크롤, 아래로 내리면 아래로 스크롤</p>
          </div>
        </div>
      </section>

      <!-- 팁과 주의사항 -->
      <section class="guide-section">
        <h2>💡 사용 팁</h2>
        <div class="tips-grid">
          <div class="tip-card">
            <h3>🔆 조명</h3>
            <p>밝고 균일한 조명에서 인식률이 높아집니다</p>
          </div>
          <div class="tip-card">
            <h3>📷 카메라 위치</h3>
            <p>카메라와 30-60cm 거리를 유지하세요</p>
          </div>
          <div class="tip-card">
            <h3>✋ 손 위치</h3>
            <p>손을 카메라 화면 중앙에 위치시키세요</p>
          </div>
          <div class="tip-card">
            <h3>⏱️ 제스처 속도</h3>
            <p>천천히 명확한 제스처를 취하세요</p>
          </div>
        </div>
      </section>

      <!-- 시작하기 버튼 -->
      <section class="guide-cta">
        <div class="cta-content">
          <h2>이제 시작해보세요!</h2>
          <div class="cta-buttons">
            <router-link to="/camera" class="btn btn-primary btn-lg">
              🎥 제스처 인식 시작
            </router-link>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'

onMounted(() => {
  console.log('가이드 페이지 로드됨')
})
</script>

<style scoped>
.guide-view {
  min-height: 100vh;
  background-color: var(--bg-secondary);
  padding: 2rem 0;
}

.guide-header {
  text-align: center;
  margin-bottom: 4rem;
  padding: 3rem 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 1rem;
  margin-bottom: 3rem;
}

.guide-header h1 {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 1rem;
}

.guide-header p {
  font-size: 1.25rem;
  opacity: 0.9;
}

.guide-section {
  margin-bottom: 4rem;
}

.guide-section h2 {
  font-size: 2rem;
  font-weight: 600;
  margin-bottom: 2rem;
  color: var(--text-color);
}

.steps-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
}

.step {
  background: var(--bg-color);
  padding: 2rem;
  border-radius: 1rem;
  text-align: center;
  box-shadow: var(--shadow);
}

.step-number {
  width: 60px;
  height: 60px;
  background: var(--primary-color);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  font-weight: 700;
  margin: 0 auto 1.5rem;
}

.step h3 {
  font-size: 1.25rem;
  margin-bottom: 1rem;
  color: var(--text-color);
}

.step p {
  color: var(--text-secondary);
}

.gestures-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
}

.gesture-card {
  background: var(--bg-color);
  padding: 1.5rem;
  border-radius: 1rem;
  text-align: center;
  box-shadow: var(--shadow);
  transition: transform 0.3s ease;
}

.gesture-card:hover {
  transform: translateY(-5px);
}

.gesture-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.gesture-card h3 {
  font-size: 1.1rem;
  margin-bottom: 0.5rem;
  color: var(--text-color);
}

.gesture-card p {
  color: var(--text-secondary);
  font-size: 0.9rem;
}

.eye-control-grid,
.tips-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
}

.control-card,
.tip-card {
  background: var(--bg-color);
  padding: 1.5rem;
  border-radius: 1rem;
  box-shadow: var(--shadow);
}

.control-card h3,
.tip-card h3 {
  margin-bottom: 0.75rem;
  color: var(--text-color);
}

.control-card p,
.tip-card p {
  color: var(--text-secondary);
  line-height: 1.6;
}

.usage-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
}

.usage-card {
  background: var(--bg-color);
  padding: 2rem;
  border-radius: 1rem;
  box-shadow: var(--shadow);
}

.usage-card h3 {
  font-size: 1.25rem;
  margin-bottom: 1rem;
  color: var(--text-color);
}

.usage-card p {
  color: var(--text-secondary);
  margin-bottom: 0.75rem;
  line-height: 1.6;
}

.usage-card strong {
  color: var(--primary-color);
  font-weight: 600;
}

.guide-cta {
  text-align: center;
  padding: 3rem;
  background: var(--bg-color);
  border-radius: 1rem;
  box-shadow: var(--shadow);
}

.cta-content h2 {
  font-size: 2rem;
  margin-bottom: 2rem;
  color: var(--text-color);
}

.cta-buttons {
  display: flex;
  gap: 1rem;
  justify-content: center;
}

.btn-lg {
  padding: 0.875rem 2rem;
  font-size: 1rem;
}

@media (max-width: 768px) {
  .guide-header h1 {
    font-size: 2rem;
  }
  
  .guide-header p {
    font-size: 1rem;
  }
  
  .steps-grid,
  .gestures-grid,
  .profiles-grid {
    grid-template-columns: 1fr;
  }
  
  .cta-buttons {
    flex-direction: column;
    align-items: center;
  }
}
</style>
