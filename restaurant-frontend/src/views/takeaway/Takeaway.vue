<template>
  <div class="takeaway-page">

    <!-- 页面头部 -->
    <div class="page-header">

      <div>
        <div class="eyebrow">
          TAKEAWAY
        </div>

        <h1>
          外卖管理
        </h1>

        <p>
          管理外卖订单与配送前的制作流程
        </p>
      </div>

      <div class="header-actions">

        <button
          class="refresh-btn"
          @click="loadOrders"
        >
          ↻ 刷新
        </button>

        <button
          class="primary-btn"
          @click="openAdd"
        >
          ＋ 新增外卖
        </button>

      </div>

    </div>


    <!-- 状态统计 -->
    <div class="stats">

      <div class="stat-card">
        <span>全部外卖</span>
        <strong>{{ orders.length }}</strong>
      </div>

      <div class="stat-card">
        <span>待确认</span>
        <strong>{{ countByStatus(1) }}</strong>
      </div>

      <div class="stat-card">
        <span>制作中</span>
        <strong>{{ countByStatus(2) }}</strong>
      </div>

      <div class="stat-card">
        <span>待取餐</span>
        <strong>{{ countByStatus(3) }}</strong>
      </div>

      <div class="stat-card">
        <span>已完成</span>
        <strong>{{ countByStatus(4) }}</strong>
      </div>

    </div>


    <!-- 筛选 -->
    <div class="toolbar">

      <div class="filter-group">

        <button
          :class="{ active: currentStatus === null }"
          @click="filterByStatus(null)"
        >
          全部
        </button>

        <button
          :class="{ active: currentStatus === 1 }"
          @click="filterByStatus(1)"
        >
          待确认
        </button>

        <button
          :class="{ active: currentStatus === 2 }"
          @click="filterByStatus(2)"
        >
          制作中
        </button>

        <button
          :class="{ active: currentStatus === 3 }"
          @click="filterByStatus(3)"
        >
          待取餐
        </button>

        <button
          :class="{ active: currentStatus === 4 }"
          @click="filterByStatus(4)"
        >
          已完成
        </button>

        <button
          :class="{ active: currentStatus === 5 }"
          @click="filterByStatus(5)"
        >
          已取消
        </button>

      </div>

    </div>


    <!-- 加载 -->
    <div
      v-if="loading"
      class="loading"
    >
      正在加载订单...
    </div>


    <!-- 空状态 -->
    <div
      v-else-if="orders.length === 0"
      class="empty"
    >
      <div class="empty-icon">
        —
      </div>

      <h3>
        暂无外卖订单
      </h3>

      <p>
        当前没有符合条件的外卖订单
      </p>
    </div>


    <!-- 订单列表 -->
    <div
      v-else
      class="order-list"
    >

      <div
        v-for="order in orders"
        :key="order.id"
        class="order-card"
      >

        <!-- 顶部 -->
        <div class="order-top">

          <div>

            <span class="order-label">
              ORDER
            </span>

            <h2>
              #{{ order.orderNo }}
            </h2>

          </div>

          <span
            class="status"
            :class="statusClass(order.status)"
          >
            {{ order.statusName }}
          </span>

        </div>


        <!-- 信息 -->
        <div class="order-info">

          <div>
            <span>取餐人</span>
            <strong>
              {{ order.pickupName || "—" }}
            </strong>
          </div>

          <div>
            <span>下单时间</span>
            <strong>
              {{ formatTime(order.createTime) }}
            </strong>
          </div>

          <div>
            <span>订单类型</span>
            <strong>
              {{ order.orderTypeName }}
            </strong>
          </div>

          <div>
            <span>订单金额</span>
            <strong class="amount">
              ¥{{ order.totalAmount }}
            </strong>
          </div>

        </div>


        <!-- 底部 -->
        <div class="order-bottom">

          <button
            class="detail-btn"
            @click="showDetail(order)"
          >
            查看详情
          </button>

          <div class="actions">

            <!-- 待确认 -->
            <template v-if="order.status === 1">

              <button
                @click="changeStatus(order, 2)"
                class="primary-btn"
              >
                开始制作
              </button>

              <button
                class="danger"
                @click="changeStatus(order, 5)"

              >
                取消订单
              </button>

            </template>


            <!-- 制作中 -->

            <button
              v-else-if="order.status === 2"
              @click="changeStatus(order, 3)"
              class="primary-btn"
            >
              制作完成
            </button>


            <!-- 待取餐 -->

            <button
              v-else-if="order.status === 3"
              @click="changeStatus(order, 4)"
              class="primary-btn"
            >
              确认完成
            </button>


            <!-- 已完成 -->

            <span
              v-else-if="order.status === 4"
              class="finished"
            >
    已完成
  </span>


            <!-- 已取消 -->

            <span
              v-else-if="order.status === 5"
              class="cancelled"
            >
    已取消
  </span>

          </div>


        </div>

      </div>

    </div>


    <!-- 订单详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="订单详情"
      width="620px"
    >

      <div
        v-if="currentOrder"
        class="detail-content"
      >

        <!-- 订单基本信息 -->

        <div class="detail-header">

          <div>

            <span>订单编号</span>

            <strong>
              #{{ currentOrder.orderNo }}
            </strong>

          </div>

          <span
            class="status"
            :class="statusClass(currentOrder.status)"
          >
      {{ currentOrder.statusName }}
    </span>

        </div>


        <!-- 顾客信息 -->

        <div class="detail-meta">

          <div>

            <span>取餐人</span>

            <strong>
              {{ currentOrder.pickupName || "—" }}
            </strong>

          </div>


          <div>

            <span>联系电话</span>

            <strong>
              {{ currentOrder.pickupPhone || "—" }}
            </strong>

          </div>


          <div>

            <span>订单类型</span>

            <strong>
              {{ currentOrder.orderTypeName }}
            </strong>

          </div>


          <div>

            <span>下单时间</span>

            <strong>
              {{ formatTime(currentOrder.createTime) }}
            </strong>

          </div>

        </div>


        <!-- 菜品 -->

        <div class="detail-dishes">

          <div class="detail-section-title">
            ORDER ITEMS
          </div>

          <div
            v-if="
        !currentOrder.details ||
        currentOrder.details.length === 0
      "
            class="no-dishes"
          >
            暂无菜品明细
          </div>


          <div
            v-for="item in currentOrder.details"
            :key="item.id"
            class="dish-row"
          >

            <div class="dish-name">

              <strong>
                {{ item.dishName }}
              </strong>

              <span>
          ¥{{ item.dishPrice }}
        </span>

            </div>


            <div class="dish-quantity">
              × {{ item.quantity }}
            </div>


            <div class="dish-subtotal">
              ¥{{ item.subtotal }}
            </div>

          </div>

        </div>


        <!-- 总计 -->

        <div class="detail-total">

    <span>
      TOTAL
    </span>

          <strong>
            ¥{{ currentOrder.totalAmount }}
          </strong>

        </div>

      </div>

    </el-dialog>
    <el-dialog
      v-model="addVisible"
      title="新增外卖订单"
      width="680px"
    >

      <div class="add-form">

        <!-- 顾客信息 -->

        <div class="add-info">

          <div class="form-field">

            <label>
              取餐人
            </label>

            <input
              v-model="addForm.pickupName"
              placeholder="请输入取餐人"
            />

          </div>


          <div class="form-field">

            <label>
              联系电话
            </label>

            <input
              v-model="addForm.pickupPhone"
              placeholder="请输入联系电话"
            />

          </div>

        </div>


        <div class="form-field">

          <label>
            备注
          </label>

          <textarea
            v-model="addForm.remark"
            placeholder="请输入订单备注"
          ></textarea>

        </div>


        <!-- 菜品 -->

        <div class="items-title">
          ORDER ITEMS
        </div>


        <div
          v-for="(item, index) in addForm.details"
          :key="index"
          class="add-item"
        >

          <select
            v-model="item.dishId"
            @change="updateItemDish(item)"
          >

            <option
              :value="null"
              disabled
            >
              选择菜品
            </option>

            <option
              v-for="dish in menuDishes"
              :key="dish.id"
              :value="dish.id"
            >
              {{ dish.name }}
              · ¥{{ dish.price }}
            </option>

          </select>


          <div class="quantity-control">

            <button
              type="button"
              @click="decreaseQuantity(item)"
            >
              −
            </button>

            <span>
          {{ item.quantity }}
        </span>

            <button
              type="button"
              @click="increaseQuantity(item)"
            >
              ＋
            </button>

          </div>


          <strong>
            ¥{{ item.subtotal.toFixed(2) }}
          </strong>


          <button
            type="button"
            class="remove-item"
            @click="removeItem(index)"
          >
            ×
          </button>

        </div>


        <button
          class="add-item-btn"
          type="button"
          @click="addItem"
        >
          ＋ 添加菜品
        </button>


        <!-- 总计 -->

        <div class="add-total">

      <span>
        TOTAL
      </span>

          <strong>
            ¥{{ addTotalAmount.toFixed(2) }}
          </strong>

        </div>

      </div>


      <template #footer>

        <button
          class="dialog-cancel"
          @click="addVisible = false"
        >
          取消
        </button>

        <button
          class="dialog-confirm"
          @click="submitAdd"
        >
          创建订单
        </button>

      </template>

    </el-dialog>

  </div>
