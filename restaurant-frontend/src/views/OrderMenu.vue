<template>

  <div class="order-menu">

    <!-- 顶部 -->
    <header class="order-header">

      <div
        class="logo"
        @click="router.push('/')"
      >
        MORI
      </div>

      <div class="header-right">

        <button
          class="back-button"
          @click="router.push('/menu')"
        >
          ← 返回菜单
        </button>

      </div>

    </header>


    <!-- 标题 -->
    <section class="menu-intro">

      <span class="eyebrow">
        TODAY'S MENU
      </span>

      <h1>
        选择你想吃的
      </h1>

      <p>
        慢慢挑选，我们会为你准备好每一道菜。
      </p>

    </section>


    <!-- 分类 -->
    <nav class="category-nav">

      <button
        :class="{ active: currentCategory === null }"
        @click="currentCategory = null"
      >
        全部
      </button>

      <button
        v-for="category in categories"
        :key="category"
        :class="{
          active: currentCategory === category
        }"
        @click="currentCategory = category"
      >
        {{ category }}
      </button>

    </nav>


    <!-- 菜品 -->
    <main class="dish-grid">

      <article
        v-for="dish in filteredDishes"
        :key="dish.id"
        class="dish-card"
      >

        <!-- 图片 -->
        <div class="dish-image">

          <img
            v-if="dish.image"
            :src="getDishImage(dish.image)"
            :alt="dish.name"
          />

          <div
            v-else
            class="dish-placeholder"
          >
            {{ dish.name }}
          </div>

        </div>


        <!-- 信息 -->
        <div class="dish-info">

          <div class="dish-meta">

            <span>
              {{ dish.categoryName || "MENU" }}
            </span>

          </div>


          <h2>
            {{ dish.name }}
          </h2>


          <div class="dish-bottom">

            <strong>
              ¥{{ Number(dish.price).toFixed(2) }}
            </strong>

            <button
              class="add-button"
              @click="addToCart(dish)"
            >
              <span>＋</span>
              加入点餐车
            </button>

          </div>

        </div>

      </article>

    </main>


    <!-- 底部购物车 -->
    <div
      class="cart-bar"
      :class="{
        'cart-bar-empty':
          cart.totalQuantity === 0
      }"
      @click="openCart"
    >

      <div class="cart-left">

        <div class="cart-icon">
          🛒
        </div>

        <div>

          <span class="cart-label">
            点餐车
          </span>

          <span
            v-if="cart.totalQuantity > 0"
            class="cart-count"
          >
            {{ cart.totalQuantity }} 份
          </span>

        </div>

      </div>


      <div class="cart-center">

        <span
          v-if="cart.totalQuantity > 0"
        >
          已选择 {{ cart.totalQuantity }} 份菜品
        </span>

        <span v-else>
          暂未选择菜品
        </span>

      </div>


      <div class="cart-right">

        <strong
          v-if="cart.totalQuantity > 0"
        >
          ¥{{ cart.totalAmount.toFixed(2) }}
        </strong>

        <span>
          查看 →
        </span>

      </div>

    </div>

  </div>

</template>


<script setup>

import {
  ref,
  computed,
  onMounted
} from "vue"

import {
  useRouter
} from "vue-router"

import {
  getMenuDishes
} from "@/api/dish"

import {
  useCartStore
} from "@/stores/cart"


const router = useRouter()

const cart = useCartStore()


const dishes = ref([])

const currentCategory = ref(null)


// 后端地址
const API_BASE_URL =
  "http://localhost:8080"


// 分类
const categories = computed(() => {

  const result = []

  dishes.value.forEach(dish => {

    if (
      dish.categoryName &&
      !result.includes(dish.categoryName)
    ) {

      result.push(dish.categoryName)

    }

  })

  return result

})


// 当前显示的菜品
const filteredDishes = computed(() => {

  if (!currentCategory.value) {

    return dishes.value

  }

  return dishes.value.filter(
    dish =>
      dish.categoryName ===
      currentCategory.value
  )

})


// 图片地址
function getDishImage(image) {

  if (!image) {
    return ""
  }

  if (image.startsWith("http")) {
    return image
  }

  return API_BASE_URL + image

}


// 加入点餐车
function addToCart(dish) {

  cart.addItem(
    dish,
    1
  )

}


// 打开点餐车
function openCart() {

  if (cart.totalQuantity === 0) {
    return
  }

  router.push("/menu/cart")

}


// 获取菜品
async function loadDishes() {

  try {

    const res =
      await getMenuDishes()

    console.log(
      "点餐菜单：",
      res.data
    )

    if (
      res.data.code === 200
    ) {

      dishes.value =
        res.data.data || []

    }

  } catch (error) {

    console.error(
      "获取菜单失败：",
      error
    )

  }

}


onMounted(
  loadDishes
)

</script>


<style scoped>

.order-menu {

  min-height: 100vh;

  background: #f5f1ea;

  color: #27231f;

  padding-bottom: 120px;

}


/* =========================
   顶部
========================= */

.order-header {

  height: 90px;

  padding:
    0 6vw;

  display: flex;

  justify-content: space-between;

  align-items: center;

  border-bottom:
    1px solid
    rgba(39,35,31,.12);

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

  color: #27231f;

}


