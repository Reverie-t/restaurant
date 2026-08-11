<template>

  <div class="menu-page">

    <!-- 顶部 -->

    <header class="menu-header">

      <div
        class="logo"
        @click="router.push('/')"
      >
        MORI
      </div>

      <div class="menu-title">

        <span>OUR MENU</span>

        <h1>
          今日菜单
        </h1>

      </div>

      <button
        class="back-button"
        @click="router.push('/')"
      >
        ← HOME
      </button>

    </header>


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
        :key="category.id"
        :class="{
          active: currentCategory === category.id
        }"
        @click="currentCategory = category.id"
      >
        {{ category.name }}
      </button>

    </nav>


    <!-- 菜品 -->

    <main class="menu-grid">

      <article
        v-for="dish in filteredDishes"
        :key="dish.id"
        class="menu-dish"
        @click="goDetail(dish.id)"
      >

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
            {{ dish.categoryName }}
          </div>

        </div>


        <div class="dish-content">

          <div class="dish-meta">

            <span>
              {{ dish.categoryName }}
            </span>

            <span>
              ¥{{ dish.price }}
            </span>

          </div>

          <h2>
            {{ dish.name }}
          </h2>

          <p v-if="dish.description">
            {{ dish.description }}
          </p>

        </div>

      </article>

    </main>


    <!-- 空状态 -->

    <div
      v-if="filteredDishes.length === 0"
      class="empty"
    >
      暂无该分类菜品
    </div>

  </div>

</template>


<script setup>

import {
  ref,
  computed,
  onMounted
} from "vue"

import { useRouter } from "vue-router"

import { getMenuDishes} from "@/api/dish"


const router = useRouter()


const API_BASE_URL = "http://localhost:8080"


const dishes = ref([])

const currentCategory = ref(null)


// 分类
const categories = computed(() => {

  const map = new Map()

  dishes.value.forEach(dish => {

    if (!map.has(dish.categoryId)) {

      map.set(
        dish.categoryId,
        {
          id: dish.categoryId,
          name: dish.categoryName
        }
      )

    }

  })

  return Array.from(map.values())

})


// 当前分类菜品
const filteredDishes = computed(() => {

  if (currentCategory.value === null) {

    return dishes.value

  }

  return dishes.value.filter(
    dish =>
      dish.categoryId === currentCategory.value
  )

})
function goDetail(id) {

  router.push(`/menu/${id}`)

}

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


// 获取菜品
async function loadDishes() {

  try {

    const res = await getMenuDishes()

    console.log("前台菜单：", res.data)

    if (res.data.code === 200) {

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


onMounted(() => {

  loadDishes()

})

</script>
<style scoped>
.menu-page {
  min-height: 100vh;
  background: #f5f1ea;
  color: #27231f;
  padding: 40px 6vw 100px;
}


.menu-header {
  display: grid;
  grid-template-columns: 1fr 2fr 1fr;
  align-items: center;
  padding-bottom: 50px;
  border-bottom: 1px solid rgba(39, 35, 31, .2);
}


.logo {
  font-family: "Cormorant Garamond", serif;
  font-size: 28px;
  letter-spacing: 5px;
  cursor: pointer;
}


.menu-title {
  text-align: center;
}


.menu-title span {
  font-size: 11px;
  letter-spacing: 4px;
  opacity: .55;
}


.menu-title h1 {
  margin: 8px 0 0;
  font-family: "Cormorant Garamond", serif;
  font-size: clamp(42px, 5vw, 72px);
  font-weight: 400;
}


.back-button {
  justify-self: end;
  border: 0;
  background: transparent;
  cursor: pointer;
  letter-spacing: 2px;
  font-size: 11px;
}


.category-nav {
  display: flex;
  justify-content: center;
  gap: 12px;
  padding: 35px 0 60px;
  flex-wrap: wrap;
}


.category-nav button {
  border: 1px solid rgba(39, 35, 31, .25);
  background: transparent;
  padding: 10px 22px;
  cursor: pointer;
  font-size: 13px;
  transition: .25s;
}


.category-nav button:hover,
.category-nav button.active {
  background: #27231f;
  color: #f5f1ea;
}


.menu-grid {
  max-width: 1400px;
  margin: auto;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 70px 35px;
}


.menu-dish {
  cursor: pointer;
}


.dish-image {
  height: 460px;
  overflow: hidden;
  background: #5c5147;
}


.dish-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  transition: transform .7s ease;
}


.menu-dish:hover .dish-image img {
  transform: scale(1.035);
}


.dish-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  color: rgba(255,255,255,.7);
  font-family: "Cormorant Garamond", serif;
  font-size: 28px;
}


.dish-content {
  padding-top: 18px;
}


.dish-meta {
  display: flex;
  justify-content: space-between;
  font-size: 11px;
  letter-spacing: 2px;
  opacity: .55;
}


.dish-content h2 {
  margin: 8px 0 0;
  font-family: "Cormorant Garamond", serif;
  font-size: 30px;
  font-weight: 400;
}


.dish-content p {
  max-width: 600px;
  margin-top: 10px;
  font-size: 13px;
  line-height: 1.8;
  opacity: .65;
}


.empty {
  text-align: center;
  padding: 100px 0;
  opacity: .5;
}


@media (max-width: 700px) {

  .menu-header {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .menu-title {
    text-align: left;
  }

  .back-button {
    justify-self: start;
  }

  .menu-grid {
    grid-template-columns: 1fr;
    gap: 50px;
  }

  .dish-image {
    height: 380px;
  }

}
</style>
