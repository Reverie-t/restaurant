<template>
  <div class="user-container">

    <el-card>

      <template #header>
        <span>用户管理</span>
      </template>
      <el-row :gutter="20" style="margin-bottom:20px">

        <el-col :span="6">
          <el-input
            v-model="searchForm.username"
            placeholder="请输入用户名"
            clearable
          />
        </el-col>

        <el-col :span="8">
          <el-button
            type="primary"
            @click="search"
          >
            查询
          </el-button>

          <el-button
            @click="resetSearch"
          >
            重置
          </el-button>
        </el-col>

        <el-col
          :span="10"
          style="text-align:right"
        >
          <el-button
            type="primary" @click="openAdd"
          >
            新增用户
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
          prop="id"
          label="ID"
          width="80"
        />

        <el-table-column
          prop="username"
          label="用户名"
        />

        <el-table-column
          prop="realName"
          label="姓名"
        />

        <el-table-column
          prop="phone"
          label="手机号"
        />

        <el-table-column
          prop="email"
          label="邮箱"
        />

        <el-table-column
          prop="role"
          label="角色"
        />

        <el-table-column
          label="操作"
          width="220"
        >

          <template #default="scope">

            <el-button
              type="primary"
              size="small"
              @click="openEdit(scope.row)"
            >
              编辑
            </el-button>

            <el-button
              type="danger"
              link
              @click="handleDelete(scope.row)"
            >
              删除
            </el-button>

          </template>

        </el-table-column>

      </el-table>

      <div style="margin-top:20px;text-align:right">

        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          :page-size="pageSize"
          :current-page="pageNum"
          :page-sizes="[5,10,20,50]"
          @current-change="handleCurrentChange"
          @size-change="handleSizeChange"
        />

      </div>

    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑用户' : '新增用户'"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >

        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username"/>
        </el-form-item>

        <el-form-item label="密码" prop="password"  v-if="!isEdit">
          <el-input
            type="password"
            v-model="form.password"
          />
        </el-form-item>

        <el-form-item label="姓名" prop="realName">
          <el-input v-model="form.realName"/>
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone"/>
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email"/>
        </el-form-item>

        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role">
            <el-option label="管理员" value="admin"/>
            <el-option label="员工" value="staff"/>
          </el-select>
        </el-form-item>

      </el-form>

      <template #footer>

        <el-button @click="dialogVisible=false">
          取消
        </el-button>

        <el-button
          type="primary"
          @click="saveUser"
        >
          {{ isEdit ? "修改" : "保存" }}
        </el-button>

      </template>

    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage,ElMessageBox } from 'element-plus'
import { getUserList } from '@/api/user'
import { reactive } from 'vue'
import { addUser, updateUser,deleteUser } from '@/api/user'


const dialogVisible = ref(false)

const formRef = ref()

const isEdit = ref(false)

const form = reactive({
  id: null,

  username: '',

  password: '',

  realName: '',

  phone: null,

  email: '',

  role: 'staff'

})

const tableData = ref([])

const loading = ref(false)

const searchForm = ref({
  username: ''
})

const pageNum = ref(1)

const pageSize = ref(10)

const total = ref(0)

const rules = {

  username: [

    {
      required: true,
      message: '请输入用户名',
      trigger: 'blur'
    }

  ],

  password: [

    {
      required: true,
      message: '请输入密码',
      trigger: 'blur'
    },

    {
      min: 6,
      max: 20,
      message: '密码长度6~20位',
      trigger: 'blur'
    }

  ],

  realName: [

    {
      required: true,
      message: '请输入姓名',
      trigger: 'blur'
    }

  ]

}

const search = () => {

  pageNum.value = 1

  loadData()

}
// 建议加上 loading 状态，防止重复提交
const deleteLoading = ref(false)

async function handleDelete(row) {
  try {
    // 1. 确认框
    await ElMessageBox.confirm(
      `确定删除用户【${row.username}】吗？`,
      "提示",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }
    )

    // 2. 执行删除（加锁）
    deleteLoading.value = true
    const res = await deleteUser(row.id)

    // 3. 处理业务返回码
    if (res.data.code === 200) {
      ElMessage.success("删除成功")
      loadData() // 刷新列表
    } else {
      // 后端业务异常（比如 code !== 200，但请求状态码还是200）
      ElMessage.error(res.data.message || "删除失败，请重试")
    }
  } catch (e) {
    // 4. 【关键区分】只有用户点击取消或关闭时，才静默处理
    if (e === 'cancel' || e === 'close') {
      // 点击取消，什么都不做，直接 return
      return
    }

    // 5. 其他异常：网络超时、500 错误、或请求被拦截器 reject
    // 这里必须提示用户，否则会有“静默失败”的问题
    ElMessage.error("删除请求异常，请检查网络或联系管理员")
    console.error("Delete Error:", e) // 打印错误用于排查
  } finally {
    // 无论成功失败，解锁按钮
    deleteLoading.value = false
  }
}

function openAdd(){

  isEdit.value = false

  Object.assign(form,{
    id:null,
    username:'',
    password:'',
    realName:'',
    phone:'',
    email:'',
    role:'staff'
  })

  dialogVisible.value=true
}

function openEdit(row){
  isEdit.value=true
  Object.assign(form, JSON.parse(JSON.stringify(row)))
  dialogVisible.value=true
}

const handleCurrentChange = (page) => {

  pageNum.value = page

  loadData()

}

const handleSizeChange = (size) => {

  pageSize.value = size

  pageNum.value = 1

  loadData()

}

const resetSearch = () => {

  searchForm.value.username = ''

  loadData()

}

const loadData = async () => {

  loading.value = true

  try {

    const res = await getUserList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      username: searchForm.value.username
    })

    if (res.data.code === 200) {

      tableData.value = res.data.data.records

      total.value = res.data.data.total

    } else {

      ElMessage.error(res.data.message)

    }

  } catch (e) {

    ElMessage.error('服务器连接失败')

    console.error(e)

  } finally {

    loading.value = false

  }

}

onMounted(() => {

  loadData()

})

async function saveUser() {

  formRef.value.validate(async (valid) => {

    if (!valid) return

    try {

      let res

      if (isEdit.value) {

        res = await updateUser(form)

      } else {

        res = await addUser(form)

      }

      if (res.data.code === 200) {

        ElMessage.success(
          isEdit.value ? "修改成功" : "新增成功"
        )

        dialogVisible.value = false

        formRef.value.resetFields()

        loadData()

      } else {

        ElMessage.error(res.data.message)

      }

    } catch (e) {

      console.error(e)

      ElMessage.error("服务器异常")

    }

  })

}
</script>

<style scoped>
.user-container {
  padding: 0;
}

.el-row {
  background: var(--surface-raised);
  padding: var(--sp-4);
  border-radius: var(--radius-md);
  margin-bottom: var(--sp-5) !important;
  border: 1px solid var(--border-light);
}
</style>