</template>


<script setup>

import {
  ref,
  onMounted,computed
} from "vue"

import {
  ElMessage,
  ElMessageBox
} from "element-plus"

import request from "@/utils/request"
import {
  getMenuDishes
} from "@/api/dish"

// ================================
// 新增外卖
// ================================

const addVisible = ref(false)

const menuDishes = ref([])

const addForm = ref({
  pickupName: "",
  pickupPhone: "",
  remark: "",
  details: []
})

const orders = ref([])

const loading = ref(false)

const currentStatus = ref(null)

const detailVisible = ref(false)

const currentOrder = ref(null)


// ================================
// 获取订单
// ================================
async function submitAdd() {

  if (!addForm.value.pickupName) {

    ElMessage.warning(
      "请输入取餐人"
    )

    return

  }


  if (!addForm.value.pickupPhone) {

    ElMessage.warning(
      "请输入联系电话"
    )

    return

  }


  const validDetails =
    addForm.value.details
      .filter(
        item =>
          item.dishId !== null
      )


  if (
    validDetails.length === 0
  ) {

    ElMessage.warning(
      "请至少选择一道菜品"
    )

    return

  }


  try {

    const data = {

      pickupName:
      addForm.value.pickupName,

      pickupPhone:
      addForm.value.pickupPhone,

      remark:
      addForm.value.remark,

      details:
        validDetails.map(
          item => ({
            dishId:
            item.dishId,

            quantity:
            item.quantity
          })
        )

    }


    const res =
      await request.post(
        "/orders/takeaway",
        data
      )


    if (res.data.code === 200) {

      ElMessage.success(
        "外卖订单创建成功"
      )

      addVisible.value = false

      await loadOrders()

    } else {

      ElMessage.error(
        res.data.message ||
        "订单创建失败"
      )

    }

  } catch (error) {

    console.error(
      "创建外卖订单失败",
      error
    )

    ElMessage.error(
      error?.response?.data?.message ||
      "创建外卖订单失败"
    )

  }

}

