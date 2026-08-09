<template>

  <div class="category-container">


    <el-card>


      <template #header>

        <div class="header">

<span>
分类管理
</span>


          <el-button
            type="primary"
            @click="openAdd"
          >
            新增分类
          </el-button>


        </div>

      </template>



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
          prop="name"
          label="分类名称"
        />


        <el-table-column
          prop="sort"
          label="排序"
        />


        <el-table-column
          label="状态"
        >

          <template #default="scope">

            <el-tag
              :type="scope.row.status===1?'success':'danger'"
            >

              {{scope.row.status===1?'启用':'禁用'}}

            </el-tag>


          </template>

        </el-table-column>



        <el-table-column
          label="操作"
          align="center"
          width="220"
        >


          <template #default="scope">


            <el-button
              link
              type="primary"
              @click="openEdit(scope.row)"
            >
              编辑
            </el-button>



            <el-popconfirm
              title="确定删除吗？"
              @confirm="remove(scope.row.id)"
            >

              <template #reference>

                <el-button
                  link
                  type="danger"
                >
                  删除
                </el-button>

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

    </el-card>




    <!-- 新增/编辑弹窗 -->


    <el-dialog
      v-model="dialogVisible"
      :title="isEdit?'编辑分类':'新增分类'"
      width="500px"
    >


      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >


        <el-form-item
          label="名称"
          prop="name"
        >

          <el-input
            v-model="form.name"
          />

        </el-form-item>



        <el-form-item
          label="排序"
          prop="sort"
        >

          <el-input-number
            v-model="form.sort"
            :min="0"
          />

        </el-form-item>



        <el-form-item
          label="状态"
          prop="status"
        >


          <el-radio-group
            v-model="form.status"
          >

            <el-radio :value="1">
              启用
            </el-radio>


            <el-radio :value="0">
              禁用
            </el-radio>


          </el-radio-group>


        </el-form-item>


      </el-form>



      <template #footer>


        <el-button
          @click="dialogVisible=false"
        >
          取消
        </el-button>


        <el-button
          type="primary"
          @click="save"
        >
          保存
        </el-button>


      </template>



    </el-dialog>



  </div>


</template>
<script setup>

import {ref,onMounted,reactive} from "vue"

import {
  getCategoryList,
  addCategory,
  updateCategory,
  deleteCategory
} from "@/api/category"

import {ElMessage} from "element-plus"



const tableData=ref([])

const loading=ref(false)



const dialogVisible=ref(false)

const isEdit=ref(false)


const formRef=ref()

const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)



const form=reactive({

  id:null,

  name:"",

  sort:0,

  status:1

})



const rules={


  name:[

    {
      required:true,
      message:"请输入分类名称",
      trigger:"blur"
    }

  ],


  sort:[

    {
      required:true,
      message:"请输入排序",
      trigger:"blur"
    }

  ]

}





// 查询

async function loadData(){
  loading.value=true
  try{
    const res=await getCategoryList({
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    if(res.data.code===200){
      tableData.value=res.data.data.records
      total.value=res.data.data.total
    }
  }catch(e){
    ElMessage.error("加载失败")
  }finally{
    loading.value=false
  }
}




// 打开新增

function openAdd(){
  isEdit.value=false
  Object.assign(form,{
    id:null,
    name:"",
    sort:0,
    status:1
  })
  formRef.value?.clearValidate()
  dialogVisible.value=true
}



// 打开编辑

function openEdit(row){
  isEdit.value=true
  Object.assign(form, JSON.parse(JSON.stringify(row)))
  dialogVisible.value=true
}




// 保存

function save(){


  formRef.value.validate(async valid=>{


    if(!valid){

      return

    }



    let res



    if(isEdit.value){


      res=await updateCategory(form)


    }else{


      res=await addCategory(form)


    }




    if(res.data.code===200){


      ElMessage.success(
        isEdit.value?"修改成功":"新增成功"
      )


      dialogVisible.value=false


      loadData()


    }else{


      ElMessage.error(res.data.message)

    }



  })

}






// 删除

async function remove(id){


  const res=await deleteCategory(id)


  if(res.data.code===200){


    ElMessage.success("删除成功")

    loadData()


  }else{


    ElMessage.error(res.data.message)


  }


}




onMounted(loadData)


</script>
<style scoped>
.category-container {
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

.pagination {
  margin-top: var(--sp-5);
  display: flex;
  justify-content: flex-end;
}
</style>
