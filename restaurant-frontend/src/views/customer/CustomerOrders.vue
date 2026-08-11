<template>

  <div class="customer-orders">

    <div class="page-header">

      <h1>我的订单</h1>

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

          <div
            class="status"
          >
            {{ order.statusName }}
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

          <strong>
            ¥{{ order.totalAmount }}
          </strong>

        </div>

      </div>

    </div>

  </div>

</template>


<script setup>

import { ref, onMounted } from "vue"

import { useRouter } from "vue-router"

import { ElMessage } from "element-plus"

import { getMyOrderList } from "@/api/order"


const router = useRouter()

const orders = ref([])

const loading = ref(false)


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


onMounted(() => {

  loadOrders()

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

  margin: 0 0 10px;

  font-family:
    "Cormorant Garamond",
    serif;

  font-size: 48px;

  font-weight: 500;

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


.status {

  font-size: 14px;

  color: #806f5d;

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

</style>
