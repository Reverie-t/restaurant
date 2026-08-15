import { createRouter, createWebHistory } from 'vue-router'

import Login from '@/views/Login.vue'
import Home from '@/views/Home.vue'

import Layout from '@/views/layout/Layout.vue'
import Dashboard from '@/views/Dashboard.vue'
import User from '@/views/user/User.vue'
import Category from '@/views/category/Category.vue'
import Dish from '@/views/dish/Dish.vue'
import Order from '@/views/order/Order.vue'
import Table from '@/views/table/Table.vue'
import Menu from '@/views/Menu.vue'
import DishDetail from '@/views/DishDetail.vue'
import Cart from "@/views/Cart.vue"
import OrderMenu from "@/views/OrderMenu.vue"
import OrderConfirm from "@/views/OrderConfirm.vue"
import CustomerLogin
  from "@/views/customer/CustomerLogin.vue"
import CustomerOrders
  from "@/views/customer/CustomerOrders.vue"
import Takeaway from "@/views/takeaway/Takeaway.vue"
import Ingredient
  from "@/views/ingredient/Ingredient.vue"
import Expense from "@/views/expense/Expense.vue"

const router = createRouter({

  history: createWebHistory(),

  routes: [

    // =========================
    // 官网首页
    // =========================
    {
      path: '/',
      name: 'Home',
      component: Home
    },


    // =========================
    // 登录
    // =========================
    {
      path: '/login',
      name: 'Login',
      component: Login
    },{
      path: '/menu',
      name: 'Menu',
      component: Menu
    },{
      path: '/menu/:id',
      name: 'DishDetail',
      component: DishDetail
    },{
      path: "/menu/cart",
      name: "Cart",
      component: Cart
    },{
      path: "/menu/order",
      name: "OrderMenu",
      component: OrderMenu
    },{
      path: "/menu/confirm",
      name: "OrderConfirm",
      component: OrderConfirm
    },{
      path: "/customer/login",
      name: "CustomerLogin",
      component: CustomerLogin
    },{
      path: "/customer/orders",
      name: "CustomerOrders",
      component: CustomerOrders
    },


    // =========================
    // 管理后台
    // =========================
    {
      path: '/admin',
      component: Layout,

      children: [

        {
          path: 'dashboard',
          component: Dashboard
        },

        {
          path: 'user',
          component: User
        },

        {
          path: 'category',
          component: Category
        },

        {
          path: 'dish',
          component: Dish
        },

        {
          path: 'order',
          component: Order
        },

        {
          path: 'table',
          component: Table
        },{
          path: "takeaway",
          component: Takeaway
        },{
          path: "ingredient",
          component: Ingredient
        },{
          path: "expense",
          component: Expense
        }

      ]

    }

  ]

})


router.beforeEach((to, from, next) => {

  // =========================================
  // ① 后台登录页
  // =========================================

  if (to.path === "/login") {
    next()
    return
  }


  // =========================================
  // ② 顾客登录页
  // =========================================

  if (to.path === "/customer/login") {
    next()
    return
  }


  // =========================================
  // ③ 后台管理系统
  // /admin/**
  // =========================================

  if (to.path.startsWith("/admin")) {

    const user =
      localStorage.getItem("user")

    if (!user) {

      next({
        path: "/login",
        query: {
          redirect: to.fullPath
        }
      })

      return
    }

    next()
    return
  }


  // =========================================
  // ④ 顾客页面
  // =========================================

  if (
    to.path === "/" ||
    to.path.startsWith("/menu") ||
    to.path.startsWith("/customer")
  ) {

    next()
    return
  }


  // =========================================
  // ⑤ 其他页面
  // =========================================

  next()

})

export default router
