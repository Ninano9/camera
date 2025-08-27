<template>
  <div class="home">
    <!-- Hero Section -->
    <section class="hero">
      <div class="container">
        <div class="hero-content">
          <h1 class="hero-title">
            🖐️ 제스처 인식 플랫폼
          </h1>
          <p class="hero-description">
            카메라를 통해 손 제스처와 시선을 인식하여<br>
            웹 페이지 상호작용, OS 수준 동작, 앱별 커스텀 매핑을 제공합니다
          </p>
          <div class="hero-buttons">
            <router-link to="/camera" class="btn btn-primary btn-lg">
              🎥 지금 시작하기
            </router-link>
            <router-link to="/guide" class="btn btn-outline btn-lg">
              📚 사용 가이드
            </router-link>
          </div>
        </div>
        <div class="hero-video">
          <div class="preview-placeholder">
            <div class="preview-icon">📹</div>
            <p>실시간 제스처 인식</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section class="features">
      <div class="container">
        <h2 class="section-title">주요 기능</h2>
        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-icon">🖐️</div>
            <h3>실시간 손 제스처 인식</h3>
            <p>MediaPipe를 활용한 30 FPS 고성능 손 제스처 인식</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">👁️</div>
            <h3>시선 추적 및 깜빡임 감지</h3>
            <p>정밀한 시선 추적과 눈 깜빡임을 통한 직관적 제어</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">🎯</div>
            <h3>스마트 매핑 엔진</h3>
            <p>제스처와 시선을 다양한 액션으로 자유롭게 바인딩</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">⚡</div>
            <h3>실시간 손가락 제어</h3>
            <p>검지로 마우스 이동, 까딱으로 클릭, 두 손가락으로 스크롤</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">🔒</div>
            <h3>안전한 로컬 처리</h3>
            <p>카메라 영상은 로컬에서만 처리되어 프라이버시 보장</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">🌐</div>
            <h3>웹 기반 설정</h3>
            <p>직관적인 웹 인터페이스로 쉬운 설정 및 관리</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Use Cases Section -->
    <section class="use-cases">
      <div class="container">
        <h2 class="section-title">활용 사례</h2>
        <div class="use-cases-grid">
          <div class="use-case">
            <h3>💻 코딩 보조</h3>
            <p>시선으로 탭 전환, 제스처로 단축키 실행하여 코딩 효율성 극대화</p>
          </div>
          <div class="use-case">
            <h3>📊 프레젠테이션 제어</h3>
            <p>손동작으로 슬라이드 넘기기, 포인터 조작하여 자연스러운 발표</p>
          </div>
          <div class="use-case">
            <h3>♿ 접근성 향상</h3>
            <p>시선 커서와 눈 깜빡임으로 마우스/키보드 없이도 컴퓨터 조작</p>
          </div>
          <div class="use-case">
            <h3>🎵 미디어 제어</h3>
            <p>제스처로 재생/일시정지, 볼륨 조절하여 핸즈프리 미디어 경험</p>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA Section -->
    <section class="cta">
      <div class="container">
        <div class="cta-content">
          <h2>지금 시작해보세요</h2>
          <p>몇 분만에 설정하고 새로운 인터랙션 경험을 만나보세요</p>
          <div class="cta-buttons">
            <router-link to="/camera" class="btn btn-primary btn-lg">
              🎥 바로 시작하기
            </router-link>
            <!-- <router-link to="/profiles" class="btn btn-outline btn-lg"> -->
              <!-- 📝 프로파일 관리 -->
            <!-- </router-link> -->
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'

onMounted(() => {
  console.log('홈 페이지 로드됨')
})
</script>

<style scoped>
.home {
  min-height: 100vh;
}

/* Hero Section */
.hero {
  padding: 4rem 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-align: center;
}

.hero .container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 4rem;
  align-items: center;
  max-width: 1200px;
}

.hero-title {
  font-size: 3.5rem;
  font-weight: 700;
  margin-bottom: 1.5rem;
  line-height: 1.2;
}

.hero-description {
  font-size: 1.25rem;
  margin-bottom: 2rem;
  opacity: 0.9;
  line-height: 1.6;
}

.hero-buttons {
  display: flex;
  gap: 1rem;
  justify-content: center;
}

.btn-lg {
  padding: 0.875rem 2rem;
  font-size: 1rem;
}

.hero-video {
  display: flex;
  justify-content: center;
  align-items: center;
}

.preview-placeholder {
  width: 400px;
  height: 300px;
  background: rgba(255, 255, 255, 0.1);
  border: 2px dashed rgba(255, 255, 255, 0.3);
  border-radius: 1rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
}

.preview-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

/* Features Section */
.features {
  padding: 4rem 0;
  background-color: var(--bg-secondary);
}

.section-title {
  text-align: center;
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 3rem;
  color: var(--text-color);
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
}

.feature-card {
  background: var(--bg-color);
  padding: 2rem;
  border-radius: 1rem;
  text-align: center;
  box-shadow: var(--shadow);
  transition: transform 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.feature-card h3 {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 1rem;
  color: var(--text-color);
}

.feature-card p {
  color: var(--text-secondary);
  line-height: 1.6;
}

/* Use Cases Section */
.use-cases {
  padding: 4rem 0;
}

.use-cases-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
}

.use-case {
  text-align: center;
  padding: 1.5rem;
}

.use-case h3 {
  font-size: 1.5rem;
  margin-bottom: 1rem;
  color: var(--text-color);
}

.use-case p {
  color: var(--text-secondary);
  line-height: 1.6;
}

/* CTA Section */
.cta {
  padding: 4rem 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-align: center;
}

.cta-content h2 {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 1rem;
}

.cta-content p {
  font-size: 1.25rem;
  margin-bottom: 2rem;
  opacity: 0.9;
}

.cta-buttons {
  display: flex;
  gap: 1rem;
  justify-content: center;
}

/* 반응형 */
@media (max-width: 768px) {
  .hero .container {
    grid-template-columns: 1fr;
    text-align: center;
  }
  
  .hero-title {
    font-size: 2.5rem;
  }
  
  .hero-buttons {
    flex-direction: column;
    align-items: center;
  }
  
  .preview-placeholder {
    width: 100%;
    max-width: 350px;
    height: 250px;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
  }
  
  .use-cases-grid {
    grid-template-columns: 1fr;
  }
  
  .cta-buttons {
    flex-direction: column;
    align-items: center;
  }
}
</style>
