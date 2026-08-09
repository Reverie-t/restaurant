import request from "@/utils/request"


// 查询订单列表
export function getOrderList(params){

  return request.get("/orders", {
    params
  })

}
export function getOrderDetail(id){

  return request.get("/orders/" + id)

}
export function updateOrderStatus(id,status){

  return request.put("/orders/status",{

    id,

    status

  })

}
export function addOrder(data) {

  return request.post("/orders/order", data)

}
