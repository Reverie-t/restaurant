import request from "@/utils/request";

export function getCategoryList(params) {
  return request({
    url: "/categories",
    method: "get",
    params
  });
}
export function deleteCategory(id){
  return request.delete(`/category/${id}`)
}
// 新增分类
export function addCategory(data){

  return request.post("/category",data)

}


// 修改分类
export function updateCategory(data){

  return request.put("/category",data)

}
