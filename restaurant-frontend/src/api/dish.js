import request from "@/utils/request"

// 查询菜品列表
export function getDishList(params){

  return request.get("/dish",{
    params
  })

}

// 新增菜品
export function addDish(data){

  return request.post("/dish",data)

}

// 修改菜品
export function updateDish(data){

  return request.put("/dish",data)

}

// 删除菜品

export function getSimpleCategory(){

  return request.get("/category/simple")

}
export function deleteDish(id){
  return request.delete("/dish/" + id)
}
export function updateDishStatus(id,status){

  return request.put("/dish/status",{

    id,

    status

  })

}
export function batchDeleteDish(ids){

  return request.delete(
    "/dish/batch",
    {
      data:ids
    }
  )

}
export function getMenuDishes() {

  return request.get("/dish/menu")

}