function addItem() {

  addForm.value.details.push({

    dishId: null,

    quantity: 1,

    dishName: "",

    dishPrice: 0,

    subtotal: 0

  })

}
function updateItemDish(item) {

  const dish =
    menuDishes.value.find(
      d =>
        d.id === item.dishId
    )


  if (!dish) {

    item.dishName = ""

    item.dishPrice = 0

    item.subtotal = 0

    return

  }


  item.dishName =
    dish.name

  item.dishPrice =
    Number(dish.price)


  item.subtotal =
    item.dishPrice *
    item.quantity

}
function increaseQuantity(item) {

  item.quantity++

  item.subtotal =
    item.dishPrice *
    item.quantity

}


function decreaseQuantity(item) {

  if (item.quantity <= 1) {

    return

  }

  item.quantity--

  item.subtotal =
    item.dishPrice *
    item.quantity

}
function removeItem(index) {

  addForm.value.details.splice(
    index,
    1
  )


  if (
    addForm.value.details.length === 0
  ) {

    addItem()

  }

}
const addTotalAmount =
  computed(() => {

    return addForm.value.details
      .reduce(
        (total, item) =>
          total +
          Number(item.subtotal || 0),
        0
      )

  })


async function openAdd() {

  addForm.value = {

    pickupName: "",

    pickupPhone: "",

    remark: "",

    details: []

  }


  addItem()

  try {

    const res =
      await getMenuDishes()


    if (res.data.code === 200) {

      menuDishes.value =
        res.data.data || []

      addVisible.value = true

    } else {

      ElMessage.error(
        res.data.message ||
        "菜品加载失败"
      )

    }

  } catch (error) {

    console.error(
      "加载菜品失败",
      error
    )

    ElMessage.error(
      "加载菜品失败"
    )

  }

}

