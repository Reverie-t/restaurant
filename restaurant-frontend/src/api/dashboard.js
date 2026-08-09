import request from "@/utils/request"

export function getDashboardStatistics() {

  return request.get("/dashboard/statistics")

}
export function getHotDishes() {

  return request.get("/dashboard/hot-dishes")

}
export function getOrderTrend() {

  return request.get("/dashboard/order-trend")

}
