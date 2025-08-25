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
            <h3>프로파일 설정</h3>
            <p>용도에 맞는 제스처 매핑을 선택하거나 커스터마이징하세요</p>
          </div>
        </div>
      </section>

      <!-- 기본 제스처 -->
      <section class="guide-section">
        <h2>✋ 기본 제스처</h2>
        <div class="gestures-grid">
          <div class="gesture-card">
            <div class="gesture-icon">👆</div>
            <h3>검지 포인팅</h3>
            <p>클릭 동작</p>
          </div>
          <div class="gesture-card">
            <div class="gesture-icon">🤏</div>
            <h3>핀치</h3>
            <p>선택/드래그</p>
          </div>
          <div class="gesture-card">
            <div class="gesture-icon">✌️</div>
            <h3>V 제스처</h3>
            <p>재생/일시정지</p>
          </div>
          <div class="gesture-card">
            <div class="gesture-icon">✋</div>
            <h3>손바닥 펼치기</h3>
            <p>정지/취소</p>
          </div>
          <div class="gesture-card">
            <div class="gesture-icon">👊</div>
            <h3>주먹 쥐기</h3>
            <p>저장 (Ctrl+S)</p>
          </div>
          <div class="gesture-card">
            <div class="gesture-icon">👍</div>
            <h3>엄지 업</h3>
            <p>확인/승인</p>
          </div>
        </div>
      </section>

      <!-- 시선 제어 -->
      <section class="guide-section">
        <h2>👁️ 시선 제어</h2>
        <div class="eye-control-grid">
          <div class="control-card">
            <h3>시선 커서</h3>
            <p>눈동자 움직임으로 마우스 커서를 조작할 수 있습니다</p>
          </div>
          <div class="control-card">
            <h3>짧은 깜빡임</h3>
            <p>클릭 동작을 수행합니다</p>
          </div>
          <div class="control-card">
            <h3>긴 깜빡임</h3>
            <p>우클릭 또는 컨텍스트 메뉴를 실행합니다</p>
          </div>
        </div>
      </section>

      <!-- 프로파일 소개 -->
      <section class="guide-section">
        <h2>📝 프로파일 종류</h2>
        <div class="profiles-grid">
          <div class="profile-card">
            <div class="profile-icon">💻</div>
            <h3>코딩 프로파일</h3>
            <p>IDE와 개발 도구에 최적화된 제스처 설정</p>
            <ul>
              <li>주먹 쥐기: Ctrl+S (저장)</li>
              <li>V 제스처: Ctrl+P (명령 팔레트)</li>
              <li>시선 좌/우: 탭 전환</li>
            </ul>
          </div>
          <div class="profile-card">
            <div class="profile-icon">📊</div>
            <h3>프레젠테이션 프로파일</h3>
            <p>발표와 슬라이드 제어에 특화된 설정</p>
            <ul>
              <li>좌/우 스와이프: 슬라이드 이동</li>
              <li>핀치 홀드: 레이저 포인터</li>
              <li>엄지 업: 포인터 토글</li>
            </ul>
          </div>
          <div class="profile-card">
            <div class="profile-icon">🎵</div>
            <h3>미디어 프로파일</h3>
            <p>음악과 동영상 재생 제어</p>
            <ul>
              <li>V 제스처: 재생/일시정지</li>
              <li>엄지 좌/우: 볼륨 조절</li>
              <li>손바닥: 정지</li>
            </ul>
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
            <router-link to="/profiles" class="btn btn-outline btn-lg">
              📝 프로파일 설정
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

.profiles-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
}

.profile-card {
  background: var(--bg-color);
  padding: 2rem;
  border-radius: 1rem;
  box-shadow: var(--shadow);
}

.profile-icon {
  font-size: 2.5rem;
  margin-bottom: 1rem;
}

.profile-card h3 {
  font-size: 1.25rem;
  margin-bottom: 1rem;
  color: var(--text-color);
}

.profile-card p {
  color: var(--text-secondary);
  margin-bottom: 1rem;
}

.profile-card ul {
  list-style: none;
  padding: 0;
}

.profile-card li {
  color: var(--text-secondary);
  margin-bottom: 0.5rem;
  padding-left: 1rem;
  position: relative;
}

.profile-card li::before {
  content: "•";
  color: var(--primary-color);
  position: absolute;
  left: 0;
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
