<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-card">
        <div class="register-header">
          <h1>회원가입</h1>
          <p>제스처 인식 플랫폼에 가입하세요</p>
        </div>
        
        <form @submit.prevent="handleRegister" class="register-form">
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
            <label for="displayName" class="form-label">닉네임</label>
            <input
              id="displayName"
              v-model="form.displayName"
              type="text"
              class="form-input"
              placeholder="닉네임을 입력하세요"
              :disabled="isLoading"
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
          
          <div class="form-group">
            <label for="confirmPassword" class="form-label">비밀번호 확인</label>
            <input
              id="confirmPassword"
              v-model="form.confirmPassword"
              type="password"
              class="form-input"
              placeholder="비밀번호를 다시 입력하세요"
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
            :disabled="isLoading || !isFormValid"
          >
            <span v-if="isLoading" class="loading">
              <span class="spinner"></span>
              가입 중...
            </span>
            <span v-else>회원가입</span>
          </button>
        </form>
        
        <div class="register-footer">
          <p>
            이미 계정이 있으신가요?
            <router-link to="/login" class="link">로그인</router-link>
          </p>
          <router-link to="/" class="link">홈으로 돌아가기</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const form = reactive({
  email: '',
  displayName: '',
  password: '',
  confirmPassword: ''
})

const isLoading = ref(false)
const error = ref<string | null>(null)

const isFormValid = computed(() => {
  return form.email && 
         form.password && 
         form.confirmPassword && 
         form.password === form.confirmPassword
})

const handleRegister = async () => {
  if (isLoading.value || !isFormValid.value) return
  
  if (form.password !== form.confirmPassword) {
    error.value = '비밀번호가 일치하지 않습니다.'
    return
  }
  
  isLoading.value = true
  error.value = null
  
  try {
    // 임시로 성공 처리
    console.log('회원가입:', form)
    await router.push('/login')
  } catch (err) {
    error.value = '회원가입에 실패했습니다.'
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 1rem;
}

.register-container {
  width: 100%;
  max-width: 400px;
}

.register-card {
  background: var(--bg-color);
  border-radius: 1rem;
  padding: 2rem;
  box-shadow: var(--shadow-lg);
}

.register-header {
  text-align: center;
  margin-bottom: 2rem;
}

.register-header h1 {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
  color: var(--text-color);
}

.register-header p {
  color: var(--text-secondary);
  font-size: 0.875rem;
}

.register-form {
  margin-bottom: 2rem;
}

.btn-full {
  width: 100%;
  margin-top: 1rem;
}

.register-footer {
  text-align: center;
  font-size: 0.875rem;
}

.register-footer p {
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
  .register-card {
    padding: 1.5rem;
  }
  
  .register-header h1 {
    font-size: 1.75rem;
  }
}
</style>