async function loadOrders() {

  loading.value = true

  try {

    const params = {
      orderType: 3
    }

    if (currentStatus.value !== null) {
      params.status = currentStatus.value
    }

    const res = await request.get(
      "/orders",
      {
        params
      }
    )

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
      "加载外卖订单失败",
      error
    )

    ElMessage.error(
      "加载外卖订单失败"
    )

  } finally {

    loading.value = false

  }

}


// ================================
// 状态筛选
// ================================




// ================================
// 状态数量
// ================================

function countByStatus(status) {

  return orders.value.filter(
    item => item.status === status
  ).length

}


// ================================
// 按状态筛选订单
// ================================

function filterByStatus(status) {

  currentStatus.value = status

  loadOrders()

}


// ================================
// 修改状态
// ================================

async function changeStatus(order, status) {

  try {

    const res = await request.put(
      "/orders/status",
      {
        id: order.id,
        status: status
      }
    )

    if (res.data.code === 200) {

      ElMessage.success(
        "订单状态已更新"
      )

      // 重新加载订单
      await loadOrders()

    } else {

      ElMessage.error(
        res.data.message ||
        "状态修改失败"
      )
    }

  } catch (error) {

    console.error(
      "修改订单状态失败",
      error
    )

    ElMessage.error(
      "修改订单状态失败"
    )
  }
}


// ================================
// 查看详情
// ================================

async function showDetail(order) {

  try {

    const res = await request.get(
      `/orders/${order.id}`
    )

    if (res.data.code === 200) {

      currentOrder.value = {
        ...order,
        details: res.data.data || []
      }

      detailVisible.value = true

    } else {

      ElMessage.error(
        res.data.message ||
        "获取订单详情失败"
      )

    }

  } catch (error) {

    console.error(
      "获取订单详情失败",
      error
    )

    ElMessage.error(
      "获取订单详情失败"
    )

  }

}


// ================================
// 时间格式
// ================================

function formatTime(time) {

  if (!time) {
    return "—"
  }

  return time
    .replace("T", " ")
    .slice(0, 16)

}


// ================================
// 状态样式
// ================================

function statusClass(status) {

  return {

    status1: status === 1,

    status2: status === 2,

    status3: status === 3,

    status4: status === 4,

    status5: status === 5

  }

}



onMounted(() => {

  loadOrders()

})

</script>


<style scoped>

.takeaway-page {
  padding: 36px 42px;
  min-height: 100%;
  background: #f7f5f1;
}


/* ================================
   Header
================================ */

.page-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 32px;
}

.eyebrow {
  font-size: 11px;
  letter-spacing: 3px;
  color: #8b8176;
  margin-bottom: 8px;
}

.page-header h1 {
  margin: 0;
  font-family: "Cormorant Garamond", serif;
  font-size: 42px;
  font-weight: 500;
  color: #302b27;
}

.page-header p {
  margin: 8px 0 0;
  color: #8b8176;
  font-size: 14px;
}

.refresh-btn {
  border: 1px solid #d8d0c7;
  background: transparent;
  padding: 10px 18px;
  cursor: pointer;
  color: #4d4640;
}

.refresh-btn:hover {
  background: #eee9e3;
}


/* ================================
   Stats
================================ */

.stats {
  display: grid;
  grid-template-columns:
    repeat(5, 1fr);

  gap: 14px;

  margin-bottom: 28px;
}

.stat-card {
  background: #fff;
  padding: 22px;
  border: 1px solid #e6e0d9;
}

.stat-card span {
  display: block;
  font-size: 12px;
  color: #999087;
  margin-bottom: 10px;
}

.stat-card strong {
  font-family: "Cormorant Garamond", serif;
  font-size: 32px;
  font-weight: 500;
  color: #302b27;
}


/* ================================
   Toolbar
================================ */

