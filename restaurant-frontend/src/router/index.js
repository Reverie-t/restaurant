import { createRouter, createWebHistory } from 'vue-router'

import Login from '@/views/Login.vue'
import Layout from '@/views/layout/Layout.vue'
import Dashboard from '@/views/Dashboard.vue'
import User from '@/views/user/User.vue'
import Category from "@/views/category/Category.vue";
import Dish from "@/views/dish/Dish.vue";
import Order from "@/views/order/Order.vue";
import Table from "@/views/table/Table.vue"

const router = createRouter({

  history: createWebHistory(),

  routes: [

    {
      path: '/login',
      component: Login
    },

    {
      path: '/',
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
          path: "category",
          component: Category
        },{
          path:"dish",
          component:Dish
        },{
          path:"order",
          component:Order
        },{
          path:"table",
          component:Table
        }

      ]
    }

  ]

})

export default router

router.beforeEach((to, from, next) => {

  // 登录页直接放行
  if (to.path === "/login") {
    next()
    return
  }

  // 检查登录状态
  const user = localStorage.getItem("user")

  if (!user) {
    next("/login")
    return
  }

  next()
})
