<template>
  <div class="page">

    <!-- Page Header -->
    <div class="page-header">
      <div>
        <div class="eyebrow">CATEGORY</div>
        <h1 class="page-title">分类管理</h1>
        <p class="page-desc">管理菜品分类、排序与启用状态</p>
      </div>
      <el-button type="primary" @click="openAdd">新增分类</el-button>
    </div>

    <!-- Content -->
    <div class="card">
      <el-table :data="tableData" v-loading="loading">

        <el-table-column prop="id" label="ID" width="70" />

        <el-table-column prop="name" label="分类名称" />

        <el-table-column prop="sort" label="排序" width="80" align="center" />

        <el-table-column label="状态" width="80" align="center">
          <template #default="scope">
            <span class="status-tag" :class="scope.row.status === 1 ? 'status-on' : 'status-off'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="140" align="center">
          <template #default="scope">
            <button class="action-btn action-edit" @click="openEdit(scope.row)">编辑</button>
            <el-popconfirm title="确定删除该分类？" @confirm="remove(scope.row.id)">
              <template #reference>
                <button class="action-btn action-del">删除</button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>

      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[5,10,20,50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </div>

    <!-- Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑分类' : '新增分类'"
      width="440px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="70px">

        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>

        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" style="width: 100%;" />
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>

      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { getCategoryList, addCategory, updateCategory, deleteCategory } from '@/api/category'
import { ElMessage } from 'element-plus'

const tableData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const form = reactive({
  id: null,
  name: '',
  sort: 0,
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
  sort: [{ required: true, message: '请输入排序', trigger: 'blur' }]
}

async function loadData() {
  loading.value = true
  try {
    const res = await getCategoryList({ pageNum: pageNum.value, pageSize: pageSize.value })
    if (res.data.code === 200) {
      tableData.value = res.data.data.records
      total.value = res.data.data.total
    }
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

function openAdd() {
  isEdit.value = false
  Object.assign(form, { id: null, name: '', sort: 0, status: 1 })
  formRef.value?.clearValidate()
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  Object.assign(form, JSON.parse(JSON.stringify(row)))
  dialogVisible.value = true
}

function save() {
  formRef.value.validate(async valid => {
    if (!valid) return
    const res = isEdit.value ? await updateCategory(form) : await addCategory(form)
    if (res.data.code === 200) {
      ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
      dialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.data.message)
    }
  })
}

async function remove(id) {
  const res = await deleteCategory(id)
  if (res.data.code === 200) {
    ElMessage.success('删除成功')
    loadData()
  } else {
    ElMessage.error(res.data.message)
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

.card {
  background: #fff;
  border: 1px solid var(--border);
}

.pagination {
  padding: 16px 20px;
  display: flex;
  justify-content: flex-end;
}

.status-tag {
  font-size: 12px;
  padding: 2px 10px;
}

.status-on {
  color: var(--success);
  background: var(--success-bg);
}

.status-off {
  color: var(--danger);
  background: var(--danger-bg);
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
