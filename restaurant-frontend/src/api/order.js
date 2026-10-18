import request from "@/utils/request"


// 查询订单列表
export function getOrderList(params){

  return request.get("/orders", {
    params
  })

}
export function getOrderDetail(id){

  return request.get("/orders/"+id ,id )

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
// 顾客查询自己的订单
export function getMyOrderList() {

  return request.get("/orders/my")

}
// 模拟支付
export function payOrder(id) {

  return request.post("/orders/pay/" + id)

}

// 支付宝沙箱支付：创建收银台表单（后端返回 HTML 片段）
export function createAlipayForm(orderId) {

  return request.post("/pay/alipay/create/" + orderId)

}

// 支付宝沙箱支付：主动查询订单支付状态
export function queryAlipayPay(orderId) {

  return request.get("/pay/alipay/query/" + orderId)

}
