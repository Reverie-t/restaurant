<template>
  <div class="page">

    <!-- Page Header -->
    <div class="page-header">
      <div>
        <div class="eyebrow">INGREDIENT</div>
        <h1 class="page-title">原材料管理</h1>
        <p class="page-desc">管理餐厅原材料库存、采购价格及供应商信息</p>
      </div>
      <el-button type="primary" @click="openAdd">新增原材料</el-button>
    </div>

    <!-- Toolbar -->
    <div class="toolbar">
      <el-input
        v-model="query.name"
        placeholder="输入原材料名称搜索"
        clearable
        style="width: 220px;"
        @keyup.enter="loadData"
      />
      <el-select
        v-model="query.category"
        placeholder="选择分类"
        clearable
        style="width: 140px;"
      >
        <el-option label="肉类" value="肉类" />
        <el-option label="蔬菜" value="蔬菜" />
        <el-option label="水果" value="水果" />
        <el-option label="乳制品" value="乳制品" />
        <el-option label="粮食" value="粮食" />
        <el-option label="饮品" value="饮品" />
        <el-option label="调味料" value="调味料" />
        <el-option label="其他" value="其他" />
      </el-select>
      <el-button type="primary" @click="loadData">查询</el-button>
      <el-button @click="resetQuery">重置</el-button>
    </div>

    <!-- Content -->
    <div class="card">
      <el-table :data="tableData" v-loading="loading">

        <el-table-column prop="id" label="ID" width="70" />

        <el-table-column prop="name" label="原材料" min-width="140">
          <template #default="{ row }">
            <span style="font-weight: 500;">{{ row.name }}</span>
            <span v-if="row.status === 0" class="status-off-label">已停用</span>
          </template>
        </el-table-column>

        <el-table-column prop="category" label="分类" width="100" />

        <el-table-column prop="unit" label="单位" width="70" align="center" />

        <el-table-column label="当前库存" width="130">
          <template #default="{ row }">
            <span :class="{ 'stock-warning': isLowStock(row) }">
              {{ row.stock ?? 0 }} {{ row.unit }}
            </span>
            <span v-if="isLowStock(row)" class="warning-tag">不足</span>
          </template>
        </el-table-column>

        <el-table-column label="安全库存" width="100">
          <template #default="{ row }">
            {{ row.safeStock ?? 0 }} {{ row.unit }}
          </template>
        </el-table-column>

        <el-table-column label="采购单价" width="100" align="right">
          <template #default="{ row }">
            <span class="price-text">¥{{ row.purchasePrice ?? '—' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="supplier" label="供应商" min-width="140" show-overflow-tooltip />

        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="changeStatus(row)"
            />
          </template>
        </el-table-column>

        <el-table-column label="操作" width="140" align="center" fixed="right">
          <template #default="{ row }">
            <button class="action-btn action-edit" @click="openEdit(row)">编辑</button>
            <button class="action-btn action-del" @click="remove(row)">删除</button>
          </template>
        </el-table-column>

      </el-table>
    </div>

    <!-- Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="560px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">

        <el-form-item label="原材料名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>

        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" style="width: 100%;">
            <el-option label="肉类" value="肉类" />
            <el-option label="蔬菜" value="蔬菜" />
            <el-option label="水果" value="水果" />
            <el-option label="乳制品" value="乳制品" />
            <el-option label="粮食" value="粮食" />
            <el-option label="饮品" value="饮品" />
            <el-option label="调味料" value="调味料" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>

        <el-form-item label="计量单位" prop="unit">
          <el-select v-model="form.unit" style="width: 100%;">
            <el-option label="kg" value="kg" />
            <el-option label="g" value="g" />
            <el-option label="L" value="L" />
            <el-option label="ml" value="ml" />
            <el-option label="个" value="个" />
            <el-option label="瓶" value="瓶" />
            <el-option label="袋" value="袋" />
            <el-option label="盒" value="盒" />
          </el-select>
        </el-form-item>

        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 0 20px;">
          <el-form-item label="当前库存" prop="stock">
            <el-input-number v-model="form.stock" :min="0" :precision="2" style="width: 100%;" />
          </el-form-item>
          <el-form-item label="安全库存" prop="safeStock">
            <el-input-number v-model="form.safeStock" :min="0" :precision="2" style="width: 100%;" />
          </el-form-item>
        </div>

        <el-form-item label="采购单价" prop="purchasePrice">
          <el-input-number v-model="form.purchasePrice" :min="0" :precision="2" :step="1" style="width: 100%;" />
        </el-form-item>

        <el-form-item label="供应商" prop="supplier">
          <el-input v-model="form.supplier" />
        </el-form-item>

        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" />
        </el-form-item>

      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue"
import { ElMessage, ElMessageBox } from "element-plus"
import request from "@/utils/request"

const query = reactive({ name: "", category: "" })
const tableData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref("新增原材料")
const saving = ref(false)
const formRef = ref()

const createForm = () => ({
  id: null, name: "", category: "", unit: "",
  stock: 0, safeStock: 0, purchasePrice: 0,
  supplier: "", status: 1, remark: ""
})

const form = reactive(createForm())

const rules = {
  name: [{ required: true, message: "请输入原材料名称", trigger: "blur" }],
  category: [{ required: true, message: "请选择分类", trigger: "change" }],
  unit: [{ required: true, message: "请选择计量单位", trigger: "change" }]
}

function isLowStock(row) {
  if (row.stock == null || row.safeStock == null) return false
  return Number(row.stock) < Number(row.safeStock)
}

async function loadData() {
  loading.value = true
  try {
    const res = await request.get("/ingredient", { params: query })
    if (res.data.code === 200) {
      tableData.value = res.data.data || []
    } else {
      ElMessage.error(res.data.message || "查询失败")
    }
  } catch {
    ElMessage.error("查询原材料失败")
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  query.name = ""
  query.category = ""
  loadData()
}

function openAdd() {
  Object.assign(form, createForm())
  dialogTitle.value = "新增原材料"
  formRef.value?.clearValidate()
  dialogVisible.value = true
}

function openEdit(row) {
  Object.assign(form, JSON.parse(JSON.stringify(row)))
  dialogTitle.value = "编辑原材料"
  dialogVisible.value = true
}

async function save() {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    const res = form.id
      ? await request.put("/ingredient", form)
      : await request.post("/ingredient", form)
    if (res.data.code === 200) {
      ElMessage.success(form.id ? "修改成功" : "新增成功")
      dialogVisible.value = false
      await loadData()
    } else {
      ElMessage.error(res.data.message || "保存失败")
    }
  } catch {
    ElMessage.error("保存失败")
  } finally {
    saving.value = false
  }
}

async function remove(row) {
  try {
    await ElMessageBox.confirm(`确定要删除「${row.name}」吗？`, "提示", { type: "warning" })
    const res = await request.delete(`/ingredient/${row.id}`)
    if (res.data.code === 200) {
      ElMessage.success("删除成功")
      await loadData()
    } else {
      ElMessage.error(res.data.message || "删除失败")
    }
  } catch (error) {
    if (error !== "cancel") ElMessage.error("删除失败")
  }
}

async function changeStatus(row) {
  try {
    const res = await request.put("/ingredient/status", null, {
      params: { id: row.id, status: row.status }
    })
    if (res.data.code === 200) {
      ElMessage.success(row.status === 1 ? "已启用" : "已停用")
    } else {
      row.status = row.status === 1 ? 0 : 1
      ElMessage.error(res.data.message || "状态修改失败")
    }
  } catch {
    row.status = row.status === 1 ? 0 : 1
    ElMessage.error("状态修改失败")
  }
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

.status-off-label {
  font-size: 11px;
  padding: 1px 6px;
  margin-left: 6px;
  color: var(--text-4);
  background: #f1f1f1;
}

.stock-warning {
  color: var(--warning);
  font-weight: 500;
}

.warning-tag {
  font-size: 11px;
  padding: 1px 6px;
  margin-left: 6px;
  background: var(--warning-bg);
  color: var(--warning);
}

.price-text {
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
