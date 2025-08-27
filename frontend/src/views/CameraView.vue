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
              <strong>마우스 포인터 + 좌클릭</strong>
              <p>검지만 펼치고 손을 움직여 커서 이동<br>검지를 까딱하면 좌클릭</p>
            </div>
          </div>
          <div class="instruction-item">
            <span class="gesture-icon">🖕</span>
            <div class="instruction-text">
              <strong>우클릭</strong>
              <p>중지만 펼치고 까딱하면 우클릭</p>
            </div>
          </div>
          <div class="instruction-item">
            <span class="gesture-icon">✌️</span>
            <div class="instruction-text">
              <strong>스크롤</strong>
              <p>검지+중지 동시에 위/아래로 움직이면 스크롤</p>
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
import { ref, reactive, onMounted, onUnmounted, computed, nextTick, toRaw } from 'vue'

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

// MediaPipe 관련 상태 (Vue 반응형 시스템 밖에서 관리)
let nativeHands: any = null
let nativeCamera: any = null
const hands = ref<any>(null) // 표시용으로만 사용
const camera = ref<any>(null) // 표시용으로만 사용
const detectedGestures = ref<string[]>([])
const handLandmarks = ref<any[]>([])
const gestureCount = ref(0)
const currentGesture = ref('')
const lastGestureTime = ref(0)
const gestureHoldTime = ref(0)
const isPerformingAction = ref(false)
const lastFingerMovement = ref<{
  indexY: number
  middleY: number
  downMovement: boolean
  clickExecuted: boolean
} | null>(null)

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

// 백엔드 연결 상태 확인 함수
const checkBackendConnection = async () => {
  try {
    const healthUrl = `${import.meta.env.VITE_API_BASE_URL}/health`
    console.log(`🏥 백엔드 헬스 체크: ${healthUrl}`)
    
    const response = await fetch(healthUrl, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      }
    })
    
    console.log(`📡 헬스 체크 응답: ${response.status} ${response.statusText}`)
    
    if (response.ok) {
      const result = await response.json()
      console.log(`✅ 백엔드 서버 정상 연결됨:`, result)
      return true
    } else {
      console.error(`❌ 백엔드 서버 응답 오류: ${response.status}`)
      return false
    }
  } catch (error) {
    console.error('❌ 백엔드 서버 연결 실패:', error)
    console.error('🔍 연결 오류 상세:', {
      message: error.message,
      healthUrl: `${import.meta.env.VITE_API_BASE_URL}/health`
    })
    return false
  }
}

// 백엔드 API를 통한 실제 마우스 제어 함수들
const executeMouseMove = async (x: number, y: number) => {
  try {
    const apiUrl = `${import.meta.env.VITE_API_BASE_URL}/api/gesture/mouse/move`
    console.log(`🔗 마우스 이동 API 호출: ${apiUrl}`)
    console.log(`📍 요청 좌표: (${Math.round(x)}, ${Math.round(y)})`)
    
    const response = await fetch(apiUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        gestureType: 'mouse_move',
        x: Math.round(x),
        y: Math.round(y)
      })
    })
    
    console.log(`📡 응답 상태: ${response.status} ${response.statusText}`)
    
    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`)
    }
    
    const result = await response.json()
    console.log(`📋 응답 결과:`, result)
    
    if (result.success) {
      console.log(`✅ 실제 마우스 이동 성공: (${Math.round(x)}, ${Math.round(y)})`)
    } else {
      console.error('❌ 마우스 이동 실패:', result.message)
    }
  } catch (error) {
    console.error('❌ 마우스 이동 API 호출 실패:', error)
    console.error('🔍 상세 오류:', {
      message: error.message,
      stack: error.stack,
      apiUrl: `${import.meta.env.VITE_API_BASE_URL}/api/gesture/mouse/move`
    })
    // 백엔드가 연결되지 않은 경우 시뮬레이션으로 대체
    console.log(`🖱️ 마우스 이동 시뮬레이션: (${Math.round(x)}, ${Math.round(y)})`)
  }
}

const executeClick = async (button: 'left' | 'right' = 'left') => {
  try {
    isPerformingAction.value = true
    
    const endpoint = button === 'left' ? 'left-click' : 'right-click'
    const apiUrl = `${import.meta.env.VITE_API_BASE_URL}/api/gesture/mouse/${endpoint}`
    console.log(`🔗 ${button} 클릭 API 호출: ${apiUrl}`)
    
    const response = await fetch(apiUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      }
    })
    
    console.log(`📡 ${button} 클릭 응답 상태: ${response.status} ${response.statusText}`)
    
    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`)
    }
    
    const result = await response.json()
    console.log(`📋 ${button} 클릭 응답 결과:`, result)
    
    if (result.success) {
      console.log(`✅ 실제 ${button} 클릭 실행 완료`)
    } else {
      console.error(`❌ ${button} 클릭 실패:`, result.message)
    }
    
    setTimeout(() => {
      isPerformingAction.value = false
    }, 500)
    
  } catch (error) {
    console.error(`❌ ${button} 클릭 API 호출 실패:`, error)
    console.error('🔍 클릭 상세 오류:', {
      message: error.message,
      apiUrl: `${import.meta.env.VITE_API_BASE_URL}/api/gesture/mouse/${button === 'left' ? 'left-click' : 'right-click'}`
    })
    // 백엔드가 연결되지 않은 경우 페이지 스크롤로 대체
    if (button === 'left') {
      window.scrollBy(0, -50) // 위로 스크롤
      console.log('🔄 대체 동작: 페이지 위로 스크롤')
    }
    
    setTimeout(() => {
      isPerformingAction.value = false
    }, 500)
  }
}

