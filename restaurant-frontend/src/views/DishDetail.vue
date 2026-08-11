<template>

  <div class="detail-page">

    <!-- 顶部 -->

    <header class="detail-header">

      <div
        class="logo"
        @click="router.push('/')"
      >
        MORI
      </div>

      <button
        class="back-button"
        @click="router.push('/menu')"
      >
        ← MENU
      </button>

    </header>


    <!-- 内容 -->

    <main
      v-if="dish"
      class="detail-content"
    >

      <!-- 图片 -->

      <div class="detail-image">

        <img
          v-if="dish.image"
          :src="getDishImage(dish.image)"
          :alt="dish.name"
        />

        <div
          v-else
          class="dish-placeholder"
        >
          {{ dish.categoryName }}
        </div>

      </div>


      <!-- 信息 -->

      <div class="detail-info">

        <span class="detail-category">
          {{ dish.categoryName }}
        </span>

        <h1>
          {{ dish.name }}
        </h1>

        <div class="detail-price">
          ¥{{ dish.price }}
        </div>

        <div
          v-if="dish.description"
          class="detail-description"
        >
          {{ dish.description }}
        </div>


        <div class="detail-divider"></div>


        <button
          class="order-button"
          @click="goOrder"
        >
          立即点餐
          <span>↗</span>
        </button>

      </div>

    </main>


    <!-- 加载中 -->

    <div
      v-else
      class="loading"
    >
      Loading...
    </div>

  </div>

</template>


<script setup>

import {
  ref,
  onMounted
} from "vue"

import {
  useRouter,
  useRoute
} from "vue-router"
import { useCartStore } from "@/stores/cart"

import { getMenuDishes } from "@/api/dish"


const router = useRouter()

const cartStore = useCartStore()

const route = useRoute()


const API_BASE_URL = "http://localhost:8080"


const dish = ref(null)

function goOrder() {

  router.push("/menu/order")

}


function getDishImage(image) {

  if (!image) {
    return ""
  }

  if (image.startsWith("http")) {
    return image
  }

  return API_BASE_URL + image

}


async function loadDish() {

  try {

    const res = await getMenuDishes()

    console.log("详情页获取菜品：", res.data)

    if (res.data.code !== 200) {

      console.error("获取菜品失败")

      return

    }

    const list = res.data.data || []

    console.log("菜品列表：", list)

    console.log("当前ID：", route.params.id)

    dish.value = list.find(
      item =>
        String(item.id) ===
        String(route.params.id)
    )

    console.log("找到的菜品：", dish.value)

  } catch (error) {

    console.error(
      "获取菜品详情失败：",
      error
    )

  }

}





onMounted(() => {

  loadDish()

})

</script>
<style  scoped>
.detail-page {

  min-height: 100vh;

  background: #f5f1ea;

  color: #27231f;

  padding: 35px 6vw 100px;

}


.detail-header {

  display: flex;

  justify-content: space-between;

  align-items: center;

  padding-bottom: 30px;

  border-bottom: 1px solid
  rgba(39, 35, 31, .15);

}


.logo {

  font-family:
    "Cormorant Garamond",
    serif;

  font-size: 28px;

  letter-spacing: 5px;

  cursor: pointer;

}


.back-button {

  border: 0;

  background: transparent;

  cursor: pointer;

  font-size: 11px;

  letter-spacing: 2px;

}


.detail-content {

  max-width: 1400px;

  margin: 80px auto 0;

  display: grid;

  grid-template-columns:
    1.15fr .85fr;

  gap: 8vw;

  align-items: center;

}


.detail-image {

  height: 700px;

  overflow: hidden;

  background: #5c5147;

}


.detail-image img {

  width: 100%;

  height: 100%;

  object-fit: cover;

  display: block;

}


.dish-placeholder {

  width: 100%;

  height: 100%;

  display: flex;

  justify-content: center;

  align-items: center;

  color: rgba(255,255,255,.7);

  font-family:
    "Cormorant Garamond",
    serif;

  font-size: 32px;

}


.detail-info {

  padding-right: 5vw;

}


.detail-category {

  font-size: 11px;

  letter-spacing: 4px;

  opacity: .5;

}


.detail-info h1 {

  margin: 18px 0 25px;

  font-family:
    "Cormorant Garamond",
    serif;

  font-size:
    clamp(48px, 5vw, 76px);

  line-height: 1;

  font-weight: 400;

}


.detail-price {

  font-family:
    "Cormorant Garamond",
    serif;

  font-size: 30px;

  color: #9b7750;

}


.detail-description {

  margin-top: 35px;

  max-width: 480px;

  font-size: 14px;

  line-height: 2;

  opacity: .65;

}


.detail-divider {

  height: 1px;

  background: rgba(39, 35, 31, .15);

  margin: 45px 0 30px;

}


.order-button {

  display: flex;

  justify-content: space-between;

  align-items: center;

  width: 100%;

  max-width: 420px;

  padding: 18px 0;

  border: 0;

  border-bottom: 1px solid #27231f;

  background: transparent;

  cursor: pointer;

  font-size: 12px;

  letter-spacing: 3px;

  text-align: left;

}


.order-button span {

  font-size: 20px;

  transition: transform .3s;

}


.order-button:hover span {

  transform: translate(5px, -5px);

}


.loading {

  text-align: center;

  padding: 150px 0;

  opacity: .5;

}


@media (max-width: 800px) {

  .detail-content {

    grid-template-columns: 1fr;

    margin-top: 50px;

  }


  .detail-image {

    height: 500px;

  }


  .detail-info {

    padding-right: 0;

  }

}
</style>
