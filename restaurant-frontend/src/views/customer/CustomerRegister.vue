<template>

  <div class="register-page">

    <div class="register-card">

      <!-- 返回首页 -->

      <button
        class="back-btn"
        @click="router.push('/')"
      >

        <el-icon>
          <ArrowLeft />
        </el-icon>

        返回首页

      </button>


      <div class="eyebrow">
        CUSTOMER REGISTER
      </div>


      <h1 class="register-title">
        注册账号
      </h1>


      <p class="register-desc">
        创建账号后即可登录并进行点餐
      </p>


      <el-form
        :model="form"
        class="register-form"
        @submit.prevent="register"
      >

        <!-- 手机号 -->

        <el-form-item>

          <el-input
            v-model="form.phone"
            placeholder="手机号"
            size="large"
            maxlength="11"
          />

        </el-form-item>


        <!-- 密码 -->

        <el-form-item>

          <el-input
            v-model="form.password"
            type="password"
            placeholder="设置密码"
            size="large"
            show-password
          />

        </el-form-item>


        <!-- 确认密码 -->

        <el-form-item>

          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="再次输入密码"
            size="large"
            show-password
            @keyup.enter="register"
          />

        </el-form-item>


        <!-- 注册 -->

        <el-form-item>

          <button
            type="submit"
            class="register-btn"
          >
            注 册
          </button>

        </el-form-item>

      </el-form>


      <!-- 登录 -->

      <div class="login-link">

        <span>
          已有账号？
        </span>

        <button
          type="button"
          @click="goLogin"
        >
          返回登录
        </button>

      </div>

    </div>

  </div>

</template>


<script setup>

import { reactive } from "vue"

import { useRouter } from "vue-router"

import { ElMessage } from "element-plus"

import {
  ArrowLeft
} from "@element-plus/icons-vue"

import {
  customerRegister
} from "@/api/customer"


const router =
  useRouter()


const form =
  reactive({

    phone: "",

    password: "",

    confirmPassword: ""

  })


async function register() {

  // =========================
  // 手机号
  // =========================

  if (!form.phone) {

    ElMessage.warning(
      "请输入手机号"
    )

    return

  }


  if (
    !/^1[3-9]\d{9}$/.test(
      form.phone
    )
  ) {

    ElMessage.warning(
      "请输入正确的手机号"
    )

    return

  }


  // =========================
  // 密码
  // =========================

  if (!form.password) {

    ElMessage.warning(
      "请输入密码"
    )

    return

  }


  if (
    form.password.length < 6
  ) {

    ElMessage.warning(
      "密码长度不能少于6位"
    )

    return

  }


  // =========================
  // 确认密码
  // =========================

  if (!form.confirmPassword) {

    ElMessage.warning(
      "请再次输入密码"
    )

    return

  }


  if (
    form.password !==
    form.confirmPassword
  ) {

    ElMessage.warning(
      "两次输入的密码不一致"
    )

    return

  }


  try {

    const res =
      await customerRegister(
        form
      )


    if (
      res.data.code === 200
    ) {

      ElMessage.success(
        "注册成功，请登录"
      )


      // 注册成功 → 登录页
      router.push({
        path: "/customer/login"
      })

    } else {

      ElMessage.error(
        res.data.message ||
        "注册失败"
      )

    }

  } catch (error) {

    console.error(
      "注册失败",
      error
    )

    ElMessage.error(
      error?.response?.data?.message ||
      "注册失败"
    )

  }

}


function goLogin() {

  router.push(
    "/customer/login"
  )

}

</script>


<style scoped>

.register-page {

  width: 100vw;

  height: 100vh;

  background: var(--surface-page);

  display: flex;

  align-items: center;

  justify-content: center;

}


.register-card {

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


.register-title {

  font-family:
    "Cormorant Garamond",
    serif;

  font-size: 32px;

  font-weight: 500;

  color: var(--text-1);

  margin: 0 0 6px;

}


.register-desc {

  font-size: 14px;

  color: var(--text-3);

  margin-bottom: 36px;

}


.register-form
:deep(.el-input__wrapper) {

  box-shadow:
    0 0 0 1px
    var(--border)
    inset;

  border-radius: 0;

  background:
    var(--surface-card);

}


.register-form
:deep(.el-input__wrapper:hover) {

  box-shadow:
    0 0 0 1px
    var(--text-3)
    inset;

}


.register-form
:deep(.el-input__wrapper.is-focus) {

  box-shadow:
    0 0 0 1px
    var(--text-1)
    inset;

}


.register-form
:deep(.el-input__inner) {

  color: var(--text-1);

  font-size: 14px;

}


.register-btn {

  width: 100%;

  height: 44px;

  background:
    var(--btn-primary);

  color: #fff;

  border: none;

  cursor: pointer;

  font-size: 14px;

  letter-spacing: 1px;

  margin-top: 4px;

}


.register-btn:hover {

  background:
    var(--btn-primary-hover);

}


.login-link {

  margin-top: 20px;

  text-align: center;

  font-size: 13px;

  color: var(--text-3);

}


.login-link button {

  margin-left: 6px;

  padding: 0;

  border: none;

  background: none;

  color: var(--text-1);

  cursor: pointer;

}


.login-link button:hover {

  text-decoration: underline;

}

</style>
