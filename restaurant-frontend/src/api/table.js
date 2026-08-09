import request from "@/utils/request"

// 查询
export function getTableList(params){

  return request.get("/table",{
    params
  })

}

// 新增
export function addTable(data){

  return request.post("/table",data)

}

// 修改
export function updateTable(data){

  return request.put("/table",data)

}

// 删除
export function deleteTable(id){

  return request.delete("/table/" + id)

}

// 修改状态
export function updateTableStatus(id,status){

  return request.put("/table/status",{

    id,

    status

  })

}
