<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-card">
        <div class="login-header">
          <h1>로그인</h1>
          <p>제스처 인식 플랫폼에 오신 것을 환영합니다</p>
        </div>
        
        <form @submit.prevent="handleLogin" class="login-form">
          <div class="form-group">
            <label for="email" class="form-label">이메일</label>
            <input
              id="email"
              v-model="form.email"
              type="email"
              class="form-input"
              placeholder="이메일을 입력하세요"
              :disabled="isLoading"
              required
            >
          </div>
          
          <div class="form-group">
            <label for="password" class="form-label">비밀번호</label>
            <input
              id="password"
              v-model="form.password"
              type="password"
              class="form-input"
              placeholder="비밀번호를 입력하세요"
              :disabled="isLoading"
              required
            >
          </div>
          
          <div v-if="error" class="error-message">
            {{ error }}
          </div>
          
          <button
            type="submit"
            class="btn btn-primary btn-full"
            :disabled="isLoading"
          >
            <span v-if="isLoading" class="loading">
              <span class="spinner"></span>
              로그인 중...
            </span>
            <span v-else>로그인</span>
          </button>
        </form>
        
        <div class="login-footer">
          <p>
            계정이 없으신가요?
            <router-link to="/register" class="link">회원가입</router-link>
          </p>
          <router-link to="/" class="link">홈으로 돌아가기</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const form = reactive({
  email: '',
  password: ''
})

const isLoading = ref(false)
const error = ref<string | null>(null)

const handleLogin = async () => {
  if (isLoading.value) return
  
  isLoading.value = true
  error.value = null
  
  try {
    await authStore.login(form)
    
    // 리다이렉트 URL이 있으면 해당 페이지로, 없으면 대시보드로
    const redirectTo = route.query.redirect as string || '/dashboard'
    await router.push(redirectTo)
  } catch (err) {
    error.value = err as string
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 1rem;
}

.login-container {
  width: 100%;
  max-width: 400px;
}

.login-card {
  background: var(--bg-color);
  border-radius: 1rem;
  padding: 2rem;
  box-shadow: var(--shadow-lg);
}

.login-header {
  text-align: center;
  margin-bottom: 2rem;
}

.login-header h1 {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
  color: var(--text-color);
}

.login-header p {
  color: var(--text-secondary);
  font-size: 0.875rem;
}

.login-form {
  margin-bottom: 2rem;
}

.btn-full {
  width: 100%;
  margin-top: 1rem;
}

.login-footer {
  text-align: center;
  font-size: 0.875rem;
}

.login-footer p {
  margin-bottom: 0.5rem;
  color: var(--text-secondary);
}

.link {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: 500;
}

.link:hover {
  text-decoration: underline;
}

@media (max-width: 480px) {
  .login-card {
    padding: 1.5rem;
  }
  
  .login-header h1 {
    font-size: 1.75rem;
  }
}
</style>
