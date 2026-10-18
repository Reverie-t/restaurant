import request from "@/utils/request"


// =========================
// 顾客登录
// =========================

export function customerLogin(data) {

  return request.post(
    "/customer/login",
    data
  )

}


// =========================
// 顾客注册
// =========================

export function customerRegister(data) {

  return request.post(
    "/customer/register",
    data
  )

}
