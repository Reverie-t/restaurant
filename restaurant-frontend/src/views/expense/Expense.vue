<template>
  <div class="page">

    <!-- Page Header -->
    <div class="page-header">
      <div>
        <div class="eyebrow">EXPENSE</div>
        <h1 class="page-title">费用管理</h1>
        <p class="page-desc">记录和管理餐厅日常经营产生的各项费用</p>
      </div>
      <el-button type="primary" @click="openAdd">记录费用</el-button>
    </div>

    <!-- Stats -->
    <div class="stats">
      <div class="stat-card stat-total">
        <span>本月总费用</span>
        <strong>¥{{ Number(statistics.total).toFixed(2) }}</strong>
      </div>
      <div class="stat-card">
        <span>人工费</span>
        <strong>¥{{ Number(statistics.labor).toFixed(2) }}</strong>
      </div>
      <div class="stat-card">
        <span>水电燃气</span>
        <strong>¥{{ (Number(statistics.water) + Number(statistics.electricity) + Number(statistics.gas)).toFixed(2) }}</strong>
      </div>
      <div class="stat-card">
        <span>行政及其他</span>
        <strong>¥{{ (Number(statistics.administrative) + Number(statistics.other)).toFixed(2) }}</strong>
      </div>
    </div>

    <!-- Toolbar -->
    <div class="toolbar">
      <el-input
        v-model="query.name"
        placeholder="搜索费用名称"
        clearable
        style="width: 220px;"
        @keyup.enter="loadData"
      />
      <el-select
        v-model="query.type"
        placeholder="费用类型"
        clearable
        style="width: 140px;"
      >
        <el-option label="人工费" :value="1" />
        <el-option label="水费" :value="2" />
        <el-option label="电费" :value="3" />
        <el-option label="燃气费" :value="4" />
        <el-option label="行政管理费" :value="5" />
        <el-option label="其他" :value="6" />
      </el-select>
      <el-button type="primary" @click="loadData">查询</el-button>
      <el-button @click="resetQuery">重置</el-button>
    </div>

    <!-- Content -->
    <div class="card">
      <el-table :data="tableData" v-loading="loading">

        <el-table-column prop="name" label="费用名称" min-width="160" />

        <el-table-column prop="type" label="费用类型" width="120">
          <template #default="{ row }">
            <span class="type-tag" :class="'type-' + row.type">
              {{ getTypeName(row.type) }}
            </span>
          </template>
        </el-table-column>

        <el-table-column prop="amount" label="金额" width="120" align="right">
          <template #default="{ row }">
            <span class="amount-text">¥{{ Number(row.amount).toFixed(2) }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="expenseDate" label="发生日期" width="120" />

        <el-table-column prop="operator" label="经办人" width="100" />

        <el-table-column prop="remark" label="备注" min-width="160" show-overflow-tooltip />

        <el-table-column prop="createTime" label="创建时间" width="170" />

        <el-table-column label="操作" width="140" align="center" fixed="right">
          <template #default="{ row }">
            <button class="action-btn action-edit" @click="openEdit(row)">编辑</button>
            <button class="action-btn action-del" @click="handleDelete(row)">删除</button>
          </template>
        </el-table-column>

      </el-table>
    </div>

    <!-- Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑费用' : '记录费用'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">

        <el-form-item label="费用名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>

        <el-form-item label="费用类型" prop="type">
          <el-select v-model="form.type" style="width: 100%;">
            <el-option label="人工费" :value="1" />
            <el-option label="水费" :value="2" />
            <el-option label="电费" :value="3" />
            <el-option label="燃气费" :value="4" />
            <el-option label="行政管理费" :value="5" />
            <el-option label="其他" :value="6" />
          </el-select>
        </el-form-item>

        <el-form-item label="费用金额" prop="amount">
          <el-input v-model="form.amount" placeholder="请输入费用金额">
            <template #prefix>¥</template>
          </el-input>
        </el-form-item>

        <el-form-item label="发生日期" prop="expenseDate">
          <el-date-picker
            v-model="form.expenseDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择发生日期"
            style="width: 100%;"
          />
        </el-form-item>

        <el-form-item label="经办人" prop="operator">
          <el-input v-model="form.operator" />
        </el-form-item>

        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" />
        </el-form-item>

      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue"
import { ElMessage, ElMessageBox } from "element-plus"
import request from "@/utils/request"

const tableData = ref([])
const statistics = reactive({
  total: 0, labor: 0, water: 0,
  electricity: 0, gas: 0, administrative: 0, other: 0
})
const loading = ref(false)
const submitLoading = ref(false)
const query = reactive({ name: "", type: null })
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const form = reactive({
  id: null, name: "", type: null,
  amount: "", expenseDate: "", operator: "", remark: ""
})

const rules = {
  name: [{ required: true, message: "请输入费用名称", trigger: "blur" }],
  type: [{ required: true, message: "请选择费用类型", trigger: "change" }],
  amount: [{ required: true, message: "请输入费用金额", trigger: "blur" }],
  expenseDate: [{ required: true, message: "请选择发生日期", trigger: "change" }]
}

function getTypeName(type) {
  const map = { 1: "人工费", 2: "水费", 3: "电费", 4: "燃气费", 5: "行政管理费", 6: "其他" }
  return map[type] || "未知"
}

function resetForm() {
  Object.assign(form, {
    id: null, name: "", type: null,
    amount: "", expenseDate: "", operator: "", remark: ""
  })
}

async function loadData() {
  loading.value = true
  try {
    const res = await request.get("/expense", { params: query })
    if (res.data.code === 200) {
      tableData.value = res.data.data || []
    } else {
      ElMessage.error(res.data.message || "查询失败")
    }
  } catch {
    ElMessage.error("查询费用失败")
  } finally {
    loading.value = false
  }
}

async function loadStatistics() {
  try {
    const res = await request.get("/expense/statistics")
    if (res.data.code === 200) Object.assign(statistics, res.data.data)
  } catch { }
}

function resetQuery() {
  query.name = ""
  query.type = null
  loadData()
}

function openAdd() {
  isEdit.value = false
  resetForm()
  formRef.value?.clearValidate()
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  Object.assign(form, JSON.parse(JSON.stringify(row)))
  dialogVisible.value = true
}

async function submitForm() {
  if (!formRef.value) return
  try { await formRef.value.validate() } catch { return }
  submitLoading.value = true
  try {
    const res = isEdit.value
      ? await request.put("/expense", form)
      : await request.post("/expense", form)
    if (res.data.code === 200) {
      ElMessage.success(isEdit.value ? "修改成功" : "添加成功")
      dialogVisible.value = false
      loadData()
      loadStatistics()
    } else {
      ElMessage.error(res.data.message || "操作失败")
    }
  } catch {
    ElMessage.error("保存费用失败")
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定要删除「${row.name}」吗？`, "提示", { type: "warning" })
    const res = await request.delete(`/expense/${row.id}`)
    if (res.data.code === 200) {
      ElMessage.success("删除成功")
      loadData()
      loadStatistics()
    } else {
      ElMessage.error(res.data.message || "删除失败")
    }
  } catch (error) {
    if (error !== "cancel") ElMessage.error("删除失败")
  }
}

onMounted(() => {
  loadData()
  loadStatistics()
})
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

.stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
  margin-bottom: 28px;
}

.stat-card {
  background: #fff;
  padding: 22px;
  border: 1px solid var(--border-light);
}

.stat-card span {
  display: block;
  font-size: 12px;
  color: var(--text-4);
  margin-bottom: 10px;
}

.stat-card strong {
  font-family: "Cormorant Garamond", serif;
  font-size: 28px;
  font-weight: 500;
  color: var(--text-1);
}

.stat-total {
  background: #f7f5f1;
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

.type-tag {
  font-size: 12px;
  padding: 2px 10px;
  color: var(--text-2);
  background: #f5f3f0;
}

.type-1 { color: var(--text-1); background: #eee9e3; }
.type-2 { color: #4a7c6f; background: #e8f0ee; }
.type-3 { color: #8b7a3e; background: #f5f0e4; }
.type-4 { color: var(--danger); background: var(--danger-bg); }
.type-5 { color: var(--text-3); background: #f5f3f0; }
.type-6 { color: var(--text-4); background: #f5f3f0; }

.amount-text {
  font-family: "Cormorant Garamond", serif;
  font-size: 15px;
  font-weight: 500;
  color: var(--text-1);
}

.action-btn {
  font-size: 12px;
  padding: 4px 12px;
  border: none;
  cursor: pointer;
  font-weight: 400;
  transition: all 0.15s ease;
}

.action-edit {
  color: #fff;
  background: #3d3731;
}

.action-edit:hover {
  background: #524b44;
}

.action-del {
  color: var(--danger);
  background: transparent;
  border-bottom: 1px solid var(--border-faint);
  margin-left: 6px;
}

.action-del:hover {
  border-bottom-color: var(--danger);
}
</style>
