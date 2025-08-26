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
      
      <!-- 제스처 사용법 안내 -->
      <div v-if="isGestureActive" class="gesture-guide">
        <h3>🖐️ 제스처 사용법</h3>
        <div class="gesture-instructions">
          <div class="instruction-item">
            <span class="gesture-icon">👆</span>
            <div class="instruction-text">
              <strong>마우스 포인터</strong>
              <p>검지만 펼치고 손을 움직여 커서 이동</p>
            </div>
          </div>
          <div class="instruction-item">
            <span class="gesture-icon">✊</span>
            <div class="instruction-text">
              <strong>좌클릭</strong>
              <p>주먹 쥐고 0.3초 유지</p>
            </div>
          </div>
          <div class="instruction-item">
            <span class="gesture-icon">✌️</span>
            <div class="instruction-text">
              <strong>우클릭</strong>
              <p>브이 사인 만들고 0.3초 유지</p>
            </div>
          </div>
          <div class="instruction-item">
            <span class="gesture-icon">✋</span>
            <div class="instruction-text">
              <strong>스크롤</strong>
              <p>손바닥 펼치고 위/아래 위치에서 0.5초 유지</p>
            </div>
          </div>
          <div class="instruction-item">
            <span class="gesture-icon">🤟</span>
            <div class="instruction-text">
              <strong>ESC 키</strong>
              <p>아이러브유 사인 0.7초 유지</p>
            </div>
          </div>
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
const currentGesture = ref('')
const lastGestureTime = ref(0)
const gestureHoldTime = ref(0)
const isPerformingAction = ref(false)

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

// 마우스 제어 함수들
const simulateMouseMove = (x: number, y: number) => {
  // 웹에서는 직접적인 마우스 제어가 불가능하므로 시뮬레이션
  console.log(`🖱️ 마우스 이동: (${Math.round(x)}, ${Math.round(y)})`)
  
  // 화면에 커서 위치 표시용 이벤트 발생
  const event = new CustomEvent('gesture-mouse-move', {
    detail: { x, y }
  })
  window.dispatchEvent(event)
}

const simulateClick = (button: 'left' | 'right' = 'left') => {
  console.log(`🖱️ ${button} 클릭 시뮬레이션`)
  isPerformingAction.value = true
  
  setTimeout(() => {
    isPerformingAction.value = false
  }, 500)
  
  // 실제 클릭 이벤트 발생
  const event = new CustomEvent('gesture-click', {
    detail: { button }
  })
  window.dispatchEvent(event)
}

const simulateScroll = (direction: 'up' | 'down') => {
  console.log(`📜 스크롤 ${direction === 'up' ? '위로' : '아래로'}`)
  isPerformingAction.value = true
  
  setTimeout(() => {
    isPerformingAction.value = false
  }, 300)
  
  // 실제 스크롤 이벤트 발생
  window.scrollBy(0, direction === 'up' ? -100 : 100)
}

