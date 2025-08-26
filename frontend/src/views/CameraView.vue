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
import { ref, reactive, onMounted, onUnmounted, computed, nextTick } from 'vue'

// MediaPipe 전역 타입 선언
declare global {
  interface Window {
    Hands: any
    Camera: any
  }
}

// 반응형 상태
const videoElement = ref<HTMLVideoElement | null>(null)
const canvasElement = ref<HTMLCanvasElement | null>(null)
const isCameraActive = ref(false)
const isLoading = ref(false)
const isGestureActive = ref(false)
const errorMessage = ref('')
const stream = ref<MediaStream | null>(null)
const debugInfo = ref('')

// MediaPipe 관련 상태
const hands = ref<any>(null)
const camera = ref<any>(null)
const detectedGestures = ref<string[]>([])
const handLandmarks = ref<any[]>([])
const gestureCount = ref(0)

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

// 손 제스처 분석 함수
const analyzeGesture = (landmarks: any) => {
  const gestures: string[] = []
  
  if (!landmarks || landmarks.length === 0) return gestures
  
  const hand = landmarks[0]
  
  // 손가락 끝점과 관절점 인덱스
  const fingerTips = [4, 8, 12, 16, 20] // 엄지, 검지, 중지, 약지, 소지
  const fingerMcps = [2, 5, 9, 13, 17] // 각 손가락의 기준점
  
  // 손가락이 펴져있는지 확인
  const isFingerUp = (tipIndex: number, mcpIndex: number) => {
    if (tipIndex === 4) { // 엄지는 다르게 처리
      return hand[tipIndex].x > hand[tipIndex - 1].x
    }
    return hand[tipIndex].y < hand[mcpIndex].y
  }
  
  const fingersUp = fingerTips.map((tip, index) => 
    isFingerUp(tip, fingerMcps[index])
  )
  
  const upFingerCount = fingersUp.filter(Boolean).length
  
  // 제스처 인식
  if (upFingerCount === 0) {
    gestures.push('주먹 ✊')
  } else if (upFingerCount === 1 && fingersUp[1]) {
    gestures.push('검지 👆')
  } else if (upFingerCount === 2 && fingersUp[1] && fingersUp[2]) {
    gestures.push('브이 ✌️')
  } else if (upFingerCount === 5) {
    gestures.push('손바닥 ✋')
  } else if (upFingerCount === 3 && fingersUp[0] && fingersUp[1] && fingersUp[4]) {
    gestures.push('아이러브유 🤟')
  }
  
  // 손의 위치도 분석
  const wrist = hand[0]
  if (wrist.y < 0.3) {
    gestures.push('위쪽 위치')
  } else if (wrist.y > 0.7) {
    gestures.push('아래쪽 위치')
  }
  
  return gestures
}

// CDN 스크립트 로드 함수
const loadScript = (src: string): Promise<void> => {
  return new Promise((resolve, reject) => {
    if (document.querySelector(`script[src="${src}"]`)) {
      resolve()
      return
    }
    
    const script = document.createElement('script')
    script.src = src
    script.onload = () => resolve()
    script.onerror = () => reject(new Error(`Failed to load script: ${src}`))
    document.head.appendChild(script)
  })
}

// 손 랜드마크 연결선 그리기
const drawHandConnections = (ctx: CanvasRenderingContext2D, landmarks: any[], connections: number[][]) => {
  ctx.strokeStyle = '#00FF00'
  ctx.lineWidth = 2
  
  for (const connection of connections) {
    const start = landmarks[connection[0]]
    const end = landmarks[connection[1]]
    
    ctx.beginPath()
    ctx.moveTo(start.x * ctx.canvas.width, start.y * ctx.canvas.height)
    ctx.lineTo(end.x * ctx.canvas.width, end.y * ctx.canvas.height)
    ctx.stroke()
  }
}

// 손 랜드마크 포인트 그리기
const drawHandLandmarks = (ctx: CanvasRenderingContext2D, landmarks: any[]) => {
  ctx.fillStyle = '#FF0000'
  
  for (const landmark of landmarks) {
    const x = landmark.x * ctx.canvas.width
    const y = landmark.y * ctx.canvas.height
    
    ctx.beginPath()
    ctx.arc(x, y, 3, 0, 2 * Math.PI)
    ctx.fill()
  }
}

