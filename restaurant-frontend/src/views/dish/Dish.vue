<template>
  <div class="page">

    <!-- Page Header -->
    <div class="page-header">
      <div class="header">
        <div class="eyebrow">DISH</div>
        <h1 class="page-title">菜品管理</h1>
        <p class="page-desc">管理菜单中的所有菜品信息</p>
      </div>
      <el-button type="primary" @click="openAdd">新增菜品</el-button>
    </div>

    <!-- Toolbar -->
    <div class="toolbar">
      <el-input
        v-model="searchForm.name"
        placeholder="输入菜品名称搜索"
        clearable
        style="width: 220px;"
        @keyup.enter="search"
      />
      <el-select
        v-model="searchForm.categoryId"
        placeholder="选择分类"
        clearable
        style="width: 160px;"
      >
        <el-option
          v-for="item in categoryList"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </el-select>
      <el-button type="primary" @click="search">查询</el-button>
      <el-button @click="resetSearch">重置</el-button>
      <el-button type="danger" @click="batchRemove" :disabled="selectedIds.length === 0">
        批量删除
      </el-button>
    </div>

    <!-- Content -->
    <div class="card">
      <el-table
        :data="tableData"
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="40" />

        <el-table-column label="菜品" min-width="280">
          <template #default="scope">
            <div class="dish-cell">
              <el-image
                :src="scope.row.image ? 'http://localhost:8080' + scope.row.image : '/images/default-dish.png'"
                class="dish-img"
                fit="cover"
                :preview-src-list="[scope.row.image ? 'http://localhost:8080' + scope.row.image : '/images/default-dish.png']"
                preview-teleported
              />
              <div class="dish-info">
                <div class="dish-name">{{ scope.row.name }}</div>
                <div class="dish-meta">
                  <span class="dish-category">{{ scope.row.categoryName }}</span>
                  <span v-if="scope.row.description" class="dish-desc">{{ scope.row.description }}</span>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="price" label="价格" width="100" align="right">
          <template #default="scope">
            <span class="price-text">¥{{ scope.row.price }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="sort" label="排序" width="70" align="center" />

        <el-table-column label="状态" width="80" align="center">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              @change="changeStatus(scope.row)"
            />
          </template>
        </el-table-column>

        <el-table-column label="操作" width="140" align="center">
          <template #default="scope">
            <button class="action-btn action-edit" @click="openEdit(scope.row)">编辑</button>
            <button class="action-btn action-del" @click="remove(scope.row)">删除</button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="page.pageNum"
          v-model:page-size="page.pageSize"
          :page-sizes="[5,10,20,50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="page.total"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </div>

    <!-- Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑菜品' : '新增菜品'"
      width="560px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">

        <el-form-item label="菜品名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>

        <el-form-item label="菜品图片">
          <el-upload
            :show-file-list="false"
            :before-upload="beforeUpload"
            :http-request="uploadFile"
          >
            <img
              v-if="form.image"
              :src="'http://localhost:8080' + form.image"
              style="width: 100px; height: 100px; object-fit: cover;"
            />
            <el-button v-else type="primary">上传图片</el-button>
          </el-upload>
        </el-form-item>

        <el-form-item label="所属分类" prop="categoryId">
          <el-select v-model="form.categoryId" style="width: 100%;">
            <el-option
              v-for="item in categoryList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="价格" prop="price">
          <el-input-number
            v-model="form.price"
            :min="0.01"
            :precision="2"
            :step="1"
            style="width: 100%;"
          />
        </el-form-item>

        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" style="width: 100%;" />
        </el-form-item>

        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">停售</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="菜品介绍">
          <el-input
            type="textarea"
            v-model="form.description"
            :rows="3"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveDish">保存</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import {
  getDishList, getSimpleCategory, addDish, updateDish,
  deleteDish, updateDishStatus, batchDeleteDish
} from '@/api/dish'
import { ElMessage, ElMessageBox } from 'element-plus'
import { uploadImage } from '@/api/upload'

const tableData = ref([])
const page = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const loading = ref(false)
const categoryList = ref([])
const searchForm = reactive({ name: '', categoryId: null })
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const selectedIds = ref([])