const executeScroll = async (direction: 'up' | 'down') => {
  try {
    isPerformingAction.value = true
    
    const apiUrl = `${import.meta.env.VITE_API_BASE_URL}/api/gesture/mouse/scroll`
    console.log(`🔗 스크롤 API 호출: ${apiUrl}`)
    console.log(`📜 스크롤 방향: ${direction}`)
    
    const response = await fetch(apiUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        gestureType: `scroll_${direction}`,
        direction: direction,
        amount: 3
      })
    })
    
    console.log(`📡 스크롤 응답 상태: ${response.status} ${response.statusText}`)
    
    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`)
    }
    
    const result = await response.json()
    console.log(`📋 스크롤 응답 결과:`, result)
    
    if (result.success) {
      console.log(`✅ 실제 스크롤 ${direction === 'up' ? '위로' : '아래로'} 실행 완료`)
    } else {
      console.error(`❌ 스크롤 ${direction} 실패:`, result.message)
    }
    
    setTimeout(() => {
      isPerformingAction.value = false
    }, 300)
    
  } catch (error) {
    console.error(`❌ 스크롤 ${direction} API 호출 실패:`, error)
    console.error('🔍 스크롤 상세 오류:', {
      message: error.message,
      apiUrl: `${import.meta.env.VITE_API_BASE_URL}/api/gesture/mouse/scroll`
    })
    // 백엔드가 연결되지 않은 경우 페이지 스크롤로 대체
    window.scrollBy(0, direction === 'up' ? -100 : 100)
    console.log(`🔄 대체 동작: 페이지 ${direction === 'up' ? '위로' : '아래로'} 스크롤`)
    
    setTimeout(() => {
      isPerformingAction.value = false
    }, 300)
  }
}

const executeKeyPress = async (key: string) => {
  try {
    const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}/api/gesture/keyboard/key?key=${key}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      }
    })
    
    const result = await response.json()
    if (result.success) {
      console.log(`⌨️ 실제 ${key} 키 입력 완료`)
    } else {
      console.error(`❌ ${key} 키 입력 실패:`, result.message)
    }
  } catch (error) {
    console.error(`❌ ${key} 키 입력 API 호출 실패:`, error)
    // 백엔드가 연결되지 않은 경우 브라우저 이벤트로 대체
    document.dispatchEvent(new KeyboardEvent('keydown', { key: key }))
  }
}

// 사용자 요구사항에 맞는 손 제스처 분석 및 액션 실행 함수
const analyzeGestureAndPerformAction = (landmarks: any) => {
  const gestures: string[] = []
  
  if (!landmarks || landmarks.length === 0) {
    currentGesture.value = ''
    lastFingerMovement.value = null
    return gestures
  }
  
  const hand = landmarks[0]
  
  // 더 정확한 손가락 상태 분석
  const fingerStates = analyzeFingerStates(hand)
  const upFingerCount = fingerStates.filter(Boolean).length
  
  // 화면 좌표로 변환 (부드러운 이동을 위한 보정)
  const indexTip = hand[8] // 검지 끝
  const middleTip = hand[12] // 중지 끝
  const smoothedX = indexTip.x * window.innerWidth
  const smoothedY = indexTip.y * window.innerHeight
  
  let detectedGesture = ''
  
  // 1. 손가락 하나 (검지만) - 마우스 커서 이동 + 까딱으로 좌클릭
  if (upFingerCount === 1 && fingerStates[1] && !fingerStates[0] && !fingerStates[2] && !fingerStates[3] && !fingerStates[4]) {
    detectedGesture = '마우스 포인터 👆'
    gestures.push(detectedGesture)
    gestures.push(`위치: (${Math.round(smoothedX)}, ${Math.round(smoothedY)})`)
    
    // 실제 마우스 이동 (연속 호출)
    executeMouseMove(smoothedX, smoothedY)
    
    // 검지 까딱 감지 (검지가 갑자기 아래로 움직였다가 다시 올라오는 동작)
    const currentIndexY = indexTip.y
    if (lastFingerMovement.value) {
      const yDiff = currentIndexY - lastFingerMovement.value.indexY
      
      // 검지가 아래로 움직였다가 다시 올라오는 까딱 동작 감지
      if (Math.abs(yDiff) > 0.03 && !lastFingerMovement.value.clickExecuted) {
        if (yDiff > 0.03) { // 아래로 움직임
          lastFingerMovement.value.downMovement = true
        } else if (yDiff < -0.03 && lastFingerMovement.value.downMovement) { // 다시 올라옴
          detectedGesture = '검지 까딱 - 좌클릭! 👆🖱️'
          executeClick('left')
          lastFingerMovement.value.clickExecuted = true
          gestures.push('좌클릭 실행!')
          console.log('🔄 검지 까딱으로 좌클릭 실행!')
        }
      }
    }
    
    // 현재 위치 저장
    lastFingerMovement.value = {
      indexY: currentIndexY,
      middleY: middleTip.y,
      downMovement: lastFingerMovement.value?.downMovement || false,
      clickExecuted: lastFingerMovement.value?.clickExecuted || false
    }
    
  } 
  // 2. 손가락 둘 (검지 + 중지) - 스크롤 모드
  else if (upFingerCount === 2 && fingerStates[1] && fingerStates[2] && !fingerStates[0] && !fingerStates[3] && !fingerStates[4]) {
    detectedGesture = '스크롤 모드 ✌️'
    gestures.push(detectedGesture)
    
    const currentIndexY = indexTip.y
    const currentMiddleY = middleTip.y
    
    if (lastFingerMovement.value) {
      const indexYDiff = currentIndexY - lastFingerMovement.value.indexY
      const middleYDiff = currentMiddleY - lastFingerMovement.value.middleY
      const avgYDiff = (indexYDiff + middleYDiff) / 2
      
      // 두 손가락이 함께 위아래로 움직이는 동작 감지
      if (Math.abs(avgYDiff) > 0.04) {
        if (avgYDiff < -0.04) { // 위로 움직임
          detectedGesture = '위로 스크롤! ⬆️📜'
          executeScroll('up')
          gestures.push('위로 스크롤 실행!')
          console.log('🔄 검지+중지 위로 움직여서 위로 스크롤!')
        } else if (avgYDiff > 0.04) { // 아래로 움직임
          detectedGesture = '아래로 스크롤! ⬇️📜'
          executeScroll('down')
          gestures.push('아래로 스크롤 실행!')
          console.log('🔄 검지+중지 아래로 움직여서 아래로 스크롤!')
        }
      }
    }
    
    // 현재 위치 저장
    lastFingerMovement.value = {
      indexY: currentIndexY,
      middleY: currentMiddleY,
      downMovement: false,
      clickExecuted: false
    }
    
  }
  // 3. 중지 하나만 - 우클릭 (추가 구현)
  else if (upFingerCount === 1 && fingerStates[2] && !fingerStates[0] && !fingerStates[1] && !fingerStates[3] && !fingerStates[4]) {
    detectedGesture = '중지 포인터 🖕'
    gestures.push(detectedGesture)
    
    const currentMiddleY = middleTip.y
    if (lastFingerMovement.value) {
      const yDiff = currentMiddleY - lastFingerMovement.value.middleY
      
      // 중지 까딱 동작 감지
      if (Math.abs(yDiff) > 0.03 && !lastFingerMovement.value.clickExecuted) {
        if (yDiff > 0.03) { // 아래로 움직임
          lastFingerMovement.value.downMovement = true
        } else if (yDiff < -0.03 && lastFingerMovement.value.downMovement) { // 다시 올라옴
          detectedGesture = '중지 까딱 - 우클릭! 🖕🖱️'
          executeClick('right')
          lastFingerMovement.value.clickExecuted = true
          gestures.push('우클릭 실행!')
          console.log('🔄 중지 까딱으로 우클릭 실행!')
        }
      }
    }
    
    // 현재 위치 저장
    lastFingerMovement.value = {
      indexY: indexTip.y,
      middleY: currentMiddleY,
      downMovement: lastFingerMovement.value?.downMovement || false,
      clickExecuted: lastFingerMovement.value?.clickExecuted || false
    }
    
  } else {
    // 다른 제스처 또는 인식되지 않은 제스처
    detectedGesture = `다른 제스처 (${upFingerCount}개 손가락)`
    gestures.push(detectedGesture)
    gestures.push('👆 검지 = 마우스 이동 + 까딱으로 좌클릭')
    gestures.push('🖕 중지 = 까딱으로 우클릭')
    gestures.push('✌️ 검지+중지 = 위아래로 스크롤')
    
    // 제스처가 변경되면 이전 동작 상태 초기화
    lastFingerMovement.value = null
  }
  
  // 제스처 변경 감지
  if (currentGesture.value !== detectedGesture) {
    currentGesture.value = detectedGesture
    
    // 중요한 제스처 실행 시에만 로그
    if (detectedGesture.includes('실행!')) {
      console.log(`🔄 제스처 실행: ${detectedGesture}`)
    }
  }
  
  return gestures
}

// 더 정확한 손가락 상태 분석 (엄격한 기준)
const analyzeFingerStates = (hand: any) => {
  const fingerTips = [4, 8, 12, 16, 20] // 엄지, 검지, 중지, 약지, 소지
  const fingerPips = [3, 6, 10, 14, 18] // 각 손가락의 중간 관절
  const fingerMcps = [2, 5, 9, 13, 17] // 각 손가락의 기준점
  
  const fingerStates = fingerTips.map((tipIndex, index) => {
    const tip = hand[tipIndex]
    const pip = hand[fingerPips[index]]
    const mcp = hand[fingerMcps[index]]
    
    if (tipIndex === 4) { // 엄지는 좌우 방향으로 판단
      const isExtended = tip.x > pip.x // 오른손 기준
      // 엄지는 더 엄격한 기준: 중간 관절과의 거리도 확인
      const distance = Math.abs(tip.x - pip.x)
      return isExtended && distance > 0.05 // 더 엄격한 거리 기준
    } else { 
      // 다른 손가락들은 위아래 방향으로 판단 (더 엄격한 기준)
      const tipAbovePip = tip.y < pip.y
      const pipAboveMcp = pip.y < mcp.y
      
      // 추가 조건: 손가락 끝이 중간 관절보다 충분히 위에 있어야 함
      const tipPipDistance = pip.y - tip.y
      const pipMcpDistance = mcp.y - pip.y
      
      // 손가락이 확실히 펼쳐져 있는지 확인 (거리 기준 추가)
      const isFullyExtended = tipAbovePip && pipAboveMcp && 
                             tipPipDistance > 0.03 && // 끝마디가 충분히 올라가 있음
                             pipMcpDistance > 0.02    // 중간마디도 충분히 올라가 있음
      
      return isFullyExtended
    }
  })
  
  // 디버깅 로그 추가 (처음 몇 번만)
  if (gestureCount.value <= 5) {
    console.log('🔍 손가락 상태 디버깅:', {
      엄지: fingerStates[0] ? '펼침' : '접음',
      검지: fingerStates[1] ? '펼침' : '접음', 
      중지: fingerStates[2] ? '펼침' : '접음',
      약지: fingerStates[3] ? '펼침' : '접음',
      소지: fingerStates[4] ? '펼침' : '접음',
      총개수: fingerStates.filter(Boolean).length
    })
  }
  
  return fingerStates
}

// 손 위치 분석
const analyzeHandPosition = (hand: any) => {
  const wrist = hand[0]
  const middleTip = hand[12]
  
  return {
    vertical: wrist.y < 0.3 ? '상단' : wrist.y > 0.7 ? '하단' : '중앙',
    horizontal: wrist.x < 0.3 ? '좌측' : wrist.x > 0.7 ? '우측' : '중앙',
    isTop: wrist.y < 0.3,
    isBottom: wrist.y > 0.7,
    isLeft: wrist.x < 0.3,
    isRight: wrist.x > 0.7,
    centerDistance: Math.sqrt(Math.pow(wrist.x - 0.5, 2) + Math.pow(wrist.y - 0.5, 2))
  }
}

// 손 움직임 분석 (간단한 버전)
const analyzeHandMovement = (hand: any) => {
  const wrist = hand[0]
  
  // 이전 위치와 비교 (실제로는 이전 프레임 데이터 저장 필요)
  return {
    speed: 0.01, // 임시 값
    direction: 'static'
  }
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

// 향상된 손 랜드마크 연결선 그리기
const drawHandConnections = (ctx: CanvasRenderingContext2D, landmarks: any[], connections: number[][]) => {
  // 각 손가락별로 다른 색상 사용
  const fingerColors = {
    thumb: '#FF6B6B',     // 빨간색 - 엄지
    index: '#4ECDC4',     // 청록색 - 검지
    middle: '#45B7D1',    // 파란색 - 중지
    ring: '#96CEB4',      // 연두색 - 약지
    pinky: '#FFEAA7',     // 노란색 - 소지
    palm: '#DDA0DD'       // 보라색 - 손바닥
  }
  
  ctx.lineWidth = 3
  ctx.lineCap = 'round'
  ctx.lineJoin = 'round'
  
  // 엄지 연결선
  ctx.strokeStyle = fingerColors.thumb
  const thumbConnections = [[0, 1], [1, 2], [2, 3], [3, 4]]
  drawFingerConnections(ctx, landmarks, thumbConnections)
  
  // 검지 연결선
  ctx.strokeStyle = fingerColors.index
  const indexConnections = [[0, 5], [5, 6], [6, 7], [7, 8]]
  drawFingerConnections(ctx, landmarks, indexConnections)
  
  // 중지 연결선
  ctx.strokeStyle = fingerColors.middle
  const middleConnections = [[5, 9], [9, 10], [10, 11], [11, 12]]
  drawFingerConnections(ctx, landmarks, middleConnections)
  
  // 약지 연결선
  ctx.strokeStyle = fingerColors.ring
  const ringConnections = [[9, 13], [13, 14], [14, 15], [15, 16]]
  drawFingerConnections(ctx, landmarks, ringConnections)
  
  // 소지 연결선
  ctx.strokeStyle = fingerColors.pinky
  const pinkyConnections = [[13, 17], [17, 18], [18, 19], [19, 20]]
  drawFingerConnections(ctx, landmarks, pinkyConnections)
  
  // 손바닥 연결선
  ctx.strokeStyle = fingerColors.palm
  const palmConnections = [[0, 17]]
  drawFingerConnections(ctx, landmarks, palmConnections)
}

// 개별 손가락 연결선 그리기 헬퍼 함수
const drawFingerConnections = (ctx: CanvasRenderingContext2D, landmarks: any[], connections: number[][]) => {
  for (const connection of connections) {
    const start = landmarks[connection[0]]
    const end = landmarks[connection[1]]
    
    ctx.beginPath()
    ctx.moveTo(start.x * ctx.canvas.width, start.y * ctx.canvas.height)
    ctx.lineTo(end.x * ctx.canvas.width, end.y * ctx.canvas.height)
    ctx.stroke()
  }
}

// 향상된 손 랜드마크 포인트 그리기
const drawHandLandmarks = (ctx: CanvasRenderingContext2D, landmarks: any[]) => {
  // 관절점 유형별 색상과 크기
  const landmarkStyles = {
    wrist: { color: '#8B4513', size: 8 },      // 손목 - 갈색
    thumb: { color: '#FF6B6B', size: 6 },      // 엄지 - 빨간색
    index: { color: '#4ECDC4', size: 6 },      // 검지 - 청록색
    middle: { color: '#45B7D1', size: 6 },     // 중지 - 파란색
    ring: { color: '#96CEB4', size: 6 },       // 약지 - 연두색
    pinky: { color: '#FFEAA7', size: 6 },      // 소지 - 노란색
    palm: { color: '#DDA0DD', size: 5 }        // 손바닥 - 보라색
  }
  
  // 각 랜드마크별 스타일 지정
  const landmarkTypeMap = [
    'wrist',                                    // 0: 손목
    'thumb', 'thumb', 'thumb', 'thumb',        // 1-4: 엄지
    'index', 'index', 'index', 'index',        // 5-8: 검지
    'middle', 'middle', 'middle', 'middle',    // 9-12: 중지
    'ring', 'ring', 'ring', 'ring',           // 13-16: 약지
    'pinky', 'pinky', 'pinky', 'pinky'        // 17-20: 소지
  ]
  
  for (let i = 0; i < landmarks.length; i++) {
    const landmark = landmarks[i]
    const x = landmark.x * ctx.canvas.width
    const y = landmark.y * ctx.canvas.height
    const style = landmarkStyles[landmarkTypeMap[i]] || landmarkStyles.palm
    
    // 외곽 원 (테두리)
    ctx.fillStyle = '#FFFFFF'
    ctx.beginPath()
    ctx.arc(x, y, style.size + 1, 0, 2 * Math.PI)
    ctx.fill()
    
    // 내부 원 (색상)
    ctx.fillStyle = style.color
    ctx.beginPath()
    ctx.arc(x, y, style.size, 0, 2 * Math.PI)
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
    
    console.log('🖐️ 네이티브 Hands 객체 생성 중...')
    nativeHands = new Hands({
      locateFile: (file: string) => {
        const url = `https://cdn.jsdelivr.net/npm/@mediapipe/hands/${file}`
        console.log(`📁 모델 파일 로드: ${file}`)
        return url
      }
    })
    
    nativeHands.setOptions({
      maxNumHands: 2,
      modelComplexity: 1,
      minDetectionConfidence: 0.3, // 더 낮은 임계값
      minTrackingConfidence: 0.3   // 더 낮은 임계값
    })
    
    // Vue ref는 표시용으로만 설정
    hands.value = 'initialized'
    
    console.log('⚙️ 네이티브 Hands 설정 완료 (임계값: 0.3)')
    console.log('📊 Hands 설정 정보:', {
      maxNumHands: 2,
      modelComplexity: 1,
      minDetectionConfidence: 0.3,
      minTrackingConfidence: 0.3
    })
    
    // 손 연결선 정의 (MediaPipe HAND_CONNECTIONS)
    const HAND_CONNECTIONS = [
      [0, 1], [1, 2], [2, 3], [3, 4],        // 엄지
      [0, 5], [5, 6], [6, 7], [7, 8],        // 검지
      [0, 17], [5, 9], [9, 10], [10, 11], [11, 12], // 중지
      [9, 13], [13, 14], [14, 15], [15, 16], // 약지
      [13, 17], [17, 18], [18, 19], [19, 20] // 소지
    ]
    
    nativeHands.onResults((results: any) => {
      // 프레임 처리 카운터
      gestureCount.value++
      
      // 로그 최소화 - 처음 3번, 그 후 100번마다만
      const shouldLog = gestureCount.value <= 3 || gestureCount.value % 100 === 0
      
      if (shouldLog) {
        console.log(`📸 프레임 처리: ${gestureCount.value}번째`)
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
              
              // 손별 디버그 정보 (매우 제한적)
              if (gestureCount.value <= 2) {
                console.log(`🖐️ 손 ${i + 1} 정보:`, {
                  landmarkCount: landmarks.length,
                  wristPos: `(${Math.round(landmarks[0].x * 100)}%, ${Math.round(landmarks[0].y * 100)}%)`
                })
              }
            }
            
            // 제스처 분석 및 액션 실행
            const gestures = analyzeGestureAndPerformAction(results.multiHandLandmarks)
            detectedGestures.value = gestures
            
            // 제스처 로그를 제거하여 성능 최적화
            
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
              console.log('🔧 디버그 정보:', {
                frameCount: gestureCount.value,
                hasResults: !!results,
                resultKeys: results ? Object.keys(results) : 'no results',
                videoPlaying: !videoElement.value?.paused,
                videoTime: videoElement.value?.currentTime,
                handsOptions: hands.value ? 'initialized' : 'not initialized'
              })
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
    
    // DOM에서 직접 비디오 엘리먼트 가져오기 (Vue 시스템 완전 우회)
    const videoElements = document.querySelectorAll('video')
    const nativeVideoElement = videoElements[0] as HTMLVideoElement
    
    if (nativeVideoElement) {
      console.log('📷 DOM에서 직접 비디오 엘리먼트 가져오기 성공')
      console.log('📐 네이티브 비디오 크기:', nativeVideoElement.videoWidth, 'x', nativeVideoElement.videoHeight)
      
      let frameCount = 0
      let isProcessing = false
      
      // Canvas 생성 방식도 변경 (MediaPipe용 임시 캔버스)
      const tempCanvas = document.createElement('canvas')
      tempCanvas.width = 640
      tempCanvas.height = 480
      const tempCtx = tempCanvas.getContext('2d')
      
      // 수동 프레임 처리 함수 (완전히 네이티브 방식) - 성능 최적화
      const processFrame = async () => {
        if (!isGestureActive.value || !nativeHands || isProcessing) {
          return
        }
        
        isProcessing = true
        frameCount++
        
        // 프레임 건너뛰기로 성능 최적화 (3프레임마다 1번만 처리)
        if (frameCount % 3 !== 0) {
          isProcessing = false
          requestAnimationFrame(processFrame)
          return
        }
        
        try {
          // 비디오 상태 체크
          if (nativeVideoElement.videoWidth === 0 || nativeVideoElement.videoHeight === 0) {
            if (frameCount % 30 === 0) {
              console.warn('⚠️ 네이티브 비디오 크기가 0입니다:', {
                videoWidth: nativeVideoElement.videoWidth,
                videoHeight: nativeVideoElement.videoHeight,
                readyState: nativeVideoElement.readyState
              })
            }
            isProcessing = false
            requestAnimationFrame(processFrame)
            return
          }
          
          // 추가 안전성 체크
          if (nativeVideoElement.readyState < 2) { // HAVE_CURRENT_DATA
            if (frameCount % 30 === 0) {
              console.warn('⚠️ 네이티브 비디오가 아직 준비되지 않음:', nativeVideoElement.readyState)
            }
            isProcessing = false
            requestAnimationFrame(processFrame)
            return
          }
          
          // 임시 캔버스에 비디오 프레임 그리기
          if (tempCtx) {
            tempCtx.drawImage(nativeVideoElement, 0, 0, tempCanvas.width, tempCanvas.height)
            
            // 네이티브 MediaPipe에 캔버스 전송 (프록시 없음)
            await nativeHands.send({ image: tempCanvas })
            
            // 프레임 전송 확인 (매우 제한적)
            if (frameCount <= 2) {
              console.log(`📸 프레임 처리: ${frameCount}번째`)
            }
          }
          
        } catch (frameError) {
          console.error('⚠️ 네이티브 프레임 처리 오류:', frameError)
          
          // 오류 발생 시 잠시 대기
          setTimeout(() => {
            isProcessing = false
            if (isGestureActive.value) {
              requestAnimationFrame(processFrame)
            }
          }, 100) // 더 짧은 대기 시간
          return
        }
        
        isProcessing = false
        
        // 다음 프레임 처리 예약
        if (isGestureActive.value) {
          requestAnimationFrame(processFrame)
        }
      }
      
      // 프레임 처리 시작
      console.log('🎬 캔버스 기반 프레임 처리 시작...')
      requestAnimationFrame(processFrame)
      
      // camera.value를 null로 설정 (Camera 객체 사용하지 않음)
      camera.value = {
        start: () => Promise.resolve(),
        stop: () => Promise.resolve()
      }
      
      console.log('📹 캔버스 기반 프레임 처리 연결 완료')
      console.log('🎬 처리 해상도: 640x480')
      
    } else {
      throw new Error('❌ DOM에서 비디오 엘리먼트를 찾을 수 없음')
    }
    
    console.log('✅ MediaPipe 손 인식 초기화 완료!')
    console.log('👋 이제 손을 카메라 앞에 보여주세요!')
    console.log('🔍 손이 감지되지 않으면 다음을 확인하세요:')
    console.log('  - 손을 카메라 중앙에 위치')
    console.log('  - 조명이 충분한지 확인')
    console.log('  - 손바닥이 카메라를 향하도록')
    
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
// 프레임 처리 시작 함수 (재사용 가능)
const startFrameProcessing = async () => {
  console.log('🎬 프레임 처리 재시작 중...')
  
  const videoElements = document.querySelectorAll('video')
  const nativeVideoElement = videoElements[0] as HTMLVideoElement
  
  if (!nativeVideoElement) {
    console.error('❌ 비디오 엘리먼트를 찾을 수 없습니다.')
    return
  }
  
  console.log('📷 비디오 엘리먼트 재확인 완료')
  console.log('📐 비디오 크기:', nativeVideoElement.videoWidth, 'x', nativeVideoElement.videoHeight)
  
  let frameCount = 0
  let isProcessing = false
  
  // Canvas 생성 (MediaPipe용 임시 캔버스)
  const tempCanvas = document.createElement('canvas')
  tempCanvas.width = 640
  tempCanvas.height = 480
  const tempCtx = tempCanvas.getContext('2d')
  
  // 프레임 처리 함수
  const processFrame = async () => {
    if (!isGestureActive.value || !nativeHands || isProcessing) {
      return
    }
    
    isProcessing = true
    frameCount++
    
    // 프레임 건너뛰기로 성능 최적화 (3프레임마다 1번만 처리)
    if (frameCount % 3 !== 0) {
      isProcessing = false
      requestAnimationFrame(processFrame)
      return
    }
    
    try {
      // 비디오 상태 체크
      if (nativeVideoElement.videoWidth === 0 || nativeVideoElement.videoHeight === 0) {
        if (frameCount % 30 === 0) {
          console.warn('⚠️ 비디오 크기가 0입니다:', {
            videoWidth: nativeVideoElement.videoWidth,
            videoHeight: nativeVideoElement.videoHeight,
            readyState: nativeVideoElement.readyState
          })
        }
        isProcessing = false
        requestAnimationFrame(processFrame)
        return
      }
      
      // 추가 안전성 체크
      if (nativeVideoElement.readyState < 2) { // HAVE_CURRENT_DATA
        if (frameCount % 30 === 0) {
          console.warn('⚠️ 비디오가 아직 준비되지 않음:', nativeVideoElement.readyState)
        }
        isProcessing = false
        requestAnimationFrame(processFrame)
        return
      }
      
      // 임시 캔버스에 비디오 프레임 그리기
      if (tempCtx) {
        tempCtx.drawImage(nativeVideoElement, 0, 0, tempCanvas.width, tempCanvas.height)
        
        // 네이티브 MediaPipe에 캔버스 전송 (프록시 없음)
        await nativeHands.send({ image: tempCanvas })
        
        // 프레임 전송 확인 (매우 제한적)
        if (frameCount <= 2) {
          console.log(`📸 프레임 재시작: ${frameCount}번째`)
        }
      }
      
    } catch (frameError) {
      console.error('⚠️ 프레임 처리 오류:', frameError)
      
      // 오류 발생 시 잠시 대기
      setTimeout(() => {
        isProcessing = false
        if (isGestureActive.value) {
          requestAnimationFrame(processFrame)
        }
      }, 100)
      return
    }
    
    isProcessing = false
    
    // 다음 프레임 처리 예약
    if (isGestureActive.value) {
      requestAnimationFrame(processFrame)
    }
  }
  
  // 프레임 처리 시작
  console.log('🎬 프레임 처리 재시작 완료')
  requestAnimationFrame(processFrame)
}

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
    console.log('- 비디오 재생 중:', !videoElement.value?.paused)
    console.log('- 비디오 준비 상태:', videoElement.value?.readyState)
    
    if (!isCameraActive.value) {
      console.error('❌ 먼저 카메라를 시작해주세요!')
      isGestureActive.value = false
      return
    }
    
    if (!videoElement.value || videoElement.value.videoWidth === 0) {
      console.error('❌ 비디오 스트림이 준비되지 않았습니다!')
      isGestureActive.value = false
      return
    }
    
    console.log('🖐️ 손 제스처 인식 활성화')
    
    try {
      // 백엔드 연결 상태 먼저 확인
      console.log('🔍 백엔드 서버 연결 상태 확인 중...')
      const isBackendConnected = await checkBackendConnection()
      
      if (!isBackendConnected) {
        console.warn('⚠️ 백엔드 서버에 연결할 수 없습니다. 시뮬레이션 모드로 동작합니다.')
      }
      
      // MediaPipe 초기화
      if (!nativeHands) {
        console.log('🤖 네이티브 MediaPipe 첫 초기화 시작...')
        await initializeMediaPipe()
      } else {
        console.log('♻️ 기존 네이티브 MediaPipe 재사용 - 프레임 처리 재시작')
        // 제스처 재시작 시 프레임 처리도 다시 시작
        await startFrameProcessing()
      }
      
      console.log('🎉 제스처 인식 시작 완료!')
      console.log('📍 새로운 사용법:')
      console.log('  👆 검지만 펼침 = 마우스 포인터 이동 + 까딱으로 좌클릭')
      console.log('  🖕 중지만 펼침 = 까딱으로 우클릭')
      console.log('  ✌️ 검지+중지 = 위아래로 스크롤')
      
      console.log('🔧 환경 설정 정보:')
      console.log(`  - API Base URL: ${import.meta.env.VITE_API_BASE_URL}`)
      console.log(`  - WS Base URL: ${import.meta.env.VITE_WS_BASE_URL}`)
      console.log(`  - NODE_ENV: ${import.meta.env.NODE_ENV}`)
      console.log(`  - MODE: ${import.meta.env.MODE}`)
      
      if (!isBackendConnected) {
        console.log('🔧 참고: 백엔드 연결 실패로 브라우저 시뮬레이션 모드로 동작합니다.')
      }
      
    } catch (error) {
      console.error('❌ MediaPipe 초기화 실패:', error)
      isGestureActive.value = false
      errorMessage.value = `MediaPipe 초기화 실패: ${error.message}`
    }
    
  } else {
    console.log('🛑 제스처 인식 비활성화')
    
    // 수동 프레임 처리 중지 (isGestureActive.value = false로 이미 중지됨)
    console.log('📷 수동 프레임 처리 중지 완료')
    
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
