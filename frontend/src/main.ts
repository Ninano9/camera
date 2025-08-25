import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './router'
import App from './App.vue'
import './style.css'

// Pinia 스토어 생성
const pinia = createPinia()

// 앱 생성 및 플러그인 설치
const app = createApp(App)

app.use(pinia)
app.use(router)

app.mount('#app')
