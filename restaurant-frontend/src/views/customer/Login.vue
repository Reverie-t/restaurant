<template>
  <div class="login-page">
    <div class="login-card">
      <button class="back-btn" @click="router.push('/')">
        <el-icon><ArrowLeft /></el-icon>
        返回首页
      </button>

      <div class="eyebrow">CUSTOMER LOGIN</div>
      <h1 class="login-title">顾客登录</h1>
      <p class="login-desc">登录后即可下单和查看订单</p>

      <el-form :model="form" class="login-form">
        <el-form-item>
          <el-input
            v-model="form.phone"
            placeholder="手机号"
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

        <div class="form-links">
          <router-link to="/customer/register">还没有账号？去注册</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { customerLogin } from '@/api/customer'

const router = useRouter()
const loading = ref(false)

const form = reactive({
  phone: '',
  password: ''
})

const handleLogin = async () => {
  if (!form.phone || !form.password) {
    ElMessage.warning('请输入手机号和密码')
    return
  }

  loading.value = true

  try {
    const res = await customerLogin(form)

    if (res.data.code === 200) {
      localStorage.setItem('customer', JSON.stringify(res.data.data))
      ElMessage.success('登录成功')

      const redirect = router.currentRoute.value.query.redirect || '/'
      router.push(redirect)
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '登录失败，请重试')
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
  position: relative;
}

.back-btn {
  position: absolute;
  top: 20px;
  left: 20px;
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: var(--text-3);
  background: none;
  border: none;
  cursor: pointer;
  transition: color 0.2s;
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

.form-links {
  text-align: center;
  margin-top: 8px;
}

.form-links a {
  color: var(--text-3);
  font-size: 13px;
  text-decoration: none;
  border-bottom: 1px solid var(--border-faint);
  padding-bottom: 1px;
  transition: color 0.2s;
}

.form-links a:hover {
  color: var(--text-1);
}
</style>
