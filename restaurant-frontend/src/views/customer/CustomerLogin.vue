<template>

  <div class="customer-login">

    <div class="login-box">

      <div class="brand">
        MORI
      </div>

      <h1>欢迎回来</h1>

      <p class="subtitle">
        登录后继续完成点餐
      </p>


      <input
        v-model="form.phone"
        placeholder="手机号"
        maxlength="11"
      />


      <input
        v-model="form.password"
        type="password"
        placeholder="密码"
      />


      <button
        @click="login"
      >
        登录
      </button>

    </div>

  </div>

</template>


<script setup>

import { reactive } from "vue"
import { useRouter } from "vue-router"
import request from "@/utils/request"


const router = useRouter()


const form = reactive({

  phone: "",

  password: ""

})


async function login() {

  if (!form.phone) {

    alert("请输入手机号")

    return

  }


  if (!form.password) {

    alert("请输入密码")

    return

  }


  try {

    const res =
      await request.post(
        "/customer/login",
        form
      )


    if (res.data.code === 200) {

      console.log("登录返回：", res.data)

      // =========================
      // 保存 JWT
      // =========================

      localStorage.setItem(
        "token",
        res.data.data.token
      )


      // =========================
      // 保存顾客信息
      // =========================

      localStorage.setItem(
        "customer",
        JSON.stringify(
          res.data.data.user
        )
      )


      // =========================
      // 登录成功
      // =========================

      const redirect =
        router.currentRoute.value.query.redirect

      if (redirect) {

        router.push(redirect)

      } else {

        router.push("/")

      }

    } else {

      alert(
        res.data.message ||
        "登录失败"
      )

    }

  } catch (error) {

    console.error(
      "登录失败",
      error
    )

    alert("登录失败")

  }

}

</script>
