<template>

  <div class="customer-orders">

    <div class="page-header">

      <div class="page-header-row">

        <h1>我的订单</h1>

        <button
          class="home-btn"
          @click="goHome"
        >
          ← 返回首页
        </button>

      </div>

      <p>查看最近的用餐记录</p>

    </div>


    <div
      v-if="loading"
      class="loading"
    >
      正在加载订单...
    </div>


    <div
      v-else-if="orders.length === 0"
      class="empty"
    >
      <div class="empty-title">
        暂无订单
      </div>

      <div class="empty-text">
        去菜单看看今天吃什么
      </div>

      <button
        @click="goMenu"
      >
        去点餐
      </button>
    </div>


    <div
      v-else
      class="order-list"
    >

      <div
        v-for="order in orders"
        :key="order.id"
        class="order-card"
      >

        <div class="order-top">

          <div>

            <div class="order-no">
              订单 {{ order.orderNo }}
            </div>

            <div class="order-time">
              {{ order.createTime }}
            </div>

          </div>

          <div class="status-wrap">

            <div class="status">
              {{ order.statusName }}
            </div>

            <div
              class="pay-tag"
              :class="order.payStatus === 1 ? 'paid' : 'unpaid'"
            >
              {{ order.payStatus === 1 ? "已支付" : "未支付" }}
            </div>

          </div>

        </div>


        <div class="order-info">

          <span>
            {{ order.orderTypeName }}
          </span>

          <span v-if="order.tableNo">
            {{ order.tableNo }}
          </span>

          <span v-if="order.pickupName">
            取餐人：{{ order.pickupName }}
          </span>

        </div>


        <div class="order-bottom">

          <span>
            合计
          </span>

          <div class="bottom-right">

            <strong>
              ¥{{ order.totalAmount }}
            </strong>

            <button
              class="detail-btn"
              @click="showDetail(order)"
            >
              查看详情
            </button>

          </div>

        </div>

      </div>

    </div>


    <!-- 订单详情弹窗 -->

    <el-dialog
      v-model="detailVisible"
      width="560px"
      align-center
    >

      <template #header>

        <div class="dlg-header">

          <div class="dlg-eyebrow">
            ORDER DETAIL
          </div>

          <h2 class="dlg-title">
            订单详情
          </h2>

        </div>

      </template>

      <div
        v-if="currentOrder"
        v-loading="detailLoading"
      >

        <div class="dlg-info">

          <div class="info-row">
            <span>订单编号</span>
            <strong>{{ currentOrder.orderNo }}</strong>
          </div>

          <div class="info-row">
            <span>下单时间</span>
            <strong>{{ formatTime(currentOrder.createTime) }}</strong>
          </div>

          <div class="info-row">
            <span>订单类型</span>
            <strong>{{ currentOrder.orderTypeName || "—" }}</strong>
          </div>

          <div
            class="info-row"
            v-if="currentOrder.tableNo"
          >
            <span>桌号</span>
            <strong>{{ currentOrder.tableNo }}</strong>
          </div>

          <div
            class="info-row"
            v-if="currentOrder.pickupName"
          >
            <span>取餐人</span>
            <strong>{{ currentOrder.pickupName }}</strong>
          </div>

          <div class="info-row">
            <span>订单状态</span>
            <strong class="dlg-status">
              {{ currentOrder.statusName || "—" }}
            </strong>
          </div>

        </div>


        <div class="dlg-items-title">
          ORDER ITEMS
        </div>

        <div
          class="dlg-item"
          v-for="item in currentOrder.details"
          :key="item.id"
        >

          <div class="item-name">
            {{ item.dishName }}
          </div>

          <div class="item-right">

            <span class="item-price">
              ¥{{ item.dishPrice }} × {{ item.quantity }}
            </span>

            <strong>
              ¥{{ Number(item.subtotal).toFixed(2) }}
            </strong>

          </div>

        </div>

        <div
          class="dlg-empty"
          v-if="currentOrder.details && currentOrder.details.length === 0"
        >
          暂无菜品明细
        </div>


        <div class="dlg-total">

          <span>
            合计
          </span>

          <strong>
            ¥{{ Number(currentOrder.totalAmount).toFixed(2) }}
          </strong>

        </div>

      </div>

    </el-dialog>

  </div>

