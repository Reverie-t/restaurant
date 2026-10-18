<template>
  <div class="login-page">
    <div class="login-card">
      <button
        class="back-btn"
        @click="router.push('/')"
      >
        ← 返回首页
      </button>

      <div class="eyebrow">RESTAURANT MANAGEMENT</div>
      <h1 class="login-title">餐馆管理系统</h1>
      <p class="login-desc">请登录您的账号以继续</p>

      <el-form :model="form" class="login-form">
        <el-form-item>
          <el-input
            v-model="form.username"
            placeholder="用户名"
            size="large"
          />
        </el-form-item>

        <el-form-item>
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            size="large"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item>
          <button
            class="login-btn"
            @click="handleLogin"
            :disabled="loading"
          >
            {{ loading ? '登录中...' : '登 录' }}
          </button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { login } from '@/api/login'

const router = useRouter()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const handleLogin = async () => {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }

  loading.value = true

  try {
    const res = await login(form)

    if (res.data.code === 200) {
      localStorage.setItem("token", res.data.data.token)
      localStorage.setItem("user", JSON.stringify(res.data.data.user))
      ElMessage.success("登录成功")
      router.push("/admin/dashboard")
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (e) {
    ElMessage.error(e.response?.data?.message || e.message)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  width: 100vw;
  height: 100vh;
  background: var(--surface-page);
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-card {
  width: 380px;
  background: var(--surface-card);
  border: 1px solid var(--border);
  padding: 48px 40px 40px;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 28px;
  padding: 0;
  border: none;
  background: none;
  color: var(--text-3);
  font-size: 13px;
  cursor: pointer;
  transition: color 0.2s var(--ease);
}

.back-btn:hover {
  color: var(--text-1);
}

.eyebrow {
  font-size: 11px;
  letter-spacing: 3px;
  color: var(--text-3);
  margin-bottom: 8px;
  font-weight: 500;
}

.login-title {
  font-family: "Cormorant Garamond", serif;
  font-size: 32px;
  font-weight: 500;
  color: var(--text-1);
  margin-bottom: 6px;
  line-height: 1.2;
}

.login-desc {
  font-size: 14px;
  color: var(--text-3);
  margin-bottom: 36px;
}

/* Input style overrides */
.login-form :deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px var(--border) inset;
  border-radius: 0;
  background: var(--surface-card);
}

.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--text-3) inset;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px var(--text-1) inset;
}

.login-form :deep(.el-input__inner) {
  color: var(--text-1);
  font-size: 14px;
}

.login-form :deep(.el-input__inner::placeholder) {
  color: var(--text-4);
}

/* Primary button — dark wood */
.login-btn {
  width: 100%;
  height: 44px;
  background: var(--btn-primary);
  color: #fff;
  border: none;
  cursor: pointer;
  font-size: 14px;
  letter-spacing: 1px;
  margin-top: 4px;
  transition: background 0.2s var(--ease);
}

.login-btn:hover {
  background: var(--btn-primary-hover);
}

.login-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
