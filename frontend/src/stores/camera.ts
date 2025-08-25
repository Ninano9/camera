import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import type { CameraSettings, HandGestureData, EyeData, GazeData } from '../types';

export const useCameraStore = defineStore('camera', () => {
  // State
  const isInitialized = ref(false);
  const isStreaming = ref(false);
  const isProcessing = ref(false);
  const error = ref<string | null>(null);
  
  const videoElement = ref<HTMLVideoElement | null>(null);
  const stream = ref<MediaStream | null>(null);
  const devices = ref<MediaDeviceInfo[]>([]);
  
  const settings = ref<CameraSettings>({
    deviceId: undefined,
    width: 640,
    height: 480,
    fps: 30
  });
  
  // 인식 결과
  const handGestureData = ref<HandGestureData | null>(null);
  const eyeData = ref<EyeData | null>(null);
  const gazeData = ref<GazeData | null>(null);
  
  // 성능 메트릭
  const fps = ref(0);
  const processingTime = ref(0);
  const lastFrameTime = ref(0);

  // Getters
  const canProcess = computed(() => isInitialized.value && isStreaming.value && !isProcessing.value);
  const currentDeviceName = computed(() => {
    if (!settings.value.deviceId) return '기본 카메라';
    const device = devices.value.find(d => d.deviceId === settings.value.deviceId);
    return device?.label || '알 수 없는 카메라';
  });

  // Actions
  const initializeCamera = async (): Promise<void> => {
    error.value = null;
    
    try {
      // 카메라 권한 요청 및 장치 목록 조회
      await getDevices();
      isInitialized.value = true;
      console.log('카메라 초기화 완료');
    } catch (err: any) {
      error.value = err.message || '카메라 초기화에 실패했습니다.';
      throw error.value;
    }
  };

  const getDevices = async (): Promise<void> => {
    try {
      // 먼저 권한 요청
      await navigator.mediaDevices.getUserMedia({ video: true });
      
      // 장치 목록 조회
      const deviceList = await navigator.mediaDevices.enumerateDevices();
      devices.value = deviceList.filter(device => device.kind === 'videoinput');
      
      // 기본 장치 설정
      if (devices.value.length > 0 && !settings.value.deviceId) {
        settings.value.deviceId = devices.value[0].deviceId;
      }
    } catch (err: any) {
      throw new Error('카메라 장치에 접근할 수 없습니다.');
    }
  };

  const startStream = async (): Promise<void> => {
    if (isStreaming.value) return;

    error.value = null;
    
    try {
      const constraints: MediaStreamConstraints = {
        video: {
          deviceId: settings.value.deviceId ? { exact: settings.value.deviceId } : undefined,
          width: { ideal: settings.value.width },
          height: { ideal: settings.value.height },
          frameRate: { ideal: settings.value.fps }
        }
      };

      stream.value = await navigator.mediaDevices.getUserMedia(constraints);
      
      if (videoElement.value) {
        videoElement.value.srcObject = stream.value;
        await videoElement.value.play();
      }
      
      isStreaming.value = true;
      console.log('카메라 스트림 시작');
    } catch (err: any) {
      error.value = '카메라 스트림을 시작할 수 없습니다.';
      throw error.value;
    }
  };

  const stopStream = (): void => {
    if (stream.value) {
      stream.value.getTracks().forEach(track => track.stop());
      stream.value = null;
    }
    
    if (videoElement.value) {
      videoElement.value.srcObject = null;
    }
    
    isStreaming.value = false;
    isProcessing.value = false;
    console.log('카메라 스트림 중지');
  };

  const setVideoElement = (element: HTMLVideoElement): void => {
    videoElement.value = element;
  };

  const updateSettings = async (newSettings: Partial<CameraSettings>): Promise<void> => {
    const wasStreaming = isStreaming.value;
    
    if (wasStreaming) {
      stopStream();
    }
    
    settings.value = { ...settings.value, ...newSettings };
    
    if (wasStreaming) {
      await startStream();
    }
  };

  const updateHandGestureData = (data: HandGestureData | null): void => {
    handGestureData.value = data;
  };

  const updateEyeData = (data: EyeData | null): void => {
    eyeData.value = data;
  };

  const updateGazeData = (data: GazeData | null): void => {
    gazeData.value = data;
  };

  const updatePerformanceMetrics = (newFps: number, newProcessingTime: number): void => {
    fps.value = newFps;
    processingTime.value = newProcessingTime;
    lastFrameTime.value = Date.now();
  };

  const setProcessing = (processing: boolean): void => {
    isProcessing.value = processing;
  };

  const clearError = (): void => {
    error.value = null;
  };

  // Cleanup
  const cleanup = (): void => {
    stopStream();
    isInitialized.value = false;
    handGestureData.value = null;
    eyeData.value = null;
    gazeData.value = null;
    fps.value = 0;
    processingTime.value = 0;
  };

  return {
    // State
    isInitialized,
    isStreaming,
    isProcessing,
    error,
    videoElement,
    stream,
    devices,
    settings,
    handGestureData,
    eyeData,
    gazeData,
    fps,
    processingTime,
    lastFrameTime,
    
    // Getters
    canProcess,
    currentDeviceName,
    
    // Actions
    initializeCamera,
    getDevices,
    startStream,
    stopStream,
    setVideoElement,
    updateSettings,
    updateHandGestureData,
    updateEyeData,
    updateGazeData,
    updatePerformanceMetrics,
    setProcessing,
    clearError,
    cleanup
  };
});
