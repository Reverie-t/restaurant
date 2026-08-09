<template>

  <div class="table-container">

    <el-card>

      <template #header>

        <div class="header">

          <span>餐桌管理</span>

          <el-button
            type="primary"
            @click="openAdd"
          >
            新增餐桌
          </el-button>

        </div>

      </template>

      <!-- 查询 -->

      <el-row :gutter="20" style="margin-bottom:20px">

        <el-col :span="6">

          <el-input
            v-model="searchForm.tableNo"
            placeholder="桌号"
            clearable
          />

        </el-col>

        <el-col :span="6">

          <el-input
            v-model="searchForm.area"
            placeholder="区域"
            clearable
          />

        </el-col>

        <el-col :span="6">

          <el-select
            v-model="searchForm.status"
            placeholder="状态"
            clearable
          >

            <el-option label="空闲" :value="0"/>

            <el-option label="用餐中" :value="1"/>

            <el-option label="已预订" :value="2"/>

          </el-select>

        </el-col>

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

      <!-- 表格 -->

      <el-table
        :data="tableData"
        border
        stripe
        v-loading="loading"
      >

        <el-table-column
          prop="tableNo"
          label="桌号"
        />

        <el-table-column
          prop="capacity"
          label="人数"
        />

        <el-table-column
          prop="area"
          label="区域"
        />

        <el-table-column
          prop="remark"
          label="备注"
        />

        <el-table-column
          prop="sort"
          label="排序"
        />

        <el-table-column
          label="状态"
          width="160"
        >

          <template #default="scope">

            <div>

              <el-tag
                :type="tagType(scope.row.status)"
                style="margin-right:10px"
              >
                {{statusName(scope.row.status)}}
              </el-tag>


              <el-select
                v-model="scope.row.status"
                size="small"
                @change="changeStatus(scope.row)"
              >

                <el-option label="空闲" :value="0"/>
                <el-option label="用餐中" :value="1"/>
                <el-option label="已预订" :value="2"/>

              </el-select>

            </div>

          </template>

        </el-table-column>
        <el-table-column
          label="操作"
          width="220">

          <template #default="scope">

            <el-button
              link
              type="primary"
              @click="openEdit(scope.row)"
            >
              编辑
            </el-button>

            <el-button
              link
              type="danger"
              @click="remove(scope.row)"
            >
              删除
            </el-button>

          </template>

        </el-table-column>

      </el-table>

    </el-card>

  </div>
  <el-dialog
    v-model="dialogVisible"
    :title="isEdit?'编辑餐桌':'新增餐桌'"
    width="500px">

    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="90px">

      <el-form-item
        label="桌号"
        prop="tableNo">

        <el-input v-model="form.tableNo"/>

      </el-form-item>

      <el-form-item
        label="人数"
        prop="capacity">

        <el-input-number
          v-model="form.capacity"
          :min="1"
          style="width:100%"/>

      </el-form-item>

      <el-form-item
        label="区域">

        <el-input
          v-model="form.area"/>

      </el-form-item>

      <el-form-item
        label="状态">

        <el-radio-group
          v-model="form.status">

          <el-radio :value="0">
            空闲
          </el-radio>

          <el-radio :value="1">
            用餐中
          </el-radio>

          <el-radio :value="2">
            已预订
          </el-radio>

        </el-radio-group>

      </el-form-item>

      <el-form-item
        label="排序">

        <el-input-number
          v-model="form.sort"
          :min="0"
          style="width:100%"/>

      </el-form-item>

      <el-form-item
        label="备注">

        <el-input
          type="textarea"
          v-model="form.remark"/>

      </el-form-item>

    </el-form>

    <template #footer>

      <el-button
        @click="dialogVisible=false">

        取消

      </el-button>

      <el-button
        type="primary"
        @click="saveTable">

        保存

      </el-button>

    </template>

  </el-dialog>

</template>
<script setup>

import {ref,reactive,onMounted} from "vue"

import {getTableList} from "@/api/table"

import {
  addTable,
  updateTable,
  deleteTable,
  updateTableStatus
} from "@/api/table"

import { ElMessage, ElMessageBox } from "element-plus"

const dialogVisible = ref(false)

const isEdit = ref(false)

const formRef = ref()

const form = reactive({

  id:null,

  tableNo:"",

  capacity:2,

  status:0,

  area:"大厅",

  remark:"",

  sort:0

})

const rules={

  tableNo:[

    {
      required:true,
      message:"请输入桌号",
      trigger:"blur"
    }

  ],

  capacity:[

    {
      required:true,
      message:"请输入人数",
      trigger:"blur"
    }

  ]

}

const tableData = ref([])

const loading = ref(false)

const searchForm = reactive({

  tableNo:"",

  area:"",

  status:null

})

function openEdit(row){

  isEdit.value = true

  Object.assign(form, JSON.parse(JSON.stringify(row)))

  formRef.value?.clearValidate()

  dialogVisible.value = true

}
async function changeStatus(row){

  const res = await updateTableStatus(
    row.id,
    row.status
  )


  if(res.data.code===200){

    ElMessage.success("状态修改成功")

  }else{

    ElMessage.error(res.data.message)

    loadData()

  }

}
async function remove(row){

  try{

    await ElMessageBox.confirm(
      "确定删除该餐桌？",
      "提示",
      {
        type:"warning"
      }
    )

    const res = await deleteTable(row.id)

    if(res.data.code===200){

      ElMessage.success("删除成功")

      loadData()

    }else{

      ElMessage.error(res.data.message)

    }

  }catch{

  }

}

async function saveTable(){

  const valid = await formRef.value.validate().catch(()=>false)

  if(!valid){

    return

  }

  let res

  if(isEdit.value){

    res = await updateTable(form)

  }else{

    res = await addTable(form)

  }

  if(res.data.code===200){

    ElMessage.success(

      isEdit.value ? "修改成功" : "新增成功"

    )

    dialogVisible.value = false

    loadData()

  }else{

    ElMessage.error(res.data.message)

  }

}

async function loadData(){

  loading.value = true

  try{

    const res = await getTableList(searchForm)

    if(res.data.code===200){

      tableData.value = res.data.data

    }

  }finally{

    loading.value = false

  }

}

function resetSearch(){

  searchForm.tableNo=""

  searchForm.area=""

  searchForm.status=null

  loadData()

}

function statusName(status){

  if(status===0) return "空闲"

  if(status===1) return "用餐中"

  return "已预订"

}

function tagType(status){

  if(status===0) return "success"

  if(status===1) return "danger"

  return "warning"

}

function openAdd(){

  isEdit.value = false

  Object.assign(form,{

    id:null,

    tableNo:"",

    capacity:2,

    status:0,

    area:"大厅",

    remark:"",

    sort:0

  })

  formRef.value?.clearValidate()

  dialogVisible.value = true

}

onMounted(loadData)

</script>
<style scoped>
.table-container {
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
</style>
