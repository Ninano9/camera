<template>
  <div class="camera-view">
    <div class="container">
      <h1>🎥 제스처 인식</h1>
      <p>카메라를 통한 실시간 손 제스처 및 시선 인식</p>
      
      <div class="camera-container">
        <!-- 카메라 준비 상태 -->
        <div v-if="!isCameraActive" class="camera-placeholder">
          <div class="camera-icon">📹</div>
          <h2>카메라 준비</h2>
          <p>브라우저에서 카메라 권한을 허용하고 제스처 인식을 시작하세요.</p>
          
          <div class="feature-list">
            <div class="feature active">✋ 손 제스처 인식</div>
            <div class="feature active">👁️ 시선 추적</div>
            <div class="feature active">🎯 실시간 매핑</div>
          </div>
          
          <div class="camera-actions">
            <button 
              @click="startCamera" 
              class="btn btn-primary btn-lg"
              :disabled="isLoading"
            >
              {{ isLoading ? '⏳ 시작 중...' : '📹 카메라 시작' }}
            </button>
            <router-link to="/guide" class="btn btn-outline">
              📚 사용 가이드
            </router-link>
          </div>
          
          <!-- 에러 메시지 -->
          <div v-if="errorMessage" class="error-message">
            ❌ {{ errorMessage }}
          </div>
        </div>
        
        <!-- 실제 카메라 스트림 -->
        <div v-else class="camera-stream">
          <video 
            ref="videoElement" 
            autoplay 
            playsinline 
            muted
            class="camera-video"
          ></video>
          <canvas 
            ref="canvasElement" 
            class="camera-canvas"
            v-show="false"
          ></canvas>
          
          <div class="camera-controls">
            <button @click="stopCamera" class="btn btn-secondary">
              ⏹️ 카메라 정지
            </button>
            <button @click="toggleGestureRecognition" class="btn btn-primary">
              {{ isGestureActive ? '⏸️ 제스처 정지' : '▶️ 제스처 시작' }}
            </button>
          </div>
        </div>
      </div>
      
      <!-- 제스처 상태 표시 -->
      <div class="status-panel">
        <div class="status-item">
          <span class="status-label">카메라 상태:</span>
          <span :class="['status-value', cameraStatus.class]">{{ cameraStatus.text }}</span>
        </div>
        <div class="status-item">
          <span class="status-label">제스처 인식:</span>
          <span :class="['status-value', gestureStatus.class]">{{ gestureStatus.text }}</span>
        </div>
        <div class="status-item">
          <span class="status-label">시선 추적:</span>
          <span :class="['status-value', gazeStatus.class]">{{ gazeStatus.text }}</span>
        </div>
      </div>
      
      <!-- 디버그 정보 -->
      <div v-if="debugInfo" class="debug-panel">
        <h3>🐛 디버그 정보</h3>
        <pre>{{ debugInfo }}</pre>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, computed } from 'vue'

// 반응형 상태
const videoElement = ref<HTMLVideoElement | null>(null)
const canvasElement = ref<HTMLCanvasElement | null>(null)
const isCameraActive = ref(false)
const isLoading = ref(false)
const isGestureActive = ref(false)
const errorMessage = ref('')
const stream = ref<MediaStream | null>(null)
const debugInfo = ref('')

// 상태 계산
const cameraStatus = computed(() => {
  if (isCameraActive.value) {
    return { text: '활성', class: 'active' }
  } else if (isLoading.value) {
    return { text: '시작 중...', class: 'pending' }
  } else if (errorMessage.value) {
    return { text: '에러', class: 'error' }
  } else {
    return { text: '대기 중', class: 'pending' }
  }
})

const gestureStatus = computed(() => {
  if (isGestureActive.value && isCameraActive.value) {
    return { text: '활성', class: 'active' }
  } else {
    return { text: '비활성', class: 'pending' }
  }
})

const gazeStatus = computed(() => {
  if (isGestureActive.value && isCameraActive.value) {
    return { text: '활성', class: 'active' }
  } else {
    return { text: '비활성', class: 'pending' }
  }
})

