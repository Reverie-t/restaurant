<template>
  <div class="login-page">
    <!-- Left: brand panel -->
    <div class="login-brand">
      <div class="brand-content">
        <div class="brand-icon">
          <el-icon :size="32"><KnifeFork /></el-icon>
        </div>
        <h1 class="brand-title">餐馆管理系统</h1>
        <p class="brand-desc">Restaurant Management</p>
        <div class="brand-divider"></div>
        <p class="brand-tagline">高效运营，从这里开始</p>
      </div>
    </div>

    <!-- Right: form -->
    <div class="login-form-wrap">
      <div class="form-card">
        <h2>欢迎回来</h2>
        <p class="form-hint">请登录您的账号以继续</p>

        <el-form :model="form" class="login-form">
          <el-form-item>
            <el-input
              v-model="form.username"
              placeholder="用户名"
              size="large"
              :prefix-icon="User"
            />
          </el-form-item>

          <el-form-item>
            <el-input
              v-model="form.password"
              type="password"
              placeholder="密码"
              size="large"
              show-password
              :prefix-icon="Lock"
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              class="login-btn"
              @click="handleLogin"
              :loading="loading"
            >
              登 录
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { login } from '@/api/login'
import { User, Lock, KnifeFork } from '@element-plus/icons-vue'

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
      router.push("/dashboard")
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
  display: flex;
}

/* ── Left brand panel ── */
.login-brand {
  width: 420px;
  background: var(--surface-deep);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  flex-shrink: 0;
}

/* Signature amber line */
.login-brand::after {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 3px;
  height: 100%;
  background: var(--accent);
}

.brand-content {
  text-align: center;
  padding: var(--sp-8);
}

.brand-icon {
  width: 64px;
  height: 64px;
  margin: 0 auto var(--sp-6);
  background: var(--accent);
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.brand-title {
  color: var(--text-inverse);
  font-size: 22px;
  font-weight: 600;
  letter-spacing: 0.04em;
  margin-bottom: var(--sp-2);
}

.brand-desc {
  color: rgba(240, 237, 232, 0.4);
  font-size: 13px;
  letter-spacing: 0.1em;
  text-transform: uppercase;
}

.brand-divider {
  width: 40px;
  height: 2px;
  background: var(--accent);
  margin: var(--sp-6) auto;
  border-radius: 1px;
}

.brand-tagline {
  color: rgba(240, 237, 232, 0.5);
  font-size: 14px;
}

/* ── Right form area ── */
.login-form-wrap {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--surface-warm);
}

.form-card {
  width: 380px;
}

.form-card h2 {
  font-size: 24px;
  font-weight: 600;
  color: var(--text-1);
  margin-bottom: var(--sp-2);
}

.form-hint {
  color: var(--text-3);
  font-size: 14px;
  margin-bottom: var(--sp-8);
}

.login-form :deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px var(--border) inset;
  border-radius: var(--radius-md);
}

.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c0bbb4 inset;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px var(--accent) inset;
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 15px;
  margin-top: var(--sp-2);
}
</style>
