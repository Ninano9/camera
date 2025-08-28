import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src'),
    },
  },
  server: {
    port: 5173,
    host: true,
    cors: true
  },
  preview: {
    port: 4173,
    host: true,
    allowedHosts: ['camera-frontend-0gzf.onrender.com', '.onrender.com']
  },
  build: {
    outDir: 'dist',
    sourcemap: false,
    rollupOptions: {
      output: {
        manualChunks: {
          vendor: ['vue', 'vue-router', 'pinia'],
          ml: ['@tensorflow/tfjs', '@mediapipe/tasks-vision'],
          utils: ['axios']
        }
      }
    }
  },
  optimizeDeps: {
    include: ['vue', 'vue-router', 'pinia', 'axios'],
    exclude: ['@tensorflow/tfjs', '@mediapipe/tasks-vision']
  },
  define: {
    'process.env': {},
    __VUE_OPTIONS_API__: true,
    __VUE_PROD_DEVTOOLS__: false
  },
  // 로컬 개발용 환경변수 설정
  envPrefix: 'VITE_',
  // 개발 서버에서 환경변수 기본값 설정
  server: {
    port: 5173,
    host: true,
    cors: true
  }
})