const form = reactive({
  id: null, name: '', categoryId: null, price: null,
  sort: 0, status: 1, description: '', image: ''
})

const rules = {
  name: [
    { required: true, message: '请输入菜品名称', trigger: 'blur' },
    { min: 2, max: 50, message: '菜品名称长度为2~50个字符', trigger: 'blur' }
  ],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  sort: [{ required: true, message: '请输入排序', trigger: 'blur' }]
}

function openEdit(row) {
  isEdit.value = true
  const cloned = JSON.parse(JSON.stringify(row))
  const matched = categoryList.value.find(item => item.name === row.categoryName)
  cloned.categoryId = matched?.id ?? null
  cloned.price = Number(cloned.price) || 0
  cloned.sort = Number(cloned.sort) || 0
  Object.assign(form, cloned)
  dialogVisible.value = true
  formRef.value?.clearValidate()
}

function openAdd() {
  isEdit.value = false
  Object.assign(form, {
    id: null, name: '', categoryId: null, price: null,
    sort: 0, status: 1, description: '', image: ''
  })
  formRef.value?.clearValidate()
  dialogVisible.value = true
}

function beforeUpload(file) {
  const allowedTypes = ['image/jpeg', 'image/png', 'image/webp']
  if (!allowedTypes.includes(file.type)) {
    ElMessage.error('仅支持 jpg、jpeg、png、webp 格式的图片')
    return false
  }
  if (file.size / 1024 / 1024 >= 5) {
    ElMessage.error('图片大小不能超过 5MB')
    return false
  }
  return true
}

async function uploadFile(option) {
  const res = await uploadImage(option.file)
  if (res.data.code === 200) {
    form.image = res.data.data
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(res.data.message)
  }
}

async function remove(row) {
  try {
    await ElMessageBox.confirm('确定删除该菜品？', '提示', { type: 'warning' })
    const res = await deleteDish(row.id)
    if (res.data.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    } else {
      ElMessage.error(res.data.message)
    }
  } catch { }
}

async function changeStatus(row) {
  const res = await updateDishStatus(row.id, row.status)
  if (res.data.code === 200) {
    ElMessage.success('状态修改成功')
  } else {
    ElMessage.error(res.data.message)
    loadData()
  }
}

function search() {
  page.pageNum = 1
  loadData()
}

function resetSearch() {
  searchForm.name = ''
  searchForm.categoryId = null
  page.pageNum = 1
  loadData()
}

function handleSelectionChange(rows) {
  selectedIds.value = rows.map(r => r.id)
}

async function batchRemove() {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请选择要删除的菜品')
    return
  }
  try {
    await ElMessageBox.confirm('确定删除选中的菜品吗？', '提示', { type: 'warning' })
    const res = await batchDeleteDish(selectedIds.value)
    if (res.data.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    }
  } catch { }
}

async function saveDish() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  const res = isEdit.value ? await updateDish(form) : await addDish(form)
  if (res.data.code === 200) {
    ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
    dialogVisible.value = false
    loadData()
  } else {
    ElMessage.error(res.data.message)
  }
}

async function loadCategory() {
  const res = await getSimpleCategory()
  if (res.data.code === 200) {
    categoryList.value = res.data.data
  }
}

async function loadData() {
  loading.value = true
  try {
    const res = await getDishList({
      pageNum: page.pageNum,
      pageSize: page.pageSize,
      name: searchForm.name,
      categoryId: searchForm.categoryId
    })
    if (res.data.code === 200) {
      tableData.value = res.data.data.records
      page.total = res.data.data.total
    }
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
  loadCategory()
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

.pagination {
  padding: 16px 20px;
  display: flex;
  justify-content: flex-end;
}

/* Dish cell */
.dish-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.dish-img {
  width: 56px;
  height: 56px;
  flex-shrink: 0;
}

.dish-info {
  flex: 1;
  min-width: 0;
}

.dish-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-1);
  line-height: 1.3;
  margin-bottom: 4px;
}

.dish-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  align-items: center;
}

.dish-category {
  font-size: 11px;
  color: var(--text-3);
  background: #f5f3f0;
  padding: 1px 8px;
}

.dish-desc {
  font-size: 12px;
  color: var(--text-4);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 180px;
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
