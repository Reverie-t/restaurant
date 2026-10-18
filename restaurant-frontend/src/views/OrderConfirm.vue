<template>

  <div class="confirm-page">

    <header class="confirm-header">

      <div
        class="logo"
        @click="router.push('/')"
      >
        MORI
      </div>

      <button
        class="back-button"
        @click="router.push('/menu/cart')"
      >
        ← 返回点餐车
      </button>

    </header>


    <main class="confirm-content">

      <div class="page-title">

        <span>
          CHECKOUT
        </span>

        <h1>
          确认订单
        </h1>

      </div>


      <!-- 用餐方式 -->

      <section class="section">

        <h2>
          用餐方式
        </h2>

        <div class="type-select">

          <button
            :class="{
              active: form.orderType === 1
            }"
            @click="form.orderType = 1"
          >

            <strong>
              堂食
            </strong>

            <span>
              在店内用餐
            </span>

          </button>


          <button
            :class="{
              active: form.orderType === 2
            }"
            @click="form.orderType = 2"
          >

            <strong>
              外带
            </strong>

            <span>
              打包带走
            </span>

          </button>

        </div>

      </section>


      <!-- 堂食 -->

      <section
        v-if="form.orderType === 1"
        class="section"
      >

        <h2>
          用餐信息
        </h2>


        <div class="form-row">

          <label>
            用餐人数
          </label>

          <el-input-number
            v-model="form.people"
            :min="1"
            :max="20"
          />

        </div>


        <div class="form-row">

          <label>
            选择餐桌
          </label>

          <el-select
            v-model="form.tableId"
            placeholder="请选择餐桌"
            style="width: 300px"
          >

            <el-option
              v-for="table in tables"
              :key="table.id"
              :label="
                `${table.tableNo} · ${table.capacity}人`
              "
              :value="table.id"
            />

          </el-select>

        </div>

      </section>


      <!-- 外带 -->

      <section
        v-if="form.orderType === 2"
        class="section"
      >

        <h2>
          取餐信息
        </h2>

        <div class="form-row">

          <label>
            取餐人
          </label>

          <el-input
            v-model="form.pickupName"
            placeholder="请输入取餐人姓名"
            style="width: 300px"
          />

        </div>

      </section>


      <!-- 菜品 -->

      <section class="section">

        <h2>
          菜品
        </h2>


        <div
          v-for="item in cart.items"
          :key="item.id"
          class="order-item"
        >

          <div>

            <strong>
              {{ item.name }}
            </strong>

            <span>
              × {{ item.quantity }}
            </span>

          </div>


          <strong>

            ¥{{
              (
                item.price *
                item.quantity
              ).toFixed(2)
            }}

          </strong>

        </div>

      </section>


      <!-- 总金额 -->

      <section class="total-section">

        <span>
          {{ cart.totalQuantity }} 份
        </span>

        <strong>
          ¥{{ cart.totalAmount.toFixed(2) }}
        </strong>

      </section>


      <!-- 提交 -->

      <button
        class="submit-button"
        @click="submitOrder"
      >

        提交订单

        <span>
          →
        </span>

      </button>

    </main>


    <!-- 支付宝沙箱支付弹窗 -->

    <div
      v-if="payVisible"
      class="pay-mask"
      @click.self="closePay"
    >

      <div class="pay-dialog">

        <button
          class="pay-close"
          @click="closePay"
        >
          ×
        </button>


        <div class="pay-logo">

          <svg
            viewBox="0 0 24 24"
            width="34"
            height="34"
            aria-hidden="true"
          >

            <rect
              width="24"
              height="24"
              rx="6"
              fill="#1677ff"
            />

            <text
              x="12"
              y="16.5"
              text-anchor="middle"
              font-size="12"
              fill="#fff"
              font-weight="700"
            >
              支
            </text>

          </svg>

        </div>


        <h2 class="pay-title">
          支付宝支付（沙箱环境）
        </h2>


        <div class="pay-amount">

          <span>
            支付金额
          </span>

          <strong>
            ¥{{ pendingAmount }}
          </strong>

        </div>


        <div class="pay-info">

          <div>

            <span>
              订单号
            </span>

            <span>
              {{ pendingOrderId }}
            </span>

          </div>

          <div>

            <span>
              收款方
            </span>

            <span>
              MORI 餐厅
            </span>

          </div>

        </div>


        <button
          class="pay-confirm"
          :disabled="paying"
          @click="confirmPay"
        >

          {{ paying ? "跳转中..." : "打开支付宝支付" }}

        </button>


        <div
          v-if="paying"
          class="pay-tip"
        >
          即将跳转到支付宝沙箱收银台...
        </div>


        <button
          class="pay-later"
          @click="closePay"
        >
          稍后支付
        </button>


        <div class="pay-sandbox-note">
          演示环境：使用沙箱买家账号
          <br />
          grjren2878@sandbox.com / 支付密码 111111
        </div>

      </div>

    </div>

  </div>