.toolbar {
  border-bottom: 1px solid #ddd6ce;
  margin-bottom: 24px;
}

.filter-group {
  display: flex;
  gap: 28px;
}

.filter-group button {
  border: none;
  background: transparent;
  padding: 12px 0;
  color: #918980;
  cursor: pointer;
  font-size: 14px;
  border-bottom: 2px solid transparent;
}

.filter-group button.active {
  color: #302b27;
  border-bottom-color: #302b27;
}


/* ================================
   Order
================================ */

.order-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.order-card {
  background: #fff;
  border: 1px solid #e4ddd5;
  padding: 26px 30px;
  transition: box-shadow .25s;
}

.order-card:hover {
  box-shadow:
    0 10px 30px rgba(40, 35, 30, .06);
}

.order-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.order-label {
  font-size: 10px;
  letter-spacing: 2px;
  color: #a49a90;
}

.order-top h2 {
  margin: 5px 0 0;
  font-size: 18px;
  font-weight: 500;
  color: #332e29;
}


.status {
  padding: 6px 12px;
  font-size: 12px;
}

.status1 {
  background: #f1ebe4;
  color: #7d6855;
}

.status2 {
  background: #eee7dc;
  color: #755b43;
}

.status3 {
  background: #e9eee9;
  color: #526354;
}

.status4 {
  background: #e5eee7;
  color: #46604d;
}

.status5 {
  background: #eee8e8;
  color: #806767;
}


/* ================================
   Info
================================ */

.order-info {
  display: grid;
  grid-template-columns:
    repeat(4, 1fr);

  gap: 20px;

  margin: 24px 0;
  padding: 18px 0;

  border-top: 1px solid #eee9e4;
  border-bottom: 1px solid #eee9e4;
}

.order-info span,
.detail-meta span {
  display: block;
  color: #9b938b;
  font-size: 12px;
  margin-bottom: 6px;
}

.order-info strong,
.detail-meta strong {
  font-size: 14px;
  font-weight: 500;
  color: #403a35;
}

.amount {
  font-family: "Cormorant Garamond", serif;
  font-size: 20px !important;
}


/* ================================
   Bottom
================================ */

.order-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.detail-btn {
  border: none;
  background: transparent;
  padding: 8px 0;
  color: #70675f;
  cursor: pointer;
}

.detail-btn:hover {
  color: #302b27;
}

.actions {
  display: flex;
  gap: 10px;
}

.primary-btn {
  border: none;
  background: #3d3731;
  color: white;
  padding: 10px 18px;
  cursor: pointer;
}

.primary-btn:hover {
  background: #51483f;
}
.detail-dishes {
  margin-top: 28px;
  border-top: 1px solid #eee9e4;
  padding-top: 22px;
}

.detail-section-title {
  font-size: 10px;
  letter-spacing: 2px;
  color: #9b938b;
  margin-bottom: 14px;
}
.add-info {
  display: grid;

  grid-template-columns:
    repeat(2, 1fr);

  gap: 18px;

  margin-bottom: 18px;
}


.form-field {
  display: flex;

  flex-direction: column;

  gap: 7px;

  margin-bottom: 16px;
}


.form-field label {
  font-size: 12px;

  color: #8b8176;
}


.form-field input,
.form-field textarea {

  border: 1px solid #d8d0c7;

  background: #fff;

  padding: 10px 12px;

  outline: none;

  font-family: inherit;

  color: #403a35;

}


.form-field input:focus,
.form-field textarea:focus {

  border-color: #8b8176;

}


.form-field textarea {

  min-height: 80px;

  resize: vertical;

}


.items-title {

  margin-top: 24px;

  margin-bottom: 12px;

  padding-top: 20px;

  border-top: 1px solid #eee9e4;

  font-size: 10px;

  letter-spacing: 2px;

  color: #9b938b;

}


.add-item {

  display: grid;

  grid-template-columns:
    1fr 100px 80px 30px;

  align-items: center;

  gap: 12px;

  padding: 12px 0;

  border-bottom:
    1px solid #f0ece8;

}


.add-item select {

  height: 36px;

  border: 1px solid #d8d0c7;

  background: #fff;

  padding: 0 10px;

  color: #403a35;

}


.quantity-control {

  display: flex;

  align-items: center;

  justify-content: center;

  gap: 10px;

}


