<template>

  <div class="order-container">


    <el-card>

      <template #header>

        <div class="header">

          <span>订单管理</span>

          <el-button
            type="primary"
            @click="openAdd"
          >
            新增订单
          </el-button>

        </div>

      </template>

      <el-row
        :gutter="20"
        style="margin-bottom:20px"
      >

        <!-- 订单类型 -->

        <el-col :span="6">

          <el-select
            v-model="searchForm.orderType"
            placeholder="订单类型"
            clearable
            style="width:100%"
          >

            <el-option
              label="堂食"
              :value="1"
            />

            <el-option
              label="外带"
              :value="2"
            />

            <el-option
              label="外卖"
              :value="3"
            />

          </el-select>

        </el-col>


        <!-- 订单状态 -->

        <el-col :span="6">

          <el-select
            v-model="searchForm.status"
            placeholder="订单状态"
            clearable
            style="width:100%"
          >

            <el-option
              label="待确认"
              :value="1"
            />

            <el-option
              label="制作中"
              :value="2"
            />

            <el-option
              label="待取餐/待上菜"
              :value="3"
            />

            <el-option
              label="已完成"
              :value="4"
            />

            <el-option
              label="已取消"
              :value="5"
            />

          </el-select>

        </el-col>


        <!-- 查询 -->

        <el-col :span="6">

          <el-button
            type="primary"
            @click="loadData"
          >
            查询
          </el-button>

          <el-button
            @click="resetSearch"
          >
            重置
          </el-button>

        </el-col>

      </el-row>

      <el-table
        :data="tableData"
        border
        stripe
        v-loading="loading"
      >


        <el-table-column
          prop="orderNo"
          label="订单编号"
        />


        <el-table-column
          prop="orderTypeName"
          label="订单类型"
          width="100"
        />



        <el-table-column
          prop="totalAmount"
          label="金额"
          width="120"
        >

          <template #default="scope">

            ￥{{scope.row.totalAmount}}

          </template>

        </el-table-column>



        <el-table-column
          prop="statusName"
          label="状态"
          width="120"
        >

          <template #default="scope">

            <el-tag
              :type="tagType(scope.row.status)"
            >

              {{ scope.row.statusName }}

            </el-tag>

          </template>


        </el-table-column>



        <el-table-column
          prop="tableNo"
          label="桌号"
        />



        <el-table-column
          prop="pickupName"
          label="取餐人"
        />



        <el-table-column
          prop="createTime"
          label="下单时间"
        />

        <el-table-column
          label="操作"
          width="220">

          <template #default="scope">

            <el-button
              link
              @click="showDetail(scope.row)"
            >
              查看详情
            </el-button>

            <el-button
              v-if="scope.row.status >= 1 && scope.row.status < 4"
              link
              type="primary"
              @click="nextStatus(scope.row)"
            >
              {{ nextStatusText(scope.row) }}
            </el-button>

            <el-button
              v-if="scope.row.status === 1"
              link
              type="danger"
              @click="cancelOrder(scope.row)"
            >
              取消订单
            </el-button>

          </template>

        </el-table-column>



      </el-table>


    </el-card>
    <el-dialog
      v-model="addVisible"
      title="新增订单"
      width="700px"
    ><el-form
      ref="formRef"
      :model="form"
      label-width="90px"
    >

      <!-- 订单类型 -->

      <el-form-item label="订单类型">

        <el-radio-group v-model="form.orderType">

          <el-radio :value="1">
            堂食
          </el-radio>

          <el-radio :value="2">
            外带
          </el-radio>

        </el-radio-group>

      </el-form-item>


      <!-- 堂食信息 -->

      <template v-if="form.orderType === 1">

        <el-form-item label="用餐人数">

          <el-input-number
            v-model="form.people"
            :min="1"
            :max="20"
          />

        </el-form-item>


        <el-form-item label="餐桌">

          <el-select
            v-model="form.tableId"
            placeholder="请选择餐桌"
            style="width:100%"
          >

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

          <el-input
            v-model="form.pickupName"
            placeholder="请输入取餐人姓名"
          />

        </el-form-item>

      </template>
      <el-form-item label="选择菜品">

        <el-select
          v-model="selectedDishId"
          placeholder="请选择菜品"
          style="width:100%"
          @change="addDishToOrder"
        >

          <el-option
            v-for="dish in dishList"
            :key="dish.id"
            :label="`${dish.name} ￥${dish.price}`"
            :value="dish.id"
          />

        </el-select>

      </el-form-item>
      <el-form-item label="订单菜品">

        <el-table
          :data="form.details"
          border
          style="width:100%"
        >

          <el-table-column
            prop="dishName"
            label="菜品"
          />

          <el-table-column
            prop="dishPrice"
            label="单价"
            width="100"
          />

          <el-table-column
            label="数量"
            width="150"
          >

            <template #default="scope">

              <el-input-number
                v-model="scope.row.quantity"
                :min="1"
                @change="calculateTotal"
              />

            </template>

          </el-table-column>


          <el-table-column
            label="小计"
            width="100"
          >

            <template #default="scope">

              ￥{{
                (
                  scope.row.dishPrice *
                  scope.row.quantity
                ).toFixed(2)
              }}

            </template>

          </el-table-column>


          <el-table-column
            label="操作"
            width="80"
          >

            <template #default="scope">

              <el-button
                link
                type="danger"
                @click="removeDish(scope.$index)"
              >
                删除
              </el-button>

            </template>

          </el-table-column>

        </el-table>

      </el-form-item>


      <!-- 总金额 -->

      <el-form-item label="总金额">

    <span class="total-price">
      ￥{{ form.totalAmount.toFixed(2) }}
    </span>

      </el-form-item>

    </el-form>
      <template #footer>

        <el-button
          @click="addVisible = false"
        >
          取消
        </el-button>

        <el-button
          type="primary"
          @click="saveOrder"
        >
          提交订单
        </el-button>

      </template>

    </el-dialog>


  </div>


  <el-dialog
    v-model="detailVisible"
    title="订单详情"
    width="700px"
  >

    <!-- 订单基本信息 -->

    <el-descriptions
      :column="2"
      border
      style="margin-bottom:20px"
    >

      <el-descriptions-item label="订单编号">
        {{ currentOrder.orderNo }}
      </el-descriptions-item>

      <el-descriptions-item label="订单类型">
        {{ currentOrder.orderTypeName }}
      </el-descriptions-item>

      <el-descriptions-item label="订单状态">
        <el-tag :type="tagType(currentOrder.status)">
          {{ currentOrder.statusName }}
        </el-tag>
      </el-descriptions-item>

      <el-descriptions-item label="桌号">
        {{ currentOrder.tableNo || "-" }}
      </el-descriptions-item>

      <el-descriptions-item label="取餐人">
        {{ currentOrder.pickupName || "-" }}
      </el-descriptions-item>

      <el-descriptions-item label="下单时间">
        {{ currentOrder.createTime }}
      </el-descriptions-item>

    </el-descriptions>


    <!-- 菜品明细 -->

    <el-table
      :data="detailList"
      border
      stripe
    >

      <el-table-column
        prop="dishName"
        label="菜品"
      />

      <el-table-column
        prop="quantity"
        label="数量"
        width="80"
        align="center"
      />

      <el-table-column
        prop="dishPrice"
        label="单价"
        width="100"
      />

      <el-table-column
        prop="subtotal"
        label="小计"
        width="100"
      />

    </el-table>


    <!-- 总金额 -->

    <div class="order-total">

      合计：

      <span>
      ￥{{ currentOrder.totalAmount }}
    </span>

    </div>


    <template #footer>

      <el-button
        @click="detailVisible = false"
      >
        关闭
      </el-button>

    </template>

  </el-dialog>