// MediaPipe 초기화
const initializeMediaPipe = async () => {
  console.log('🤖 MediaPipe 손 인식 초기화 중...')
  
  try {
    // CDN 스크립트 로드
    await loadScript('https://cdn.jsdelivr.net/npm/@mediapipe/camera_utils/camera_utils.js')
    await loadScript('https://cdn.jsdelivr.net/npm/@mediapipe/control_utils/control_utils.js')
    await loadScript('https://cdn.jsdelivr.net/npm/@mediapipe/drawing_utils/drawing_utils.js')
    await loadScript('https://cdn.jsdelivr.net/npm/@mediapipe/hands/hands.js')
    
    console.log('📦 MediaPipe 스크립트 로드 완료')
    
    // @ts-ignore - MediaPipe 전역 객체 사용
    const Hands = window.Hands
    const Camera = window.Camera
    
    if (!Hands || !Camera) {
      throw new Error('MediaPipe 모듈을 찾을 수 없습니다.')
    }
    
    hands.value = new Hands({
      locateFile: (file: string) => {
        return `https://cdn.jsdelivr.net/npm/@mediapipe/hands/${file}`
      }
    })
    
    hands.value.setOptions({
      maxNumHands: 2,
      modelComplexity: 1,
      minDetectionConfidence: 0.5,
      minTrackingConfidence: 0.5
    })
    
    // 손 연결선 정의 (MediaPipe HAND_CONNECTIONS)
    const HAND_CONNECTIONS = [
      [0, 1], [1, 2], [2, 3], [3, 4],        // 엄지
      [0, 5], [5, 6], [6, 7], [7, 8],        // 검지
      [0, 17], [5, 9], [9, 10], [10, 11], [11, 12], // 중지
      [9, 13], [13, 14], [14, 15], [15, 16], // 약지
      [13, 17], [17, 18], [18, 19], [19, 20] // 소지
    ]
    
    hands.value.onResults((results: any) => {
      if (canvasElement.value && videoElement.value) {
        const canvas = canvasElement.value
        const video = videoElement.value
        const ctx = canvas.getContext('2d')
        
        if (ctx) {
          // 캔버스 크기 조정
          canvas.width = video.videoWidth
          canvas.height = video.videoHeight
          
          // 캔버스 클리어
          ctx.clearRect(0, 0, canvas.width, canvas.height)
          
          if (results.multiHandLandmarks && results.multiHandLandmarks.length > 0) {
            handLandmarks.value = results.multiHandLandmarks
            gestureCount.value++
            
            // 각 손에 대해 랜드마크 그리기
            for (const landmarks of results.multiHandLandmarks) {
              // 손 연결선 그리기
              drawHandConnections(ctx, landmarks, HAND_CONNECTIONS)
              
              // 손 관절점 그리기
              drawHandLandmarks(ctx, landmarks)
            }
            
            // 제스처 분석
            const gestures = analyzeGesture(results.multiHandLandmarks)
            detectedGestures.value = gestures
            
            if (gestures.length > 0) {
              console.log(`🖐️ 감지된 제스처: ${gestures.join(', ')}`)
              console.log(`📊 총 인식 횟수: ${gestureCount.value}`)
            }
            
            // 화면에 제스처 정보 표시
            ctx.fillStyle = 'rgba(255, 255, 255, 0.8)'
            ctx.fillRect(10, 10, 350, 80)
            ctx.fillStyle = 'black'
            ctx.font = 'bold 16px Arial'
            ctx.fillText(`🖐️ 인식된 제스처: ${gestures.join(', ')}`, 15, 35)
            ctx.font = '14px Arial'
            ctx.fillText(`📊 감지 횟수: ${gestureCount.value}`, 15, 55)
            ctx.fillText(`👥 감지된 손: ${results.multiHandLandmarks.length}개`, 15, 75)
          } else {
            handLandmarks.value = []
            detectedGestures.value = []
            
            // 손이 감지되지 않았을 때 메시지
            ctx.fillStyle = 'rgba(255, 255, 255, 0.8)'
            ctx.fillRect(10, 10, 250, 40)
            ctx.fillStyle = 'black'
            ctx.font = '16px Arial'
            ctx.fillText('👋 손을 카메라 앞에 올려주세요', 15, 30)
          }
        }
      }
    })
    
    // 카메라와 MediaPipe 연결
    if (videoElement.value) {
      camera.value = new Camera(videoElement.value, {
        onFrame: async () => {
          if (hands.value && videoElement.value) {
            await hands.value.send({ image: videoElement.value })
          }
        },
        width: 1280,
        height: 720
      })
      
      console.log('📹 MediaPipe 카메라 연결 완료')
    }
    
    console.log('✅ MediaPipe 손 인식 초기화 완료')
    
  } catch (error) {
    console.error('❌ MediaPipe 초기화 실패:', error)
    errorMessage.value = `MediaPipe 초기화 실패: ${(error as Error).message}`
  }
}

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
    console.log('🔍 비디오 트랙 정보:', mediaStream.getVideoTracks())
    stream.value = mediaStream
    
    // 화면을 카메라 스트림으로 전환
    isCameraActive.value = true
    
    // DOM 업데이트 완료까지 대기
    await nextTick()
    console.log('⏳ DOM 업데이트 완료 대기...')
    
    // 비디오 엘리먼트에 스트림 연결
    if (videoElement.value) {
      console.log('📺 비디오 엘리먼트 찾음:', videoElement.value)
      videoElement.value.srcObject = mediaStream
      
      // 여러 이벤트 리스너 추가
      videoElement.value.addEventListener('loadedmetadata', () => {
        console.log('✅ 비디오 메타데이터 로드 완료')
        console.log(`📐 비디오 해상도: ${videoElement.value!.videoWidth}x${videoElement.value!.videoHeight}`)
        
        debugInfo.value = `
카메라 정보:
- 해상도: ${videoElement.value!.videoWidth}x${videoElement.value!.videoHeight}
- 스트림 ID: ${mediaStream.id}
- 트랙 수: ${mediaStream.getVideoTracks().length}
- 활성 상태: ${mediaStream.active}
- 비디오 준비 상태: ${videoElement.value!.readyState}
        `.trim()
      })
      
      videoElement.value.addEventListener('loadeddata', () => {
        console.log('✅ 비디오 데이터 로드 완료')
      })
      
      videoElement.value.addEventListener('canplay', () => {
        console.log('✅ 비디오 재생 가능')
      })
      
      videoElement.value.addEventListener('playing', () => {
        console.log('✅ 비디오 재생 중')
        debugInfo.value = debugInfo.value + `\n- 재생 상태: ${!videoElement.value!.paused}`
      })
      
      videoElement.value.addEventListener('error', (e) => {
        console.error('❌ 비디오 에러:', e)
        errorMessage.value = '비디오 재생 오류가 발생했습니다.'
      })
      
      // 비디오 재생 시작
      try {
        await videoElement.value.play()
        console.log('✅ 비디오 재생 시작 명령 완료')
        console.log('🔍 비디오 엘리먼트 상태:')
        console.log('  - paused:', videoElement.value.paused)
        console.log('  - readyState:', videoElement.value.readyState)
        console.log('  - videoWidth:', videoElement.value.videoWidth)
        console.log('  - videoHeight:', videoElement.value.videoHeight)
      } catch (playError) {
        console.error('❌ 비디오 재생 실패:', playError)
        errorMessage.value = '비디오 재생을 시작할 수 없습니다.'
      }
    } else {
      console.error('❌ 비디오 엘리먼트를 찾을 수 없음')
      console.log('🔍 현재 isCameraActive:', isCameraActive.value)
      errorMessage.value = '비디오 엘리먼트를 찾을 수 없습니다.'
      
      // 다시 시도를 위해 상태 되돌리기
      isCameraActive.value = false
      if (stream.value) {
        stream.value.getTracks().forEach(track => track.stop())
        stream.value = null
      }
    }
    
  } catch (error) {
    console.error('❌ 카메라 시작 실패:', error)
    errorMessage.value = `카메라 접근 실패: ${(error as Error).message}`
    debugInfo.value = `에러 정보: ${JSON.stringify(error, null, 2)}`
    isCameraActive.value = false
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
const toggleGestureRecognition = async () => {
  isGestureActive.value = !isGestureActive.value
  console.log(`${isGestureActive.value ? '▶️' : '⏸️'} 제스처 인식: ${isGestureActive.value ? '시작' : '정지'}`)
  
  if (isGestureActive.value) {
    console.log('🖐️ 손 제스처 인식 활성화')
    
    // MediaPipe 초기화
    if (!hands.value) {
      await initializeMediaPipe()
    }
  } else {
    console.log('🛑 제스처 인식 비활성화')
    
    // 캔버스 클리어
    if (canvasElement.value) {
      const ctx = canvasElement.value.getContext('2d')
      if (ctx) {
        ctx.clearRect(0, 0, canvasElement.value.width, canvasElement.value.height)
      }
    }
    
    detectedGestures.value = []
    handLandmarks.value = []
  }
}

onMounted(() => {
  console.log('📱 카메라 뷰 로드됨')
  console.log('🔍 MediaDevices 지원:', !!navigator.mediaDevices)
  console.log('🔍 getUserMedia 지원:', !!navigator.mediaDevices?.getUserMedia)
})

onUnmounted(() => {
  console.log('🧹 카메라 뷰 언마운트 - 리소스 정리')
  
  // 제스처 인식 정리
  if (isGestureActive.value) {
    isGestureActive.value = false
  }
  
  // MediaPipe 리소스 정리
  if (hands.value) {
    hands.value.close()
  }
  
  // 카메라 정리
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
  border: 2px solid var(--primary-color);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  min-height: 400px;
  object-fit: cover;
}

.camera-canvas {
  position: absolute;
  top: 1rem;
  left: 1rem;
  right: 1rem;
  bottom: 1rem;
  width: calc(100% - 2rem);
  height: calc(100% - 2rem);
  pointer-events: none;
  border-radius: 0.5rem;
  max-width: 800px;
  margin: 0 auto;
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