</template>


<script setup>

import {
  onMounted,
  reactive,
  ref
} from "vue"

import {
  useRouter
} from "vue-router"

import {
  useCartStore
} from "@/stores/cart"

import { getTableList } from "@/api/table"
import { ElMessage } from "element-plus"
import { addOrder, createAlipayForm } from "@/api/order"


const router = useRouter()

const cart = useCartStore()


const tables = ref([])


// 支付弹窗状态
const payVisible = ref(false)

const paying = ref(false)

const pendingAmount = ref("0.00")

const pendingOrderNo = ref("")

const pendingOrderId = ref(null)


const form = reactive({

  // 1 堂食
  // 2 外带
  orderType: 1,

  tableId: null,

  people: 1,

  pickupName: ""

})
async function loadTables() {

  try {

    const res = await getTableList({
      status: 0
    })

    console.log(
      "空闲餐桌：",
      res.data
    )

    if (res.data.code === 200) {

      // 根据你之前餐桌接口的数据结构
      // 如果 data 本身就是数组
      tables.value =
        res.data.data || []

    }

  } catch (error) {

    console.error(
      "获取餐桌失败：",
      error
    )

  }

}

async function submitOrder() {

  // =========================
  // ① 检查购物车
  // =========================

  if (cart.items.length === 0) {

    ElMessage.warning("请先选择菜品")

    return
  }


  // =========================
  // ② 检查堂食信息
  // =========================

  if (form.orderType === 1) {

    if (!form.tableId) {

      ElMessage.warning("请选择餐桌")

      return
    }

    if (!form.people) {

      ElMessage.warning("请输入用餐人数")

      return
    }
  }


  // =========================
  // ③ 检查外带信息
  // =========================

  if (form.orderType === 2) {

    if (!form.pickupName) {

      ElMessage.warning("请输入取餐人")

      return
    }
  }


  // =========================
  // ④ 检查顾客登录
  // =========================

  const token = localStorage.getItem("token")

  if (!token) {

    ElMessage.warning("请先登录")

    router.push({
      path: "/customer/login",
      query: {
        redirect: "/menu/confirm"
      }
    })

    return
  }


  // =========================
  // ⑤ 转换订单明细
  // =========================

  const details = cart.items.map(item => ({

    dishId: item.id,

    quantity: item.quantity

  }))


  // =========================
  // ⑥ 组装请求
  // =========================

  const data = {

    orderType: form.orderType,

    tableId:
      form.orderType === 1
        ? form.tableId
        : null,

    people:
      form.orderType === 1
        ? form.people
        : null,

    pickupName:
      form.orderType === 2
        ? form.pickupName
        : null,

    totalAmount: cart.totalAmount,

    details

  }


  console.log(
    "最终提交订单：",
    data
  )


  // =========================
  // ⑦ 调用后端
  // =========================

  try {

    const res = await addOrder(data)

    console.log(
      "创建订单返回：",
      res.data
    )


    if (res.data.code === 200) {

      // 清空购物车
      cart.clear()


      // 订单 ID（后端下单接口现在返回订单 ID）
      pendingOrderId.value = res.data.data

      pendingAmount.value = data.totalAmount.toFixed(2)

      pendingOrderNo.value = "ORD" + Date.now()


      // 弹出支付宝支付弹窗
      payVisible.value = true

    } else {

      ElMessage.error(
        res.data.message ||
        "订单提交失败"
      )

    }

  } catch (error) {

    console.error(
      "提交订单失败：",
      error
    )

    ElMessage.error(
      "提交订单失败"
    )
  }
}