</template>



<script setup>

import {
  ref,
  onMounted, reactive
} from 'vue'


import {
  getOrderList,updateOrderStatus,addOrder
} from "@/api/order"

import {
  getOrderDetail
} from "@/api/order"
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTableList } from "@/api/table"
import { getDishList } from "@/api/dish"



const tableData = ref([])

const searchForm = reactive({
  orderType: null,
  status: null
})

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
    orderType: 1,
    tableId: null,
    people: 1,
    pickupName: "",
    totalAmount: 0,
    details: []
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

  if (row.status === 1) {
    return "开始制作"
  }

  if (row.status === 2) {
    return row.orderTypeName === "堂食"
      ? "开始上菜"
      : "待取餐"
  }

  if (row.status === 3) {
    return "完成订单"
  }

  return ""
}
async function nextStatus(row) {

  let nextStatus

  if (row.status === 1) {
    nextStatus = 2
  } else if (row.status === 2) {
    nextStatus = 3
  } else if (row.status === 3) {
    nextStatus = 4
  } else {
    return
  }

  try {

    const res = await updateOrderStatus(
      row.id,
      nextStatus
    )

    if (res.data.code === 200) {

      ElMessage.success("状态修改成功")

      loadData()

    } else {

      ElMessage.error(res.data.message)

    }

  } catch (e) {

    console.error(e)

    ElMessage.error("状态修改失败")

  }
}
async function cancelOrder(row) {

  try {

    await ElMessageBox.confirm(
      "确定取消这个订单吗？",
      "提示",
      {
        type: "warning"
      }
    )

    const res = await updateOrderStatus(
      row.id,
      5
    )

    if (res.data.code === 200) {

      ElMessage.success("订单已取消")

      loadData()

    } else {

      ElMessage.error(res.data.message)

    }

  } catch (e) {

    // 用户点击取消，不需要提示
  }
}
async function loadTableList() {

  const res = await getTableList({
    status: 0
  })

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

    total +=
      Number(item.dishPrice) *
      Number(item.quantity)

  })

  form.value.totalAmount = Number(
    total.toFixed(2)
  )
}
async function saveOrder() {

  if (form.value.details.length === 0) {

    ElMessage.warning("请至少选择一道菜")

    return
  }


  if (
    form.value.orderType === 1 &&
    !form.value.tableId
  ) {

    ElMessage.warning("请选择餐桌")

    return
  }


  if (
    form.value.orderType === 2 &&
    !form.value.pickupName
  ) {

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

    console.error(e)

    ElMessage.error(
      e.response?.data?.message ||
      "订单创建失败"
    )

  }

}
function addDishToOrder(dishId) {

  const dish = dishList.value.find(
    item => item.id === dishId
  )

  if (!dish) {
    return
  }


  // 看看是不是已经添加过

  const exist = form.value.details.find(
    item => item.dishId === dishId
  )


  if (exist) {

    // 已经存在 → 数量 +1

    exist.quantity++

  } else {

    // 第一次添加

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
function tagType(status){

  switch(status){

    case 1:
      return "warning"

    case 2:
      return "primary"

    case 3:
      return "success"

    case 4:
      return ""

    case 5:
      return "danger"

    default:
      return "info"

  }

}



async function showDetail(row) {

  currentOrder.value = row

  detailVisible.value = true

  try {

    const res = await getOrderDetail(row.id)

    if (res.data.code === 200) {

      detailList.value = res.data.data

    } else {

      ElMessage.error(res.data.message)

    }

  } catch (e) {

    console.error(e)

    ElMessage.error("获取订单详情失败")

  }

}
async function loadData(){

  loading.value = true

  try {

    const res = await getOrderList(searchForm)

    if(res.data.code === 200){

      tableData.value = res.data.data

    }

  } finally {

    loading.value = false

  }

}
function resetSearch(){

  searchForm.orderType = null

  searchForm.status = null

  loadData()

}



onMounted(()=>{


  loadData()


})


</script>



<style scoped>
.order-container {
  padding: 0;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header span {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-1);
}

.el-row {
  background: var(--surface-raised);
  padding: var(--sp-4);
  border-radius: var(--radius-md);
  margin-bottom: var(--sp-5) !important;
  border: 1px solid var(--border-light);
}

.order-total {
  margin-top: var(--sp-5);
  text-align: right;
  font-size: 16px;
  font-weight: 600;
}

.order-total span {
  color: var(--danger);
  font-size: 20px;
}

.total-price {
  font-size: 18px;
  font-weight: 700;
  color: var(--danger);
}
</style>