// 카메라 시작
const startCamera = async () => {
  console.log('🎥 카메라 시작 요청')
  isLoading.value = true
  errorMessage.value = ''
  
  try {
    // 카메라 권한 및 스트림 요청
    const mediaStream = await navigator.mediaDevices.getUserMedia({
      video: {
        width: { ideal: 1280 },
        height: { ideal: 720 },
        frameRate: { ideal: 30 }
      },
      audio: false
    })
    
    console.log('✅ 카메라 스트림 획득 성공:', mediaStream)
    stream.value = mediaStream
    
    // 비디오 엘리먼트에 스트림 연결
    if (videoElement.value) {
      videoElement.value.srcObject = mediaStream
      await videoElement.value.play()
      console.log('✅ 비디오 재생 시작')
      
      isCameraActive.value = true
      debugInfo.value = `
카메라 정보:
- 해상도: ${videoElement.value.videoWidth}x${videoElement.value.videoHeight}
- 스트림 ID: ${mediaStream.id}
- 트랙 수: ${mediaStream.getVideoTracks().length}
- 활성 상태: ${mediaStream.active}
      `.trim()
    }
    
  } catch (error) {
    console.error('❌ 카메라 시작 실패:', error)
    errorMessage.value = `카메라 접근 실패: ${(error as Error).message}`
    debugInfo.value = `에러 정보: ${JSON.stringify(error, null, 2)}`
  } finally {
    isLoading.value = false
  }
}

// 카메라 정지
const stopCamera = () => {
  console.log('⏹️ 카메라 정지')
  
  if (stream.value) {
    stream.value.getTracks().forEach(track => {
      track.stop()
      console.log('🔄 카메라 트랙 정지:', track.kind)
    })
    stream.value = null
  }
  
  if (videoElement.value) {
    videoElement.value.srcObject = null
  }
  
  isCameraActive.value = false
  isGestureActive.value = false
  debugInfo.value = ''
}

// 제스처 인식 토글
const toggleGestureRecognition = () => {
  isGestureActive.value = !isGestureActive.value
  console.log(`${isGestureActive.value ? '▶️' : '⏸️'} 제스처 인식: ${isGestureActive.value ? '시작' : '정지'}`)
  
  if (isGestureActive.value) {
    // TODO: MediaPipe 손 제스처 인식 시작
    console.log('🖐️ 손 제스처 인식 활성화')
    console.log('👁️ 시선 추적 활성화')
  } else {
    // TODO: 제스처 인식 정지
    console.log('🛑 제스처 인식 비활성화')
  }
}

onMounted(() => {
  console.log('📱 카메라 뷰 로드됨')
  console.log('🔍 MediaDevices 지원:', !!navigator.mediaDevices)
  console.log('🔍 getUserMedia 지원:', !!navigator.mediaDevices?.getUserMedia)
})

onUnmounted(() => {
  console.log('🧹 카메라 뷰 언마운트 - 리소스 정리')
  stopCamera()
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

.camera-stream {
  background: var(--bg-color);
  border-radius: 1rem;
  padding: 1rem;
  box-shadow: var(--shadow);
  position: relative;
}

.camera-video {
  width: 100%;
  max-width: 800px;
  height: auto;
  border-radius: 0.5rem;
  background: #000;
  display: block;
  margin: 0 auto;
}

.camera-canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.camera-controls {
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-top: 1rem;
  flex-wrap: wrap;
}

.error-message {
  background: var(--error-color);
  color: white;
  padding: 1rem;
  border-radius: 0.5rem;
  margin-top: 1rem;
  text-align: center;
}

.debug-panel {
  background: var(--bg-color);
  border-radius: 1rem;
  padding: 1.5rem;
  margin-top: 2rem;
  box-shadow: var(--shadow);
}

.debug-panel h3 {
  margin-bottom: 1rem;
  color: var(--text-color);
}

.debug-panel pre {
  background: var(--bg-secondary);
  padding: 1rem;
  border-radius: 0.5rem;
  overflow-x: auto;
  font-size: 0.875rem;
  color: var(--text-color);
  white-space: pre-wrap;
  word-break: break-word;
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