// =========================
// 确认支付
// =========================

async function confirmPay() {

  if (!pendingOrderId.value) {
    ElMessage.error("订单信息缺失")
    return
  }

  paying.value = true

  try {

    const res = await createAlipayForm(pendingOrderId.value)

    if (res.data.code === 200 && res.data.data) {

      // 关闭当前弹窗
      payVisible.value = false

      // 把支付宝返回的表单 HTML 注入到隐藏容器，自动提交跳转
      const container = document.createElement("div")

      container.innerHTML = res.data.data

      const form = container.querySelector("form")

      if (form) {

        form.id = "alipay_submit_form"

        // 隐藏掉，避免页面抖动
        form.style.display = "none"

        document.body.appendChild(form)

        // 自动提交，浏览器会跳转到支付宝沙箱收银台
        form.submit()

      } else {

        ElMessage.error("支付表单解析失败")

        paying.value = false

      }

    } else {

      ElMessage.error(
        res.data.message || "创建支付订单失败"
      )

      paying.value = false

    }

  } catch (error) {

    console.error("支付失败：", error)

    ElMessage.error("支付失败")

  } finally {

    paying.value = false

  }
}


function closePay() {

  payVisible.value = false

  router.push("/customer/orders")

}


onMounted(() => {

  loadTables()

})

</script>


<style scoped>

.confirm-page {

  min-height: 100vh;

  background: #f5f1ea;

  color: #27231f;

  padding-bottom: 100px;

}


.confirm-header {

  height: 90px;

  padding:
    0 6vw;

  display: flex;

  justify-content: space-between;

  align-items: center;

  border-bottom:
    1px solid
    rgba(39,35,31,.12);

}


.logo {

  font-family:
    "Cormorant Garamond",
    serif;

  font-size: 28px;

  letter-spacing: 5px;

  cursor: pointer;

}


.back-button {

  border: 0;

  background: transparent;

  cursor: pointer;

  font-size: 11px;

  letter-spacing: 2px;

}


.confirm-content {

  max-width: 900px;

  margin: 80px auto;

  padding: 0 30px;

}


.page-title span {

  font-size: 10px;

  letter-spacing: 4px;

  opacity: .5;

}


.page-title h1 {

  margin:
    15px 0 70px;

  font-family:
    "Cormorant Garamond",
    serif;

  font-size: 64px;

  font-weight: 400;

}


.section {

  padding:
    35px 0;

  border-top:
    1px solid
    rgba(39,35,31,.15);

}


.section h2 {

  margin:
    0 0 25px;

  font-family:
    "Cormorant Garamond",
    serif;

  font-size: 28px;

  font-weight: 400;

}


.type-select {

  display: grid;

  grid-template-columns:
    1fr 1fr;

  gap: 20px;

}


.type-select button {

  padding: 25px;

  text-align: left;

  border:
    1px solid
    rgba(39,35,31,.2);

  background: transparent;

  cursor: pointer;

}


.type-select button.active {

  background: #27231f;

  color: #f5f1ea;

}


.type-select strong {

  display: block;

  margin-bottom: 8px;

  font-family:
    "Cormorant Garamond",
    serif;

  font-size: 24px;

  font-weight: 400;

}


.type-select span {

  font-size: 11px;

  opacity: .6;

}