</template>


<script setup>

import { ref, onMounted } from "vue"

import { useRoute, useRouter } from "vue-router"

import { ElMessage } from "element-plus"

import { getMyOrderList, queryAlipayPay } from "@/api/order"

import request from "@/utils/request"


const route = useRoute()
const router = useRouter()

const orders = ref([])

const loading = ref(false)


const detailVisible = ref(false)

const currentOrder = ref(null)

const detailLoading = ref(false)


async function loadOrders() {

  loading.value = true

  try {

    const res =
      await getMyOrderList()

    if (res.data.code === 200) {

      orders.value =
        res.data.data || []

    } else {

      ElMessage.error(
        res.data.message ||
        "订单加载失败"
      )

    }

  } catch (error) {

    console.error(
      "加载我的订单失败：",
      error
    )

    ElMessage.error(
      "订单加载失败"
    )

  } finally {

    loading.value = false

  }

}


function goMenu() {

  router.push("/menu")

}


function goHome() {

  router.push("/")

}


// ================================
// 查看订单详情
// ================================

async function showDetail(order) {

  currentOrder.value = {
    ...order,
    details: []
  }

  detailVisible.value = true

  detailLoading.value = true

  try {

    const res =
      await request.get(
        "/orders/my/" + order.id
      )

    if (res.data.code === 200) {

      currentOrder.value = {
        ...order,
        details: res.data.data || []
      }

    } else {

      ElMessage.error(
        res.data.message ||
        "订单详情加载失败"
      )

    }

  } catch (error) {

    console.error(
      "加载订单详情失败：",
      error
    )

    ElMessage.error(
      "订单详情加载失败"
    )

  } finally {

    detailLoading.value = false

  }

}


// 时间显示格式化（去掉 T，精确到分）
function formatTime(t) {

  return t
    ? String(t).replace("T", " ").slice(0, 16)
    : ""

}


// ================================
// 支付宝沙箱回跳：监听 ?fromAlipay=1
// 付款完成后浏览器被跳回本页，主动查单确认支付状态
// ================================
async function handleAlipayReturn() {
  if (route.query.fromAlipay !== "1") return;

  const orderId = route.query.out_trade_no;
  if (!orderId) {
    ElMessage.warning("支付宝回跳参数缺失");
    return;
  }

  // 清理 URL 上的回跳参数，避免用户刷新页面时重复触发
  router.replace({ query: {} });

  try {
    const res = await queryAlipayPay(orderId);
    if (res.data.code === 200 && res.data.data && res.data.data.paid) {
      ElMessage.success("支付成功！");
    } else if (res.data.code === 200) {
      ElMessage.warning("订单尚未完成支付，请稍后再试");
    } else {
      ElMessage.error(res.data.message || "支付状态查询失败");
    }
  } catch (error) {
    console.error("查询支付状态失败：", error);
    ElMessage.error("支付状态查询失败");
  }

  // 无论成功与否，刷新订单列表拿最新 pay_status
  await loadOrders();
}


onMounted(() => {

  loadOrders()

  handleAlipayReturn()

})

</script>
<style scoped>

.customer-orders {

  max-width: 1100px;

  margin: 0 auto;

  padding: 80px 30px;

  color: #2d2925;

}


.page-header {

  margin-bottom: 40px;

}


.page-header h1 {

  margin: 0;

  font-family:
    "Cormorant Garamond",
    serif;

  font-size: 48px;

  font-weight: 500;

}


.page-header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 10px;
}


.home-btn {
  border: 1px solid #3f3933;
  background: transparent;
  color: #3f3933;
  padding: 8px 22px;
  font-size: 13px;
  cursor: pointer;
  letter-spacing: 1px;
  transition: background .2s, color .2s;
}


.home-btn:hover {
  background: #3f3933;
  color: #fff;
}

.page-header p {

  margin: 0;

  color: #8a8178;

}


.order-list {

  display: flex;

  flex-direction: column;

  gap: 18px;

}


