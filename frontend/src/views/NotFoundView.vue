<template>
  <div class="not-found">
    <div class="container">
      <div class="not-found-content">
        <h1>404</h1>
        <h2>페이지를 찾을 수 없습니다</h2>
        <p>요청하신 페이지가 존재하지 않거나 이동되었습니다.</p>
        <router-link to="/" class="btn btn-primary">홈으로 돌아가기</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// 404 페이지 컴포넌트
</script>

<style scoped>
.not-found {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--bg-secondary);
}

.not-found-content {
  text-align: center;
  background: var(--bg-color);
  padding: 4rem 2rem;
  border-radius: 1rem;
  box-shadow: var(--shadow);
}

.not-found-content h1 {
  font-size: 6rem;
  font-weight: 700;
  color: var(--primary-color);
  margin-bottom: 1rem;
}

.not-found-content h2 {
  font-size: 2rem;
  color: var(--text-color);
  margin-bottom: 1rem;
}

.not-found-content p {
  color: var(--text-secondary);
  margin-bottom: 2rem;
  font-size: 1.1rem;
}
</style>