.form-row {

  display: flex;

  align-items: center;

  margin-bottom: 25px;

}


.form-row label {

  width: 100px;

  font-size: 13px;

}


.order-item {

  display: flex;

  justify-content: space-between;

  padding: 18px 0;

  border-bottom:
    1px solid
    rgba(39,35,31,.1);

}


.order-item div {

  display: flex;

  gap: 25px;

}


.order-item span {

  opacity: .5;

}


.total-section {

  display: flex;

  justify-content: space-between;

  align-items: center;

  padding:
    30px 0;

  border-top:
    1px solid #27231f;

}


.total-section strong {

  font-family:
    "Cormorant Garamond",
    serif;

  font-size: 34px;

  font-weight: 400;

}


.submit-button {

  width: 100%;

  padding: 22px;

  border: 0;

  background: #27231f;

  color: #f5f1ea;

  cursor: pointer;

  display: flex;

  justify-content: space-between;

  font-size: 12px;

  letter-spacing: 3px;

}


.submit-button span {

  font-size: 20px;

}


/* =========================
   模拟支付弹窗
   ========================= */

.pay-mask {

  position: fixed;

  inset: 0;

  background:
    rgba(39, 35, 31, .55);

  display: flex;

  align-items: center;

  justify-content: center;

  z-index: 3000;

}


.pay-dialog {

  position: relative;

  width: 340px;

  background: #fff;

  padding: 40px 36px 30px;

  text-align: center;

  border-radius: 4px;

}


.pay-close {

  position: absolute;

  top: 14px;

  right: 16px;

  border: 0;

  background: transparent;

  font-size: 24px;

  color: #9b948b;

  cursor: pointer;

  line-height: 1;

}


.pay-logo {

  display: inline-flex;

  align-items: center;

  justify-content: center;

  margin-bottom: 16px;

}


.pay-title {

  margin: 0 0 30px;

  font-size: 16px;

  font-weight: 500;

  color: #27231f;

}


.pay-amount {

  padding: 20px 0;

  border-top: 1px solid #f0ece5;

  border-bottom: 1px solid #f0ece5;

  margin-bottom: 18px;

}


.pay-amount span {

  display: block;

  font-size: 11px;

  color: #9b948b;

  margin-bottom: 10px;

  letter-spacing: 1px;

}


.pay-amount strong {

  font-family: "Cormorant Garamond", serif;

  font-size: 44px;

  font-weight: 400;

  color: #27231f;

}


.pay-info {

  margin-bottom: 26px;

}


.pay-info div {

  display: flex;

  justify-content: space-between;

  padding: 8px 0;

  font-size: 12px;

  color: #6b645c;

}


.pay-info span:first-child {

  color: #9b948b;

}


.pay-confirm {

  width: 100%;

  padding: 14px;

  border: 0;

  background: #07c160;

  color: #fff;

  cursor: pointer;

  font-size: 14px;

  letter-spacing: 2px;

  border-radius: 4px;

}


.pay-confirm:disabled {

  opacity: .6;

  cursor: not-allowed;

}


.pay-tip {

  margin-top: 14px;

  font-size: 12px;

  color: #9b948b;

  letter-spacing: 1px;

}


.pay-sandbox-note {

  margin-top: 18px;

  padding-top: 14px;

  border-top: 1px dashed #ece6dc;

  font-size: 11px;

  color: #9b948b;

  line-height: 1.6;

  letter-spacing: 0.5px;

}


.pay-later {

  margin-top: 16px;

  border: 0;

  background: transparent;

  color: #9b948b;

  cursor: pointer;

  font-size: 12px;

  letter-spacing: 1px;

}


@media (max-width: 700px) {

  .page-title h1 {

    font-size: 48px;

  }


  .type-select {

    grid-template-columns: 1fr;

  }


  .form-row {

    align-items: flex-start;

    flex-direction: column;

    gap: 10px;

  }

}

</style>