.order-card {

  padding: 26px 30px;

  background: #fff;

  border: 1px solid #e7e1da;

  transition:
    transform .25s,
    box-shadow .25s;

}


.order-card:hover {

  transform: translateY(-2px);

  box-shadow:
    0 12px 30px
    rgba(50, 40, 30, .08);

}


.order-top {

  display: flex;

  justify-content: space-between;

  align-items: flex-start;

  padding-bottom: 20px;

  border-bottom: 1px solid #eee9e3;

}


.order-no {

  font-size: 16px;

  font-weight: 600;

}


.order-time {

  margin-top: 7px;

  font-size: 13px;

  color: #999;

}


.status-wrap {

  display: flex;

  flex-direction: column;

  align-items: flex-end;

  gap: 8px;

}


.status {

  font-size: 14px;

  color: #806f5d;

}


.pay-tag {

  font-size: 12px;

  padding: 3px 10px;

  border-radius: 2px;

}


.pay-tag.paid {

  color: #0f6e56;

  background: #e1f5ee;

}


.pay-tag.unpaid {

  color: #a32d2d;

  background: #fcebeb;

}


.order-info {

  display: flex;

  gap: 25px;

  padding: 20px 0;

  color: #70685f;

  font-size: 14px;

}


.order-bottom {

  display: flex;

  justify-content: space-between;

  align-items: center;

}


.order-bottom strong {

  font-size: 22px;

  font-weight: 500;

}


.empty {

  padding: 100px 20px;

  text-align: center;

  border-top: 1px solid #e7e1da;

}


.empty-title {

  font-size: 24px;

  margin-bottom: 10px;

}


.empty-text {

  color: #999;

  margin-bottom: 25px;

}


.empty button {

  padding: 11px 28px;

  border: none;

  background: #3f3933;

  color: white;

  cursor: pointer;

}


.loading {

  padding: 100px;

  text-align: center;

  color: #999;

}


/* ==================== 详情按钮 ==================== */

.bottom-right {

  display: flex;

  align-items: center;

  gap: 18px;

}


.detail-btn {

  padding: 8px 22px;

  border: 1px solid #3f3933;

  background: transparent;

  color: #3f3933;

  font-size: 13px;

  cursor: pointer;

  transition:
    background .2s,
    color .2s;

}


.detail-btn:hover {

  background: #3f3933;

  color: white;

}


/* ==================== 详情弹窗 ==================== */

.dlg-header {

  padding-right: 30px;

}


.dlg-eyebrow {

  margin-bottom: 6px;

  font-size: 11px;

  letter-spacing: 3px;

  color: #8a8178;

  font-weight: 500;

}


.dlg-title {

  margin: 0;

  font-family:
    "Cormorant Garamond",
    serif;

  font-size: 30px;

  font-weight: 500;

}


.dlg-info {

  padding: 4px 0 16px;

  border-bottom: 1px solid #eee9e3;

}


.info-row {

  display: flex;

  justify-content: space-between;

  padding: 7px 0;

  font-size: 14px;

}


.info-row span {

  color: #8a8178;

}


.info-row strong {

  font-weight: 500;

}


.dlg-status {

  color: #806f5d;

}


.dlg-items-title {

  margin: 20px 0 4px;

  font-size: 11px;

  letter-spacing: 3px;

  color: #8a8178;

  font-weight: 500;

}


.dlg-item {

  display: flex;

  justify-content: space-between;

  align-items: center;

  padding: 12px 0;

  border-bottom: 1px dashed #eee9e3;

  font-size: 14px;

}


.item-name {

  color: #2d2925;

}


.item-right {

  display: flex;

  align-items: center;

  gap: 16px;

}


.item-price {

  font-size: 13px;

  color: #8a8178;

}


.dlg-item strong {

  font-weight: 500;

}


.dlg-empty {

  padding: 20px 0;

  text-align: center;

  color: #999;

  font-size: 13px;

}


.dlg-total {

  display: flex;

  justify-content: space-between;

  align-items: center;

  padding-top: 18px;

  font-size: 15px;

}


.dlg-total strong {

  font-size: 24px;

  font-weight: 500;

}

</style>
