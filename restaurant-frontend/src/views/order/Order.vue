<template>
  <div class="page">

    <!-- Page Header -->
    <div class="page-header">
      <div>
        <div class="eyebrow">ORDER</div>
        <h1 class="page-title">订单管理</h1>
        <p class="page-desc">管理所有堂食、外带和外卖订单</p>
      </div>
      <el-button type="primary" @click="openAdd">新增订单</el-button>
    </div>

    <!-- Toolbar -->
    <div class="toolbar">
      <el-select
        v-model="searchForm.orderType"
        placeholder="订单类型"
        clearable
        style="width: 140px;"
      >
        <el-option label="堂食" :value="1" />
        <el-option label="外带" :value="2" />
        <el-option label="外卖" :value="3" />
      </el-select>
      <el-select
        v-model="searchForm.status"
        placeholder="订单状态"
        clearable
        style="width: 140px;"
      >
        <el-option label="待确认" :value="1" />
        <el-option label="制作中" :value="2" />
        <el-option label="待取餐/待上菜" :value="3" />
        <el-option label="已完成" :value="4" />
        <el-option label="已取消" :value="5" />
      </el-select>
      <el-button type="primary" @click="loadData">查询</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>

    <!-- Content -->
    <div class="card">
      <el-table :data="tableData" v-loading="loading">

        <el-table-column prop="orderNo" label="订单编号" min-width="160" />

        <el-table-column prop="orderTypeName" label="订单类型" width="100" align="center">
          <template #default="scope">
            <span class="type-tag" :class="'otype-' + scope.row.orderType">
              {{ scope.row.orderTypeName }}
            </span>
          </template>
        </el-table-column>

        <el-table-column prop="totalAmount" label="金额" width="110" align="right">
          <template #default="scope">
            <span class="amount-text">¥{{ scope.row.totalAmount }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="statusName" label="状态" width="110" align="center">
          <template #default="scope">
            <span class="status-tag" :class="'ostatus-' + scope.row.status">
              {{ scope.row.statusName }}
            </span>
          </template>
        </el-table-column>

        <el-table-column prop="tableNo" label="桌号" width="80" align="center">
          <template #default="scope">
            {{ scope.row.tableNo || "-" }}
          </template>
        </el-table-column>

        <el-table-column prop="pickupName" label="取餐人" width="100">
          <template #default="scope">
            {{ scope.row.pickupName || "-" }}
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="下单时间" width="170" />

        <el-table-column label="操作" width="220" align="center">
          <template #default="scope">
            <button class="action-btn action-view" @click="showDetail(scope.row)">查看详情</button>
            <button
              v-if="scope.row.status >= 1 && scope.row.status < 4"
              class="action-btn action-next"
              @click="nextStatus(scope.row)"
            >
              {{ nextStatusText(scope.row) }}
            </button>
            <button
              v-if="scope.row.status === 1"
              class="action-btn action-cancel"
              @click="cancelOrder(scope.row)"
            >
              取消订单
            </button>
          </template>
        </el-table-column>

      </el-table>
    </div>

    <!-- Add Order Dialog -->
    <el-dialog
      v-model="addVisible"
      title="新增订单"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="form" label-width="90px">

        <el-form-item label="订单类型">
          <el-radio-group v-model="form.orderType">
            <el-radio :value="1">堂食</el-radio>
            <el-radio :value="2">外带</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 堂食信息 -->
        <template v-if="form.orderType === 1">
          <el-form-item label="用餐人数">
            <el-input-number v-model="form.people" :min="1" :max="20" />
          </el-form-item>
          <el-form-item label="餐桌">
            <el-select v-model="form.tableId" placeholder="请选择餐桌" style="width: 100%;">
              <el-option
                v-for="item in tableList"
                :key="item.id"
                :label="`${item.tableNo}（${item.capacity}人）`"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </template>

        <!-- 外带信息 -->
        <template v-if="form.orderType === 2">
          <el-form-item label="取餐人">
            <el-input v-model="form.pickupName" placeholder="请输入取餐人姓名" />
          </el-form-item>
        </template>

        <el-form-item label="选择菜品">
          <el-select
            v-model="selectedDishId"
            placeholder="请选择菜品"
            style="width: 100%;"
            @change="addDishToOrder"
          >
            <el-option
              v-for="dish in dishList"
              :key="dish.id"
              :label="`${dish.name} ¥${dish.price}`"
              :value="dish.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="订单菜品">
          <el-table :data="form.details" style="width: 100%;">
            <el-table-column prop="dishName" label="菜品" />
            <el-table-column prop="dishPrice" label="单价" width="100">
              <template #default="scope">¥{{ scope.row.dishPrice }}</template>
            </el-table-column>
            <el-table-column label="数量" width="150">
              <template #default="scope">
                <el-input-number v-model="scope.row.quantity" :min="1" @change="calculateTotal" />
              </template>
            </el-table-column>
            <el-table-column label="小计" width="100">
              <template #default="scope">
                ¥{{ (scope.row.dishPrice * scope.row.quantity).toFixed(2) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80" align="center">
              <template #default="scope">
                <el-button link type="danger" @click="removeDish(scope.$index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>

        <el-form-item label="总金额">
          <span class="total-price">¥{{ form.totalAmount.toFixed(2) }}</span>
        </el-form-item>

      </el-form>

      <template #footer>
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" @click="saveOrder">提交订单</el-button>
      </template>
    </el-dialog>

    <!-- Order Detail Dialog -->
    <el-dialog
      v-model="detailVisible"
      title="订单详情"
      width="600px"
      :close-on-click-modal="false"
    >
      <!-- 基本信息 -->
      <div class="detail-section">
        <div class="detail-section-title">基本信息</div>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单编号">{{ currentOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="订单类型">{{ currentOrder.orderTypeName }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <span class="status-tag" :class="'ostatus-' + currentOrder.status">
              {{ currentOrder.statusName }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="桌号">{{ currentOrder.tableNo || "-" }}</el-descriptions-item>
          <el-descriptions-item label="取餐人">{{ currentOrder.pickupName || "-" }}</el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ currentOrder.createTime }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 菜品明细 -->
      <div class="detail-section">
        <div class="detail-section-title">菜品明细</div>
        <el-table :data="detailList">
          <el-table-column prop="dishName" label="菜品" />
          <el-table-column prop="quantity" label="数量" width="80" align="center" />
          <el-table-column prop="dishPrice" label="单价" width="100" align="right">
            <template #default="scope">
              <span class="amount-text">¥{{ scope.row.dishPrice }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="subtotal" label="小计" width="100" align="right">
            <template #default="scope">
              <span class="amount-text">¥{{ scope.row.subtotal }}</span>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 合计 -->
      <div class="order-total">
        合计：<span>¥{{ currentOrder.totalAmount }}</span>
      </div>

      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { getOrderList, updateOrderStatus, addOrder, getOrderDetail } from "@/api/order"
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTableList } from "@/api/table"
import { getDishList } from "@/api/dish"

const tableData = ref([])
const searchForm = reactive({ orderType: null, status: null })
const loading = ref(false)
const detailVisible = ref(false)
const detailList = ref([])
const currentOrder = ref({})
const addVisible = ref(false)
const formRef = ref()
const selectedDishId = ref(null)
const dishList = ref([])
const tableList = ref([])

const form = ref({
  orderType: 1,
  tableId: null,
  people: 1,
  pickupName: "",
  totalAmount: 0,
  details: []
})

function openAdd() {
  form.value = {
    orderType: 1, tableId: null, people: 1,
    pickupName: "", totalAmount: 0, details: []
  }
  selectedDishId.value = null
  addVisible.value = true
  loadDishList()
  loadTableList()
}

async function loadDishList() {
  const res = await getDishList({ pageSize: 100 })
  if (res.data.code === 200) {
    dishList.value = res.data.data.records
  }
}

function nextStatusText(row) {
  if (row.status === 1) return "开始制作"
  if (row.status === 2) return row.orderTypeName === "堂食" ? "开始上菜" : "待取餐"
  if (row.status === 3) return "完成订单"
  return ""
}

async function nextStatus(row) {
  let ns
  if (row.status === 1) ns = 2
  else if (row.status === 2) ns = 3
  else if (row.status === 3) ns = 4
  else return
  try {
    const res = await updateOrderStatus(row.id, ns)
    if (res.data.code === 200) {
      ElMessage.success("状态修改成功")
      loadData()
    } else {
      ElMessage.error(res.data.message)
    }
  } catch {
    ElMessage.error("状态修改失败")
  }
}

async function cancelOrder(row) {
  try {
    await ElMessageBox.confirm("确定取消这个订单吗？", "提示", { type: "warning" })
    const res = await updateOrderStatus(row.id, 5)
    if (res.data.code === 200) {
      ElMessage.success("订单已取消")
      loadData()
    } else {
      ElMessage.error(res.data.message)
    }
  } catch { }
}

async function loadTableList() {
  const res = await getTableList({ status: 0 })
  if (res.data.code === 200) {
    tableList.value = res.data.data
  }
}

function removeDish(index) {
  form.value.details.splice(index, 1)
  calculateTotal()
}

function calculateTotal() {
  let total = 0
  form.value.details.forEach(item => {
    total += Number(item.dishPrice) * Number(item.quantity)
  })
  form.value.totalAmount = Number(total.toFixed(2))
}

async function saveOrder() {
  if (form.value.details.length === 0) {
    ElMessage.warning("请至少选择一道菜")
    return
  }
  if (form.value.orderType === 1 && !form.value.tableId) {
    ElMessage.warning("请选择餐桌")
    return
  }
  if (form.value.orderType === 2 && !form.value.pickupName) {
    ElMessage.warning("请输入取餐人")
    return
  }
  try {
    const res = await addOrder(form.value)
    if (res.data.code === 200) {
      ElMessage.success("订单创建成功")
      addVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (e) {
    ElMessage.error(e.response?.data?.message || "订单创建失败")
  }
}

function addDishToOrder(dishId) {
  const dish = dishList.value.find(item => item.id === dishId)
  if (!dish) return
  const exist = form.value.details.find(item => item.dishId === dishId)
  if (exist) {
    exist.quantity++
  } else {
    form.value.details.push({
      dishId: dish.id,
      dishName: dish.name,
      dishPrice: dish.price,
      quantity: 1
    })
  }
  selectedDishId.value = null
  calculateTotal()
}

async function showDetail(row) {
  currentOrder.value = JSON.parse(JSON.stringify(row))
  detailVisible.value = true
  try {
    const res = await getOrderDetail(row.id)
    if (res.data.code === 200) {
      detailList.value = res.data.data
    } else {
      ElMessage.error(res.data.message)
    }
  } catch {
    ElMessage.error("获取订单详情失败")
  }
}

async function loadData() {
  loading.value = true
  try {
    const res = await getOrderList(searchForm)
    if (res.data.code === 200) {
      tableData.value = res.data.data
    }
  } finally {
    loading.value = false
  }
}

function resetSearch() {
  searchForm.orderType = null
  searchForm.status = null
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.page {
  padding: 36px 42px;
  min-height: 100%;
  background: var(--surface-page);
}

.page-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 28px;
}

.eyebrow {
  font-size: 11px;
  letter-spacing: 3px;
  color: var(--text-3);
  margin-bottom: 8px;
  font-weight: 500;
}

.page-title {
  font-family: "Cormorant Garamond", serif;
  font-size: 32px;
  font-weight: 500;
  color: var(--text-1);
  margin-bottom: 4px;
  line-height: 1.2;
}

.page-desc {
  font-size: 13px;
  color: var(--text-3);
}

.toolbar {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}

.card {
  background: #fff;
  border: 1px solid var(--border);
}

/* ── Order type tags ── */
.type-tag {
  font-size: 12px;
  padding: 2px 10px;
}

.otype-1 { color: var(--text-1); background: #eee9e3; }
.otype-2 { color: #4a7c6f; background: #e8f0ee; }
.otype-3 { color: #8b7a3e; background: #f5f0e4; }

/* ── Status tags ── */
.status-tag {
  font-size: 12px;
  padding: 2px 10px;
}

.ostatus-1 { color: var(--warning); background: var(--warning-bg); }
.ostatus-2 { color: #4a6fa5; background: #e8edf5; }
.ostatus-3 { color: #4a7c6f; background: #e8f0ee; }
.ostatus-4 { color: var(--success); background: var(--success-bg); }
.ostatus-5 { color: var(--danger); background: var(--danger-bg); }

/* ── Action buttons ── */
.action-btn {
  font-size: 12px;
  padding: 4px 12px;
  border: none;
  cursor: pointer;
  font-weight: 400;
  transition: all 0.15s ease;
}

.action-view {
  color: var(--text-2);
  background: transparent;
  border-bottom: 1px solid var(--border-faint);
}

.action-view:hover {
  color: var(--text-1);
  border-bottom-color: var(--text-1);
}

.action-next {
  color: #fff;
  background: #3d3731;
  margin-left: 6px;
}

.action-next:hover {
  background: #524b44;
}

.action-cancel {
  color: var(--danger);
  background: transparent;
  border-bottom: 1px solid var(--border-faint);
  margin-left: 6px;
}

.action-cancel:hover {
  border-bottom-color: var(--danger);
}

.amount-text {
  font-family: "Cormorant Garamond", serif;
  font-size: 15px;
  font-weight: 500;
  color: var(--text-1);
}

.total-price {
  font-family: "Cormorant Garamond", serif;
  font-size: 22px;
  font-weight: 500;
  color: var(--text-1);
}

/* ── Detail dialog ── */
.detail-section {
  margin-bottom: 24px;
}

.detail-section-title {
  font-size: 13px;
  font-weight: 500;
  color: var(--text-2);
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--border-faint);
}

.order-total {
  text-align: right;
  font-size: 14px;
  color: var(--text-2);
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid var(--border-faint);
}

.order-total span {
  font-family: "Cormorant Garamond", serif;
  font-size: 22px;
  font-weight: 500;
  color: var(--text-1);
  margin-left: 4px;
}
</style>
