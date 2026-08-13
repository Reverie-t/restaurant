<template>
  <div class="page">

    <!-- Page Header -->
    <div class="page-header">
      <div>
        <div class="eyebrow">TABLE</div>
        <h1 class="page-title">餐桌管理</h1>
        <p class="page-desc">管理餐厅餐桌状态、区域与容量</p>
      </div>
      <el-button type="primary" @click="openAdd">新增餐桌</el-button>
    </div>

    <!-- Toolbar -->
    <div class="toolbar">
      <el-input
        v-model="searchForm.tableNo"
        placeholder="输入桌号搜索"
        clearable
        style="width: 180px;"
      />
      <el-input
        v-model="searchForm.area"
        placeholder="输入区域搜索"
        clearable
        style="width: 160px;"
      />
      <el-select
        v-model="searchForm.status"
        placeholder="选择状态"
        clearable
        style="width: 130px;"
      >
        <el-option label="空闲" :value="0" />
        <el-option label="用餐中" :value="1" />
        <el-option label="已预订" :value="2" />
      </el-select>
      <el-button type="primary" @click="loadData">查询</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>

    <!-- Content -->
    <div class="card">
      <el-table :data="tableData" v-loading="loading">

        <el-table-column prop="tableNo" label="桌号" width="100" />

        <el-table-column prop="capacity" label="容量" width="80" align="center">
          <template #default="scope">
            <span>{{ scope.row.capacity }}人</span>
          </template>
        </el-table-column>

        <el-table-column prop="area" label="区域" width="120" />

        <el-table-column prop="remark" label="备注" show-overflow-tooltip />

        <el-table-column prop="sort" label="排序" width="70" align="center" />

        <el-table-column label="状态" width="180" align="center">
          <template #default="scope">
            <span class="status-tag" :class="'status-' + scope.row.status">
              {{ statusName(scope.row.status) }}
            </span>
            <el-select
              v-model="scope.row.status"
              size="small"
              style="width: 90px; margin-left: 8px;"
              @change="changeStatus(scope.row)"
            >
              <el-option label="空闲" :value="0" />
              <el-option label="用餐中" :value="1" />
              <el-option label="已预订" :value="2" />
            </el-select>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="140" align="center">
          <template #default="scope">
            <button class="action-btn action-edit" @click="openEdit(scope.row)">编辑</button>
            <button class="action-btn action-del" @click="remove(scope.row)">删除</button>
          </template>
        </el-table-column>

      </el-table>
    </div>

    <!-- Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑餐桌' : '新增餐桌'"
      width="480px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="70px">

        <el-form-item label="桌号" prop="tableNo">
          <el-input v-model="form.tableNo" />
        </el-form-item>

        <el-form-item label="容量" prop="capacity">
          <el-input-number v-model="form.capacity" :min="1" style="width: 100%;" />
        </el-form-item>

        <el-form-item label="区域">
          <el-input v-model="form.area" />
        </el-form-item>

        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="0">空闲</el-radio>
            <el-radio :value="1">用餐中</el-radio>
            <el-radio :value="2">已预订</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" style="width: 100%;" />
        </el-form-item>

        <el-form-item label="备注">
          <el-input type="textarea" v-model="form.remark" :rows="3" />
        </el-form-item>

      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveTable">保存</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue"
import { getTableList, addTable, updateTable, deleteTable, updateTableStatus } from "@/api/table"
import { ElMessage, ElMessageBox } from "element-plus"

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const loading = ref(false)
const tableData = ref([])

const form = reactive({
  id: null,
  tableNo: "",
  capacity: 2,
  status: 0,
  area: "大厅",
  remark: "",
  sort: 0
})

const rules = {
  tableNo: [{ required: true, message: "请输入桌号", trigger: "blur" }],
  capacity: [{ required: true, message: "请输入人数", trigger: "blur" }]
}

const searchForm = reactive({
  tableNo: "",
  area: "",
  status: null
})

function openAdd() {
  isEdit.value = false
  Object.assign(form, {
    id: null, tableNo: "", capacity: 2, status: 0,
    area: "大厅", remark: "", sort: 0
  })
  formRef.value?.clearValidate()
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  Object.assign(form, JSON.parse(JSON.stringify(row)))
  formRef.value?.clearValidate()
  dialogVisible.value = true
}

function statusName(status) {
  if (status === 0) return "空闲"
  if (status === 1) return "用餐中"
  return "已预订"
}

async function changeStatus(row) {
  const res = await updateTableStatus(row.id, row.status)
  if (res.data.code === 200) {
    ElMessage.success("状态修改成功")
  } else {
    ElMessage.error(res.data.message)
    loadData()
  }
}

async function remove(row) {
  try {
    await ElMessageBox.confirm("确定删除该餐桌？", "提示", { type: "warning" })
    const res = await deleteTable(row.id)
    if (res.data.code === 200) {
      ElMessage.success("删除成功")
      loadData()
    } else {
      ElMessage.error(res.data.message)
    }
  } catch { }
}

async function saveTable() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  const res = isEdit.value ? await updateTable(form) : await addTable(form)
  if (res.data.code === 200) {
    ElMessage.success(isEdit.value ? "修改成功" : "新增成功")
    dialogVisible.value = false
    loadData()
  } else {
    ElMessage.error(res.data.message)
  }
}

async function loadData() {
  loading.value = true
  try {
    const res = await getTableList(searchForm)
    if (res.data.code === 200) {
      tableData.value = res.data.data
    }
  } finally {
    loading.value = false
  }
}

function resetSearch() {
  searchForm.tableNo = ""
  searchForm.area = ""
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

.status-tag {
  font-size: 12px;
  padding: 2px 10px;
}

.status-0 {
  color: var(--success);
  background: var(--success-bg);
}

.status-1 {
  color: var(--danger);
  background: var(--danger-bg);
}

.status-2 {
  color: var(--warning);
  background: var(--warning-bg);
}

/* ── Action buttons ── */
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
