<template>
  <div class="page">

    <!-- Page Header -->
    <div class="page-header">
      <div>
        <div class="eyebrow">USER</div>
        <h1 class="page-title">用户管理</h1>
        <p class="page-desc">管理系统用户、角色与权限</p>
      </div>
      <el-button type="primary" @click="openAdd">新增用户</el-button>
    </div>

    <!-- Toolbar -->
    <div class="toolbar">
      <el-input
        v-model="searchForm.username"
        placeholder="输入用户名搜索"
        clearable
        style="width: 240px;"
        @keyup.enter="search"
      />
      <el-button type="primary" @click="search">查询</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>

    <!-- Content -->
    <div class="card">
      <el-table :data="tableData" v-loading="loading">

        <el-table-column prop="id" label="ID" width="70" />

        <el-table-column prop="username" label="用户名" />

        <el-table-column prop="realName" label="姓名" />

        <el-table-column prop="phone" label="手机号" />

        <el-table-column prop="email" label="邮箱" />

        <el-table-column prop="role" label="角色" width="100">
          <template #default="scope">
            <span class="role-tag" :class="scope.row.role === 'admin' ? 'role-admin' : 'role-staff'">
              {{ scope.row.role === 'admin' ? '管理员' : '顾客' }}
            </span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="140" align="center">
          <template #default="scope">
            <button class="action-btn action-edit" @click="openEdit(scope.row)">编辑</button>
            <button class="action-btn action-del" @click="handleDelete(scope.row)">删除</button>
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
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑用户' : '新增用户'"
      width="480px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="70px">

        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" />
        </el-form-item>

        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input type="password" v-model="form.password" />
        </el-form-item>

        <el-form-item label="姓名" prop="realName">
          <el-input v-model="form.realName" />
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" />
        </el-form-item>

        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" style="width: 100%;">
            <el-option label="管理员" value="admin" />
            <el-option label="顾客" value="user" />
          </el-select>
        </el-form-item>

      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveUser">{{ isEdit ? '修改' : '保存' }}</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, addUser, updateUser, deleteUser } from '@/api/user'

const dialogVisible = ref(false)
const formRef = ref()
const isEdit = ref(false)
const loading = ref(false)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({ username: '' })

const form = reactive({
  id: null,
  username: '',
  password: '',
  realName: '',
  phone: '',
  email: '',
  role: 'staff'
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度6~20位', trigger: 'blur' }
  ],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }]
}

function search() {
  pageNum.value = 1
  loadData()
}

function resetSearch() {
  searchForm.username = ''
  pageNum.value = 1
  loadData()
}

const handleCurrentChange = (page) => { pageNum.value = page; loadData() }
const handleSizeChange = (size) => { pageSize.value = size; pageNum.value = 1; loadData() }

function openAdd() {
  isEdit.value = false
  Object.assign(form, { id: null, username: '', password: '', realName: '', phone: '', email: '', role: 'staff' })
  formRef.value?.clearValidate()
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  Object.assign(form, JSON.parse(JSON.stringify(row)))
  dialogVisible.value = true
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定删除用户【${row.username}】吗？`, '提示', { type: 'warning' })
    const res = await deleteUser(row.id)
    if (res.data.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    } else {
      ElMessage.error(res.data.message || '删除失败')
    }
  } catch (e) {
    if (e === 'cancel' || e === 'close') return
    ElMessage.error('删除请求异常')
  }
}

async function saveUser() {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      const res = isEdit.value ? await updateUser(form) : await addUser(form)
      if (res.data.code === 200) {
        ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
        dialogVisible.value = false
        formRef.value.resetFields()
        loadData()
      } else {
        ElMessage.error(res.data.message)
      }
    } catch (e) {
      ElMessage.error('服务器异常')
    }
  })
}

async function loadData() {
  loading.value = true
  try {
    const res = await getUserList({ pageNum: pageNum.value, pageSize: pageSize.value, username: searchForm.username })
    if (res.data.code === 200) {
      tableData.value = res.data.data.records
      total.value = res.data.data.total
    }
  } catch (e) {
    ElMessage.error('服务器连接失败')
  } finally {
    loading.value = false
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

.pagination {
  padding: 16px 20px;
  display: flex;
  justify-content: flex-end;
}

.role-tag {
  font-size: 12px;
  padding: 2px 10px;
}

.role-admin {
  color: var(--text-1);
  background: #eee9e3;
}

.role-staff {
  color: var(--text-3);
  background: #f5f3f0;
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