.quantity-control button {

  width: 26px;

  height: 26px;

  border: 1px solid #d8d0c7;

  background: transparent;

  color: #4d4640;

  cursor: pointer;

}


.quantity-control span {

  min-width: 20px;

  text-align: center;

  font-size: 13px;

}


.add-item strong {

  font-family:
    "Cormorant Garamond",
    serif;

  font-size: 18px;

  font-weight: 500;

  color: #403a35;

}


.remove-item {

  border: none;

  background: none;

  font-size: 20px;

  color: #9b938b;

  cursor: pointer;

}


.remove-item:hover {

  color: #806767;

}


.add-item-btn {

  margin-top: 14px;

  border: none;

  background: transparent;

  color: #70675f;

  cursor: pointer;

  padding: 7px 0;

}


.add-item-btn:hover {

  color: #302b27;

}


.add-total {

  display: flex;

  align-items: center;

  justify-content: space-between;

  margin-top: 20px;

  padding-top: 18px;

  border-top: 1px solid #ddd6ce;

}


.add-total span {

  font-size: 10px;

  letter-spacing: 2px;

  color: #918980;

}


.add-total strong {

  font-family:
    "Cormorant Garamond",
    serif;

  font-size: 28px;

  font-weight: 500;

  color: #302b27;

}


.dialog-cancel,
.dialog-confirm {

  padding: 10px 18px;

  cursor: pointer;

}


.dialog-cancel {

  border: 1px solid #d8d0c7;

  background: transparent;

  color: #4d4640;

}


.dialog-confirm {

  margin-left: 8px;

  border: none;

  background: #3d3731;

  color: white;

}


.dialog-confirm:hover {

  background: #51483f;

}

.dish-row {
  display: grid;
  grid-template-columns: 1fr 80px 100px;
  align-items: center;
  padding: 13px 0;
  border-bottom: 1px solid #f0ece8;
}

.dish-name {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.dish-name strong {
  font-size: 14px;
  font-weight: 500;
  color: #403a35;
}

.dish-name span {
  font-size: 12px;
  color: #9b938b;
}

.dish-quantity {
  text-align: center;
  color: #777069;
  font-size: 13px;
}

.dish-subtotal {
  text-align: right;
  font-family: "Cormorant Garamond", serif;
  font-size: 19px;
  color: #403a35;
}

.no-dishes {
  padding: 25px 0;
  text-align: center;
  color: #aaa29a;
  font-size: 13px;
}

.detail-total {
  display: flex;
  align-items: center;
  justify-content: space-between;

  margin-top: 20px;
  padding-top: 18px;

  border-top: 1px solid #ddd6ce;
}

.detail-total span {
  font-size: 10px;
  letter-spacing: 2px;
  color: #918980;
}

.detail-total strong {
  font-family: "Cormorant Garamond", serif;
  font-size: 28px;
  font-weight: 500;
  color: #302b27;
}


/* ================================
   Empty
================================ */

.loading,
.empty {
  text-align: center;
  padding: 80px 20px;
  color: #918980;
}

.empty-icon {
  font-size: 32px;
  margin-bottom: 12px;
}

.empty h3 {
  margin: 0 0 8px;
  color: #514a44;
  font-weight: 500;
}

.empty p {
  margin: 0;
  font-size: 13px;
}


/* ================================
   Detail
================================ */

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee9e4;
}

.detail-header span:first-child {
  display: block;
  font-size: 12px;
  color: #999087;
  margin-bottom: 6px;
}

.detail-header strong {
  font-size: 18px;
  font-weight: 500;
}

.detail-meta {
  display: grid;
  grid-template-columns:
    repeat(2, 1fr);

  gap: 24px;

  padding-top: 24px;
}


/* ================================
   Responsive
================================ */

@media (max-width: 900px) {

  .stats {
    grid-template-columns:
      repeat(2, 1fr);
  }

  .order-info {
    grid-template-columns:
      repeat(2, 1fr);
  }

}

@media (max-width: 600px) {

  .takeaway-page {
    padding: 24px 18px;
  }

  .page-header h1 {
    font-size: 34px;
  }

  .filter-group {
    overflow-x: auto;
  }

  .order-card {
    padding: 20px;
  }

  .order-info {
    grid-template-columns: 1fr;
  }

}

</style>