// 손 제스처 분석 및 액션 실행 함수
const analyzeGestureAndPerformAction = (landmarks: any) => {
  const gestures: string[] = []
  
  if (!landmarks || landmarks.length === 0) {
    currentGesture.value = ''
    return gestures
  }
  
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
  
  // 손목 위치 (마우스 커서 제어용)
  const wrist = hand[0]
  const indexTip = hand[8] // 검지 끝
  
  // 화면 좌표로 변환 (0~1 범위를 화면 크기로 변환)
  const screenX = indexTip.x * window.innerWidth
  const screenY = indexTip.y * window.innerHeight
  
  let detectedGesture = ''
  
  // 제스처 인식 및 액션
  if (upFingerCount === 1 && fingersUp[1]) {
    // 검지만 펼침 - 마우스 포인터 모드
    detectedGesture = '마우스 포인터 👆'
    gestures.push(detectedGesture)
    simulateMouseMove(screenX, screenY)
    
  } else if (upFingerCount === 0) {
    // 주먹 - 좌클릭
    detectedGesture = '좌클릭 ✊'
    gestures.push(detectedGesture)
    
    if (currentGesture.value === detectedGesture) {
      gestureHoldTime.value += 1
      if (gestureHoldTime.value === 10) { // 약 0.3초 유지 시
        simulateClick('left')
      }
    } else {
      gestureHoldTime.value = 0
    }
    
  } else if (upFingerCount === 2 && fingersUp[1] && fingersUp[2]) {
    // 브이 - 우클릭
    detectedGesture = '우클릭 ✌️'
    gestures.push(detectedGesture)
    
    if (currentGesture.value === detectedGesture) {
      gestureHoldTime.value += 1
      if (gestureHoldTime.value === 10) {
        simulateClick('right')
      }
    } else {
      gestureHoldTime.value = 0
    }
    
  } else if (upFingerCount === 5) {
    // 손바닥 - 스크롤 모드
    detectedGesture = '스크롤 모드 ✋'
    gestures.push(detectedGesture)
    
    // 손의 세로 위치로 스크롤 방향 결정
    if (wrist.y < 0.3) {
      gestures.push('위로 스크롤')
      if (currentGesture.value === detectedGesture) {
        gestureHoldTime.value += 1
        if (gestureHoldTime.value === 15) { // 약 0.5초 유지 시
          simulateScroll('up')
          gestureHoldTime.value = 0
        }
      }
    } else if (wrist.y > 0.7) {
      gestures.push('아래로 스크롤')
      if (currentGesture.value === detectedGesture) {
        gestureHoldTime.value += 1
        if (gestureHoldTime.value === 15) {
          simulateScroll('down')
          gestureHoldTime.value = 0
        }
      }
    }
    
  } else if (upFingerCount === 3 && fingersUp[0] && fingersUp[1] && fingersUp[4]) {
    // 아이러브유 - 특수 기능 (ESC)
    detectedGesture = 'ESC 키 🤟'
    gestures.push(detectedGesture)
    
    if (currentGesture.value === detectedGesture) {
      gestureHoldTime.value += 1
      if (gestureHoldTime.value === 20) { // 약 0.7초 유지 시
        console.log('⌨️ ESC 키 눌림')
        // ESC 키 시뮬레이션
        document.dispatchEvent(new KeyboardEvent('keydown', { key: 'Escape' }))
        gestureHoldTime.value = 0
      }
    } else {
      gestureHoldTime.value = 0
    }
  }
  
  // 제스처 변경 감지
  if (currentGesture.value !== detectedGesture) {
    currentGesture.value = detectedGesture
    gestureHoldTime.value = 0
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
  console.log('📹 비디오 엘리먼트 상태:', !!videoElement.value)
  console.log('🎨 캔버스 엘리먼트 상태:', !!canvasElement.value)
  
  try {
    // CDN 스크립트 로드
    console.log('📥 스크립트 로드 시작...')
    await loadScript('https://cdn.jsdelivr.net/npm/@mediapipe/camera_utils/camera_utils.js')
    console.log('✅ camera_utils 로드 완료')
    
    await loadScript('https://cdn.jsdelivr.net/npm/@mediapipe/control_utils/control_utils.js')
    console.log('✅ control_utils 로드 완료')
    
    await loadScript('https://cdn.jsdelivr.net/npm/@mediapipe/drawing_utils/drawing_utils.js')
    console.log('✅ drawing_utils 로드 완료')
    
    await loadScript('https://cdn.jsdelivr.net/npm/@mediapipe/hands/hands.js')
    console.log('✅ hands 로드 완료')
    
    console.log('📦 MediaPipe 스크립트 로드 완료')
    
    // @ts-ignore - MediaPipe 전역 객체 사용
    const Hands = window.Hands
    const Camera = window.Camera
    
    console.log('🔍 MediaPipe 객체 확인:')
    console.log('- Hands 존재:', !!Hands)
    console.log('- Camera 존재:', !!Camera)
    
    if (!Hands || !Camera) {
      throw new Error('MediaPipe 모듈을 찾을 수 없습니다.')
    }
    
    console.log('🖐️ Hands 객체 생성 중...')
    hands.value = new Hands({
      locateFile: (file: string) => {
        const url = `https://cdn.jsdelivr.net/npm/@mediapipe/hands/${file}`
        console.log(`📁 모델 파일 로드: ${file}`)
        return url
      }
    })
    
    hands.value.setOptions({
      maxNumHands: 2,
      modelComplexity: 1,
      minDetectionConfidence: 0.3, // 더 낮은 임계값
      minTrackingConfidence: 0.3   // 더 낮은 임계값
    })
    
    console.log('⚙️ Hands 설정 완료 (임계값: 0.3)')
    
    // 손 연결선 정의 (MediaPipe HAND_CONNECTIONS)
    const HAND_CONNECTIONS = [
      [0, 1], [1, 2], [2, 3], [3, 4],        // 엄지
      [0, 5], [5, 6], [6, 7], [7, 8],        // 검지
      [0, 17], [5, 9], [9, 10], [10, 11], [11, 12], // 중지
      [9, 13], [13, 14], [14, 15], [15, 16], // 약지
      [13, 17], [17, 18], [18, 19], [19, 20] // 소지
    ]
    
    hands.value.onResults((results: any) => {
      // 프레임 처리 카운터
      gestureCount.value++
      
      // 매 30프레임마다 처리 상태 로그
      if (gestureCount.value % 30 === 0) {
        console.log(`📸 프레임 처리 중... ${gestureCount.value}번째`)
        console.log('🔍 결과 상태:', {
          hasResults: !!results,
          hasMultiHandLandmarks: !!results?.multiHandLandmarks,
          handCount: results?.multiHandLandmarks?.length || 0,
          canvasElement: !!canvasElement.value,
          videoElement: !!videoElement.value
        })
      }
      
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
            // 손 감지 성공 로그
            if (gestureCount.value % 10 === 0) { // 더 자주 로그
              console.log(`✋ 손 감지 성공! 손 개수: ${results.multiHandLandmarks.length}`)
              console.log('👆 첫 번째 손 랜드마크 샘플:', {
                wrist: results.multiHandLandmarks[0][0],
                indexTip: results.multiHandLandmarks[0][8],
                thumbTip: results.multiHandLandmarks[0][4]
              })
            }
            
            handLandmarks.value = results.multiHandLandmarks
            
            // 각 손에 대해 랜드마크 그리기
            for (let i = 0; i < results.multiHandLandmarks.length; i++) {
              const landmarks = results.multiHandLandmarks[i]
              
              // 손 연결선 그리기
              drawHandConnections(ctx, landmarks, HAND_CONNECTIONS)
              
              // 손 관절점 그리기
              drawHandLandmarks(ctx, landmarks)
              
              // 손별 디버그 정보
              if (gestureCount.value % 30 === 0) {
                console.log(`🖐️ 손 ${i + 1} 정보:`, {
                  landmarkCount: landmarks.length,
                  wristPos: `(${Math.round(landmarks[0].x * 100)}%, ${Math.round(landmarks[0].y * 100)}%)`,
                  confidence: landmarks[0].visibility || 'N/A'
                })
              }
            }
            
            // 제스처 분석 및 액션 실행
            const gestures = analyzeGestureAndPerformAction(results.multiHandLandmarks)
            detectedGestures.value = gestures
            
            if (gestures.length > 0) {
              console.log(`🖐️ 현재 제스처: ${gestures.join(', ')}`)
              console.log(`⏱️ 유지 시간: ${gestureHoldTime.value}/20`)
            }
            
            // 화면에 제스처 정보 및 액션 상태 표시
            const bgColor = isPerformingAction.value ? 'rgba(255, 200, 0, 0.9)' : 'rgba(255, 255, 255, 0.9)'
            ctx.fillStyle = bgColor
            ctx.fillRect(10, 10, 450, 120)
            
            // 테두리 그리기
            ctx.strokeStyle = isPerformingAction.value ? '#ff6600' : '#333'
            ctx.lineWidth = 2
            ctx.strokeRect(10, 10, 450, 120)
            
            ctx.fillStyle = 'black'
            ctx.font = 'bold 18px Arial'
            ctx.fillText(`🖐️ 현재 제스처: ${gestures.join(', ')}`, 15, 35)
            
            ctx.font = '16px Arial'
            if (gestureHoldTime.value > 0) {
              const progress = Math.round((gestureHoldTime.value / 20) * 100)
              ctx.fillText(`⏱️ 액션 진행도: ${progress}%`, 15, 60)
              
              // 진행바 그리기
              ctx.fillStyle = '#007bff'
              ctx.fillRect(15, 70, progress * 3, 10)
              ctx.strokeStyle = '#333'
              ctx.strokeRect(15, 70, 300, 10)
            }
            
            ctx.fillStyle = 'black'
            ctx.font = '14px Arial'
            ctx.fillText(`👥 감지된 손: ${results.multiHandLandmarks.length}개`, 15, 100)
            ctx.fillText(`🎯 총 처리: ${gestureCount.value}회`, 15, 115)
            
            if (isPerformingAction.value) {
              ctx.fillStyle = 'red'
              ctx.font = 'bold 16px Arial'
              ctx.fillText('🔥 액션 실행 중!', 320, 35)
            }
          } else {
            handLandmarks.value = []
            detectedGestures.value = []
            
            // 손이 감지되지 않았을 때 로그 (가끔씩만)
            if (gestureCount.value % 60 === 0) {
              console.log('👋 손 감지 안됨 - 손을 카메라 앞에 보여주세요')
              console.log('💡 팁: 손을 더 가까이, 조명이 밝은 곳에서 시도해보세요')
            }
            
            // 손이 감지되지 않았을 때 메시지
            ctx.fillStyle = 'rgba(255, 100, 100, 0.9)'
            ctx.fillRect(10, 10, 400, 80)
            ctx.strokeStyle = '#ff3333'
            ctx.lineWidth = 2
            ctx.strokeRect(10, 10, 400, 80)
            
            ctx.fillStyle = 'white'
            ctx.font = 'bold 18px Arial'
            ctx.fillText('👋 손을 카메라 앞에 보여주세요', 15, 35)
            ctx.font = '14px Arial'
            ctx.fillText(`📸 프레임 처리 중: ${gestureCount.value}회`, 15, 55)
            ctx.fillText('💡 팁: 조명이 밝은 곳에서 시도하세요', 15, 75)
          }
        }
      }
    })
    
    // 카메라와 MediaPipe 연결
    if (videoElement.value) {
      console.log('📷 MediaPipe Camera 객체 생성 중...')
      camera.value = new Camera(videoElement.value, {
        onFrame: async () => {
          if (hands.value && videoElement.value) {
            try {
              await hands.value.send({ image: videoElement.value })
              // 프레임 전송 확인 (100번마다)
              if (gestureCount.value % 100 === 0 && gestureCount.value > 0) {
                console.log(`📸 프레임 전송 중... ${gestureCount.value}번째`)
              }
            } catch (frameError) {
              console.error('⚠️ 프레임 전송 오류:', frameError)
            }
          }
        },
        width: 1280,
        height: 720
      })
      
      console.log('📹 MediaPipe 카메라 연결 완료')
      console.log('🎬 카메라 해상도: 1280x720')
    } else {
      throw new Error('❌ 비디오 엘리먼트가 준비되지 않음')
    }
    
    console.log('✅ MediaPipe 손 인식 초기화 완료!')
    console.log('👋 이제 손을 카메라 앞에 보여주세요!')
    
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
    console.log('🎯 제스처 인식 시작 요청...')
    console.log('📋 현재 상태 체크:')
    console.log('- 카메라 활성:', isCameraActive.value)
    console.log('- 비디오 엘리먼트:', !!videoElement.value)
    console.log('- 캔버스 엘리먼트:', !!canvasElement.value)
    console.log('- 비디오 크기:', videoElement.value?.videoWidth, 'x', videoElement.value?.videoHeight)
    
    if (!isCameraActive.value) {
      console.error('❌ 먼저 카메라를 시작해주세요!')
      isGestureActive.value = false
      return
    }
    
    console.log('🖐️ 손 제스처 인식 활성화')
    
    // MediaPipe 초기화
    if (!hands.value) {
      console.log('🤖 MediaPipe 첫 초기화 시작...')
      await initializeMediaPipe()
    } else {
      console.log('♻️ 기존 MediaPipe 재사용')
    }
    
    console.log('🎉 제스처 인식 시작 완료!')
    console.log('📍 사용법:')
    console.log('  👆 검지만 펼침 = 마우스 포인터')
    console.log('  ✊ 주먹 0.3초 유지 = 좌클릭')
    console.log('  ✌️ 브이 0.3초 유지 = 우클릭')
    console.log('  ✋ 손바닥 위/아래 0.5초 = 스크롤')
    
  } else {
    console.log('🛑 제스처 인식 비활성화')
    
    // 캔버스 클리어
    if (canvasElement.value) {
      const ctx = canvasElement.value.getContext('2d')
      if (ctx) {
        ctx.clearRect(0, 0, canvasElement.value.width, canvasElement.value.height)
        console.log('🎨 캔버스 클리어 완료')
      }
    }
    
    detectedGestures.value = []
    handLandmarks.value = []
    gestureCount.value = 0
    console.log('✅ 제스처 인식 중지 완료')
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

.gesture-guide {
  background: var(--bg-color);
  border-radius: 1rem;
  padding: 2rem;
  margin-top: 2rem;
  box-shadow: var(--shadow);
  border: 2px solid var(--primary-color);
}

.gesture-guide h3 {
  margin-bottom: 1.5rem;
  color: var(--text-color);
  text-align: center;
}

.gesture-instructions {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1rem;
}

.instruction-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: var(--bg-secondary);
  border-radius: 0.5rem;
  border: 1px solid var(--border-color);
}

.gesture-icon {
  font-size: 2rem;
  min-width: 40px;
  text-align: center;
}

.instruction-text strong {
  color: var(--primary-color);
  font-size: 1.1rem;
  display: block;
  margin-bottom: 0.25rem;
}

.instruction-text p {
  color: var(--text-secondary);
  font-size: 0.9rem;
  margin: 0;
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