/* =========================
   标题
========================= */

.menu-intro {

  max-width: 1200px;

  margin:
    90px auto 50px;

  padding:
    0 30px;

}


.eyebrow {

  font-size: 10px;

  letter-spacing: 4px;

  opacity: .5;

}


.menu-intro h1 {

  margin:
    16px 0 12px;

  font-family:
    "Cormorant Garamond",
    serif;

  font-size: 64px;

  font-weight: 400;

}


.menu-intro p {

  margin: 0;

  font-size: 14px;

  opacity: .6;

}


/* =========================
   分类
========================= */

.category-nav {

  max-width: 1200px;

  margin:
    0 auto 50px;

  padding:
    0 30px;

  display: flex;

  gap: 30px;

  overflow-x: auto;

}


.category-nav button {

  flex-shrink: 0;

  border: 0;

  border-bottom:
    1px solid transparent;

  background: transparent;

  padding:
    8px 0;

  cursor: pointer;

  font-size: 12px;

  letter-spacing: 2px;

  color: #27231f;

  opacity: .5;

}


.category-nav button.active {

  opacity: 1;

  border-bottom-color:
    #27231f;

}


/* =========================
   菜品
========================= */

.dish-grid {

  max-width: 1200px;

  margin: 0 auto;

  padding:
    0 30px;

  display: grid;

  grid-template-columns:
    repeat(3, 1fr);

  gap:
    60px 30px;

}


.dish-card {

  min-width: 0;

}


.dish-image {

  height: 380px;

  overflow: hidden;

  background: #5c5147;

}


.dish-image img {

  width: 100%;

  height: 100%;

  object-fit: cover;

  display: block;

  transition:
    transform .6s ease;

}


.dish-card:hover
.dish-image img {

  transform:
    scale(1.04);

}


.dish-placeholder {

  width: 100%;

  height: 100%;

  display: flex;

  align-items: center;

  justify-content: center;

  text-align: center;

  padding: 20px;

  box-sizing: border-box;

  font-family:
    "Cormorant Garamond",
    serif;

  font-size: 24px;

  background:
    linear-gradient(
      135deg,
      #806f5d,
      #39332d
    );

  color:
    rgba(255,255,255,.7);

}


.dish-info {

  padding-top: 20px;

}


.dish-meta {

  font-size: 10px;

  letter-spacing: 3px;

  opacity: .5;

}


.dish-info h2 {

  margin:
    10px 0 18px;

  font-family:
    "Cormorant Garamond",
    serif;

  font-size: 28px;

  font-weight: 400;

}


.dish-bottom {

  display: flex;

  justify-content: space-between;

  align-items: center;

  gap: 15px;

}


.dish-bottom strong {

  font-family:
    "Cormorant Garamond",
    serif;

  font-size: 24px;

  font-weight: 400;

}


.add-button {

  border: 0;

  border-bottom:
    1px solid
    #27231f;

  background: transparent;

  padding:
    6px 0;

  cursor: pointer;

  font-size: 11px;

  letter-spacing: 1px;

  color: #27231f;

  transition:
    opacity .2s;

}


.add-button:hover {

  opacity: .5;

}


.add-button span {

  font-size: 16px;

  margin-right: 4px;

}


/* =========================
   底部点餐车
========================= */

.cart-bar {

  position: fixed;

  z-index: 100;

  left: 0;

  right: 0;

  bottom: 0;

  height: 76px;

  padding:
    0 6vw;

  box-sizing: border-box;

  display: grid;

  grid-template-columns:
    1fr 1fr 1fr;

  align-items: center;

  background: #27231f;

  color: #f5f1ea;

  cursor: pointer;

  box-shadow:
    0 -10px 30px
    rgba(0,0,0,.08);

}


.cart-bar-empty {

  opacity: .7;

}


.cart-left {

  display: flex;

  align-items: center;

  gap: 14px;

}


.cart-icon {

  font-size: 18px;

}


.cart-label {

  font-size: 11px;

  letter-spacing: 3px;

}


.cart-count {

  margin-left: 12px;

  font-size: 11px;

  opacity: .6;

}


.cart-center {

  text-align: center;

  font-size: 11px;

  letter-spacing: 1px;

  opacity: .6;

}


.cart-right {

  display: flex;

  justify-content: flex-end;

  align-items: center;

  gap: 25px;

}


.cart-right strong {

  font-family:
    "Cormorant Garamond",
    serif;

  font-size: 22px;

  font-weight: 400;

}


.cart-right span {

  font-size: 11px;

  letter-spacing: 2px;

}


/* =========================
   手机端
========================= */

@media (max-width: 800px) {

  .menu-intro {

    margin-top: 60px;

  }


  .menu-intro h1 {

    font-size: 48px;

  }


  .dish-grid {

    grid-template-columns:
      1fr;

    gap: 45px;

  }


  .dish-image {

    height: 420px;

  }


  .cart-bar {

    grid-template-columns:
      1fr auto;

    height: 70px;

  }


  .cart-center {

    display: none;

  }


  .cart-right {

    gap: 12px;

  }

}

</style>
