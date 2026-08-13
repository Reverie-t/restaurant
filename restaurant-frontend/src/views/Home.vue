<template>

  <div class="home">

    <!-- ==================== 顶部导航 ==================== -->

    <header class="header">

      <div
        class="logo"
        @click="scrollTo('home')"
      >

        <span class="logo-main">
          MORI
        </span>

        <span class="logo-sub">
          RESTAURANT
        </span>

      </div>


      <div class="header-right">

        <!-- 菜单 -->

        <button
          class="nav-button"
          @click="menuOpen = true"
        >
          菜单
        </button>


        <!-- 顾客登录 -->

        <button
          v-if="!customer"
          class="login-button"
          @click="goLogin1"
        >
          登录
        </button>


        <!-- 已登录 -->

        <button
          v-else
          class="customer-button"
          @click="goOrders"
        >
          {{  "订单管理" }}
        </button>


        <!-- 管理后台登录 -->

        <button
          class="admin-button"
          @click="goLogin1"
        >
          登录
        </button>

      </div>

    </header>


    <!-- ==================== 全屏导航 ==================== -->

    <Transition name="menu">

      <div
        v-if="menuOpen"
        class="menu-overlay"
      >

        <div class="menu-top">

          <div class="logo">

            <span class="logo-main">
              MORI
            </span>

            <span class="logo-sub">
              RESTAURANT
            </span>

          </div>


          <button
            class="close-button"
            @click="menuOpen = false"
          >

            <span>
              ×
            </span>

            CLOSE

          </button>

        </div>


        <nav class="navigation">

          <!-- 01 首页 -->

          <button
            @click="navigate('home')"
          >

            <span class="number">
              01
            </span>

            <span class="nav-text">

              <strong>
                HOME
              </strong>

              <small>
                首页
              </small>

            </span>

          </button>


          <!-- 02 关于我们 -->

          <button
            @click="navigate('about')"
          >

            <span class="number">
              02
            </span>

            <span class="nav-text">

              <strong>
                ABOUT US
              </strong>

              <small>
                关于我们
              </small>

            </span>

          </button>


          <!-- 03 菜单 -->

          <button
            @click="navigate('menu')"
          >

            <span class="number">
              03
            </span>

            <span class="nav-text">

              <strong>
                OUR MENU
              </strong>

              <small>
                招牌菜
              </small>

            </span>

          </button>


          <!-- 04 我的订单 -->

          <button
            @click="goOrdersFromMenu"
          >

            <span class="number">
              04
            </span>

            <span class="nav-text">

              <strong>
                MY ORDERS
              </strong>

              <small>
                我的订单
              </small>

            </span>

          </button>


          <!-- 05 餐厅空间 -->

          <button
            @click="navigate('space')"
          >

            <span class="number">
              05
            </span>

            <span class="nav-text">

              <strong>
                THE SPACE
              </strong>

              <small>
                餐厅空间
              </small>

            </span>

          </button>


          <!-- 06 联系我们 -->

          <button
            @click="navigate('contact')"
          >

            <span class="number">
              06
            </span>

            <span class="nav-text">

              <strong>
                CONTACT
              </strong>

              <small>
                联系我们
              </small>

            </span>

          </button>

        </nav>


        <div class="menu-footer">

          <span>
            DINING / 2026
          </span>

          <span>
            11:00 — 22:00
          </span>

        </div>

      </div>

    </Transition>


    <!-- ==================== HERO ==================== -->

    <section
      id="home"
      class="hero section"
    >

      <!-- 这里暂时使用渐变背景 -->
      <!-- 后面换成真正的餐厅图片 -->

      <div class="hero-image"></div>

      <div class="hero-overlay"></div>


      <div class="hero-content">

        <p class="eyebrow">
          GOOD FOOD · GOOD MOMENTS
        </p>


        <h1>
          懂你的味蕾<br>
          更懂你的品味
        </h1>


        <p class="hero-description">
          每一餐，都是一场值得期待的仪式。
        </p>


        <button
          class="discover"
          @click="scrollTo('menu')"
        >

          <span>
            DISCOVER OUR MENU
          </span>

          <span class="discover-arrow">
            ↓
          </span>

        </button>

      </div>


      <div class="hero-bottom">

        <span>
          RESTAURANT / 01
        </span>

        <span class="scroll-text">
          SCROLL TO EXPLORE
        </span>

      </div>

    </section>


    <!-- ==================== ABOUT ==================== -->

    <section
      id="about"
      class="about section"
    >

      <div class="section-label">

        <span>
          02
        </span>

        <span>
          ABOUT US
        </span>

      </div>


      <div class="about-content">

        <div class="about-title">

          <p class="english-title">
            WE BELIEVE IN
          </p>

          <h2>
            把平凡的日子，<br>
            盛进精致的餐盘里
          </h2>

        </div>


        <div class="about-text">

          <p>
            我们相信，食物连接的不只是味觉，
            还有人与人之间的相遇。
          </p>

          <p>
            从新鲜食材，到每一道菜的呈现，
            我们希望让每一次用餐，
            都成为值得记住的时刻。
          </p>

          <span class="about-sign">
            MORI RESTAURANT
          </span>

        </div>

      </div>

    </section>


    <!-- ==================== MENU ==================== -->

    <section id="menu">

      <div class="section-heading">

        <div class="section-title">

          <span>
            OUR MENU
          </span>

          <h2>
            今日菜单
          </h2>

        </div>


        <button
          class="full-menu-button"
          @click="router.push('/menu')"
        >

          <span>
            查看完整菜单
          </span>

          <span class="button-arrow">
            ↗
          </span>

        </button>

      </div>


      <div class="dish-grid">

        <article
          v-for="dish in dishes.slice(0, 6)"
          :key="dish.id"
          class="dish-card"
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


          <div class="dish-info">

            <div>

              <span class="dish-en">
                {{ dish.categoryName }}
              </span>

              <h3>
                {{ dish.name }}
              </h3>

              <span class="dish-price">
                ¥{{ dish.price }}
              </span>

            </div>


            <span class="dish-arrow">
              ↗
            </span>

          </div>

        </article>

      </div>

    </section>


    <!-- ==================== SPACE ==================== -->

    <section
      id="space"
      class="space section"
    >

      <div class="section-label">

        <span>
          05
        </span>

        <span>
          THE SPACE
        </span>

      </div>


      <div class="space-content">

        <div class="space-title">

          <p>
            A PLACE TO STAY
          </p>

          <h2>
            懂美食，更懂你
          </h2>

        </div>


        <div class="space-image">

          <div class="space-image-inner"></div>

        </div>

      </div>

    </section>


    <!-- ==================== CONTACT ==================== -->

    <section
      id="contact"
      class="contact section"
    >

      <div class="contact-inner">

        <p class="contact-label">
          COME & DINE
        </p>


        <h2>
          欢迎体验
        </h2>


        <div class="contact-info">

          <div>

            <span>
              OPENING HOURS
            </span>

            <strong>
              11:00 — 22:00
            </strong>

          </div>


          <div>

            <span>
              LOCATION
            </span>

            <strong>
              YOUR CITY · CHINA
            </strong>

          </div>

        </div>


        <!-- 管理后台 -->

        <button
          class="admin-login"
          @click="goLogin"
        >

          <span>
            进入管理后台
          </span>

          <span>
            ↗
          </span>

        </button>

      </div>


      <footer>

        <span>
          © 2026 MORI RESTAURANT
        </span>

        <span>
          GOOD FOOD · GOOD MOMENTS
        </span>

      </footer>

    </section>

  </div>

</template>


<script setup>

import {
  ref,
  onMounted
} from "vue"

import {
  useRouter
} from "vue-router"

import {
  getDishList
} from "@/api/dish"


// =========================
// Router
// =========================

const router = useRouter()


// =========================
// 顾客登录状态
// =========================

const customer = ref(null)


// =========================
// 导航菜单
// =========================

const menuOpen = ref(false)


// =========================
// 首页菜品
// =========================

const dishes = ref([])


// =========================
// 后端地址
// =========================

const API_BASE_URL =
  "http://localhost:8080"


// =========================
// 获取菜品图片
// =========================

function getDishImage(image) {

  if (!image) {

    return API_BASE_URL +
      "/upload/default-dish.png"

  }


  if (image.startsWith("http")) {

    return image

  }


  return API_BASE_URL + image

}


// =========================
// 加载首页菜品
// =========================

async function loadDishes() {

  try {

    const res =
      await getDishList()


    if (
      res.data.code === 200
    ) {

      dishes.value =
        res.data.data.records || []

    }

  } catch (error) {

    console.error(
      "获取首页菜品失败：",
      error
    )

  }

}


// =========================
// 页面加载
// =========================

onMounted(() => {

  loadDishes()


  const data =
    localStorage.getItem(
      "customer"
    )


  if (data) {

    try {

      customer.value =
        JSON.parse(data)

    } catch (error) {

      console.error(
        "读取顾客信息失败：",
        error
      )

      localStorage.removeItem(
        "customer"
      )

    }

  }

})


// =========================
// 顾客登录
// =========================

function goLogin1() {
  // 拿到当前页面的完整路径（比如 /dish/101）
  const currentPath = router.currentRoute.value.fullPath;

  // 跳转到登录页，并通过 query 参数携带“来源地址”
  router.push({
    path: "/customer/login",
    query: { redirect: currentPath }
  });
}


// =========================
// 我的订单
// =========================

function goOrders() {

  router.push(
    "/customer/orders"
  )

}


// =========================
// 导航抽屉中的“我的订单”
// =========================

function goOrdersFromMenu() {

  // 先关闭导航抽屉

  menuOpen.value = false


  // 没有登录

  if (!customer.value) {

    router.push({

      path:
        "/customer/login",

      query: {

        redirect:
          "/customer/orders"

      }

    })

    return

  }


  // 已登录

  router.push(
    "/customer/orders"
  )

}


// =========================
// 滚动到指定区域
// =========================

function scrollTo(id) {

  menuOpen.value = false


  const element =
    document.getElementById(id)


  if (element) {

    element.scrollIntoView({

      behavior: "smooth"

    })

  }

}


// =========================
// 导航
// =========================

function navigate(id) {

  scrollTo(id)

}


// =========================
// 管理后台登录
// =========================

function goLogin() {

  menuOpen.value = false

  router.push("/login")

}

</script>


<style scoped>

@import url(
'https://fonts.googleapis.com/css2?family=Cormorant+Garamond:wght@400;500;600&family=Noto+Sans+SC:wght@300;400;500&display=swap'
);


/* =========================
   基础
========================= */

* {
  box-sizing: border-box;
}


.home {

  background: #f6f4ef;

  color: #1d1d1b;

  font-family:
    "Noto Sans SC",
    sans-serif;

  overflow-x: hidden;

}


button {

  border: none;

  background: none;

  font-family: inherit;

  cursor: pointer;

}


/* =========================
   顶部
========================= */

.header {

  position: fixed;

  top: 0;

  left: 0;

  right: 0;

  z-index: 100;

  height: 90px;

  padding: 0 48px;

  display: flex;

  align-items: center;

  justify-content: space-between;

  color: white;

}


.logo {

  display: flex;

  flex-direction: column;

  cursor: pointer;

}


.logo-main {

  font-family:
    "Cormorant Garamond",
    serif;

  font-size: 28px;

  letter-spacing: 4px;

  line-height: 1;

}


.logo-sub {

  margin-top: 5px;

  font-size: 8px;

  letter-spacing: 3px;

  opacity: .7;

}


.header-actions {

  display: flex;

  align-items: center;

  gap: 28px;

}


.menu-button,
.login-button {

  display: flex;

  align-items: center;

  gap: 9px;

  color: white;

  font-size: 11px;

  letter-spacing: 2px;

}


.nav-button,
.login-button,
.customer-button {

  padding: 0;

  border: none;

  background: none;

  font-size: 14px;

  color: white;

  cursor: pointer;

  letter-spacing: .08em;

  transition: opacity .25s;

}


.nav-button:hover,
.login-button:hover,
.customer-button:hover {

  opacity: .55;

}


.login-button {

  padding-left: 24px;

  border-left:
    1px solid
    rgba(255,255,255,.45);

}


.admin-button {

  padding: 8px 20px;

  border: 1px solid
    rgba(255,255,255,.5);

  background: transparent;

  color: white;

  font-size: 11px;

  letter-spacing: 2px;

  transition:
    background .3s,
    color .3s;

}


.admin-button:hover {

  background: white;

  color: #1d1d1b;

}


.menu-icon {

  width: 21px;

  display: flex;

  flex-direction: column;

  gap: 6px;

}


.menu-icon i {

  display: block;

  width: 100%;

  height: 1px;

  background: white;

}


.arrow {

  font-size: 16px;

  transition:
    transform .3s;

}


.login-button:hover .arrow {

  transform:
    translate(3px, -3px);

}


.header-right {

  display: flex;

  align-items: center;

  gap: 28px;

}


/* =========================
   Hero
========================= */

.hero {

  position: relative;

  height: 100vh;

  min-height: 680px;

  color: white;

  display: flex;

  align-items: center;

}


.hero-image {

  position: absolute;

  inset: 0;

  background:
    linear-gradient(
      135deg,
      #6e6255,
      #2e2a26
    );

}


.hero-overlay {

  position: absolute;

  inset: 0;

  background:
    linear-gradient(
      90deg,
      rgba(0,0,0,.52),
      rgba(0,0,0,.08)
    );

}


.hero-content {

  position: relative;

  z-index: 2;

  width:
    min(
      1200px,
      calc(100% - 96px)
    );

  margin: 0 auto;

  padding-top: 70px;

}


.eyebrow {

  margin: 0 0 28px;

  font-size: 10px;

  letter-spacing: 4px;

  opacity: .8;

}


.hero h1 {

  margin: 0;

  font-family:
    "Cormorant Garamond",
    "Noto Serif SC",
    serif;

  font-size:
    clamp(
      62px,
      8vw,
      120px
    );

  font-weight: 400;

  line-height: 1.05;

  letter-spacing: -3px;

}


.hero-description {

  margin-top: 35px;

  font-size: 15px;

  font-weight: 300;

  letter-spacing: 2px;

}


.discover {

  margin-top: 60px;

  padding: 0;

  display: flex;

  align-items: center;

  gap: 25px;

  color: white;

  font-size: 10px;

  letter-spacing: 3px;

}


.discover-arrow {

  width: 40px;

  height: 40px;

  border:
    1px solid
    rgba(255,255,255,.5);

  border-radius: 50%;

  display: flex;

  justify-content: center;

  align-items: center;

  font-size: 15px;

  transition:
    transform .3s;

}


.discover:hover
.discover-arrow {

  transform:
    translateY(5px);

}


.hero-bottom {

  position: absolute;

  z-index: 2;

  left: 48px;

  right: 48px;

  bottom: 30px;

  display: flex;

  justify-content: space-between;

  font-size: 9px;

  letter-spacing: 2px;

  opacity: .7;

}


/* =========================
   通用 section
========================= */

.section {

  position: relative;

}


.section-label {

  display: flex;

  align-items: center;

  gap: 18px;

  font-size: 9px;

  letter-spacing: 3px;

  color: #77736b;

}


.section-label span:first-child {

  color: #8c6a43;

}


.section-label.light {

  color:
    rgba(255,255,255,.6);

}


.section-label.light
span:first-child {

  color: #c7a77e;

}


/* =========================
   About
========================= */

.about {

  min-height: 100vh;

  padding: 130px 8vw;

  display: flex;

  flex-direction: column;

}


.about-content {

  flex: 1;

  display: grid;

  grid-template-columns:
    1fr 1fr;

  align-items: center;

  gap: 10vw;

}


.english-title {

  margin:
    0 0 25px;

  font-size: 10px;

  letter-spacing: 4px;

  color: #8c6a43;

}


.about h2 {

  margin: 0;

  font-family:
    "Cormorant Garamond",
    "Noto Serif SC",
    serif;

  font-weight: 400;

  font-size:
    clamp(
      45px,
      5vw,
      75px
    );

  line-height: 1.2;

}


.about-text {

  max-width: 470px;

}


.about-text p {

  margin:
    0 0 28px;

  font-size: 16px;

  line-height: 2.2;

  font-weight: 300;

  color: #5d5a54;

}


.about-sign {

  display: block;

  margin-top: 50px;

  font-family:
    "Cormorant Garamond",
    serif;

  font-size: 18px;

  letter-spacing: 3px;

}


/* =========================
   Menu
========================= */

.menu-section {

  min-height: 100vh;

  padding: 120px 6vw;

  background: #24211e;

  color: #f6f4ef;

}


.menu-heading {

  margin:
    80px 0 60px;

}


.menu-heading p {

  margin:
    0 0 15px;

  font-size: 9px;

  letter-spacing: 4px;

  color: #b49b7e;

}


.menu-heading h2 {

  margin: 0;

  font-family:
    "Cormorant Garamond",
    "Noto Serif SC",
    serif;

  font-size:
    clamp(
      55px,
      7vw,
      100px
    );

  font-weight: 400;

}


.dish-price {

  display: block;

  margin-top: 12px;

  font-family:
    "Cormorant Garamond",
    serif;

  font-size: 20px;

  color: #c7a77e;

}


.dish-grid {

  display: grid;

  grid-template-columns:
    repeat(3, 1fr);

  gap: 22px;

}


.dish-card {

  cursor: pointer;

}


.dish-image {

  height: 420px;

  overflow: hidden;

  position: relative;

  background: #5c5147;

  transition:
    transform .6s,
    filter .6s;

}


.dish-image img {

  width: 100%;

  height: 100%;

  object-fit: cover;

  display: block;

  transition:
    transform .6s;

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

  font-family:
    "Cormorant Garamond",
    serif;

  font-size: 28px;

  letter-spacing: 3px;

  color:
    rgba(255,255,255,.7);

  background:
    linear-gradient(
      135deg,
      #806f5d,
      #39332d
    );

}


.dish-card:hover
.dish-image {

  transform:
    scale(.98);

  filter:
    brightness(1.1);

}


.image-one {

  background:
    linear-gradient(
      145deg,
      #9a8065,
      #3c3028
    );

}


.image-two {

  background:
    linear-gradient(
      145deg,
      #777d6b,
      #302f28
    );

}


.image-three {

  background:
    linear-gradient(
      145deg,
      #a48d72,
      #494036
    );

}


.dish-info {

  padding:
    22px 0;

  display: flex;

  justify-content:
    space-between;

  align-items:
    flex-start;

  border-bottom:
    1px solid
    rgba(255,255,255,.2);

}


.dish-en {

  font-size: 8px;

  letter-spacing: 3px;

  color: #a9a29a;

}


.dish-info h3 {

  margin:
    8px 0 0;

  font-size: 19px;

  font-weight: 400;

}


.dish-price {

  display: block;

  margin-top: 12px;

  font-family:
    "Cormorant Garamond",
    serif;

  font-size: 20px;

  color: #c7a77e;

}


.dish-arrow {

  font-size: 22px;

  transition:
    transform .3s;

}


.dish-card:hover
.dish-arrow {

  transform:
    translate(4px,-4px);

}


/* =========================
   Space
========================= */

.space {

  min-height: 100vh;

  padding: 130px 8vw;

}


.space-content {

  margin-top: 80px;

  display: grid;

  grid-template-columns:
    .8fr 1.5fr;

  gap: 8vw;

  align-items: center;

}


.space-title p {

  font-size: 9px;

  letter-spacing: 4px;

  color: #8c6a43;

}


.space-title h2 {

  font-family:
    "Cormorant Garamond",
    "Noto Serif SC",
    serif;

  font-size:
    clamp(
      45px,
      5vw,
      75px
    );

  font-weight: 400;

  line-height: 1.2;

}


.space-image {

  height: 620px;

  overflow: hidden;

}


.space-image-inner {

  width: 100%;

  height: 100%;

  background:
    linear-gradient(
      135deg,
      #827666,
      #37312b
    );

  transition:
    transform .8s;

}


.space-image:hover
.space-image-inner {

  transform:
    scale(1.03);

}


/* =========================
   Contact
========================= */

.contact {

  min-height: 90vh;

  padding:
    150px 8vw 40px;

  background: #e8e1d7;

  display: flex;

  flex-direction: column;

}


.contact-inner {

  flex: 1;

  display: flex;

  flex-direction: column;

  align-items: center;

  justify-content: center;

  text-align: center;

}


.contact-label {

  margin:
    0 0 25px;

  font-size: 9px;

  letter-spacing: 4px;

  color: #8c6a43;

}


.contact h2 {

  margin: 0;

  font-family:
    "Cormorant Garamond",
    "Noto Serif SC",
    serif;

  font-size:
    clamp(
      60px,
      8vw,
      110px
    );

  font-weight: 400;

}


.contact-info {

  margin-top: 70px;

  display: flex;

  gap: 100px;

}


.contact-info div {

  display: flex;

  flex-direction: column;

  gap: 12px;

}


.contact-info span {

  font-size: 8px;

  letter-spacing: 3px;

  color: #77736b;

}


.contact-info strong {

  font-size: 13px;

  font-weight: 400;

  letter-spacing: 1px;

}


.admin-login {

  margin-top: 60px;

  padding:
    18px 30px;

  border:
    1px solid #292622;

  display: flex;

  gap: 30px;

  color: #292622;

  font-size: 10px;

  letter-spacing: 2px;

  transition:
    background .3s,
    color .3s;

}


.admin-login:hover {

  background: #292622;

  color: white;

}


footer {

  padding-top: 30px;

  border-top:
    1px solid
    rgba(41,38,34,.2);

  display: flex;

  justify-content:
    space-between;

  font-size: 8px;

  letter-spacing: 2px;

  color: #77736b;

}


/* =========================
   全屏导航
========================= */

.menu-overlay {

  position: fixed;

  inset: 0;

  z-index: 200;

  background: #24211e;

  color: #f6f4ef;

  padding: 0 48px;

}


.menu-top {

  height: 90px;

  display: flex;

  align-items: center;

  justify-content:
    space-between;

}


.close-button {

  display: flex;

  align-items: center;

  gap: 10px;

  color: white;

  font-size: 10px;

  letter-spacing: 2px;

}


.close-button span {

  font-size: 28px;

  font-weight: 200;

}


.navigation {

  height:
    calc(100vh - 180px);

  display: flex;

  flex-direction: column;

  justify-content: center;

  max-width: 850px;

  margin: auto;

}


.navigation button {

  padding: 17px 0;

  display: flex;

  align-items: center;

  gap: 35px;

  color: white;

  text-align: left;

  border-bottom:
    1px solid
    rgba(255,255,255,.12);

}


.number {

  width: 35px;

  font-size: 9px;

  letter-spacing: 2px;

  color: #9d8871;

}


.nav-text {

  display: flex;

  align-items: baseline;

  gap: 20px;

}


.nav-text strong {

  font-family:
    "Cormorant Garamond",
    serif;

  font-size:
    clamp(
      30px,
      4vw,
      52px
    );

  font-weight: 400;

  letter-spacing: 1px;

  transition:
    transform .3s;

}


.nav-text small {

  font-size: 10px;

  color: #aaa39b;

}


.navigation button:hover
.nav-text strong {

  transform:
    translateX(15px);

}


.menu-footer {

  position: absolute;

  left: 48px;

  right: 48px;

  bottom: 30px;

  display: flex;

  justify-content:
    space-between;

  font-size: 8px;

  letter-spacing: 2px;

  color: #918b83;

}


/* =========================
   导航动画
========================= */

.menu-enter-active,
.menu-leave-active {

  transition:
    opacity .35s,
    transform .35s;

}


.menu-enter-from,
.menu-leave-to {

  opacity: 0;

  transform:
    translateY(-20px);

}


/* =========================
   手机
========================= */

@media (max-width: 768px) {

  .header {

    height: 70px;

    padding:
      0 22px;

  }


  .logo-main {

    font-size: 23px;

  }


  .header-actions {

    gap: 15px;

  }


  .header-right {

    gap: 15px;

  }


  .login-button {

    font-size: 9px;

    padding-left: 15px;

  }


  .nav-button,
  .customer-button {

    font-size: 12px;

  }


  .admin-button {

    font-size: 9px;

    padding: 6px 14px;

  }


  .hero {

    min-height: 600px;

  }


  .hero-content {

    width:
      calc(100% - 44px);

  }


  .hero h1 {

    font-size: 58px;

    letter-spacing: -2px;

  }


  .hero-bottom {

    left: 22px;

    right: 22px;

  }


  .scroll-text {

    display: none;

  }


  .about {

    padding:
      90px 22px;

  }


  .about-content {

    grid-template-columns: 1fr;

    gap: 60px;

  }


  .menu-section {

    padding:
      90px 22px;

  }


  .dish-grid {

    grid-template-columns: 1fr;

  }


  .dish-image {

    height: 360px;

  }


  .space {

    padding:
      90px 22px;

  }


  .space-content {

    grid-template-columns: 1fr;

    gap: 45px;

  }


  .space-image {

    height: 450px;

  }


  .contact {

    padding:
      100px 22px 30px;

  }


  .contact-info {

    flex-direction: column;

    gap: 35px;

  }


  footer {

    flex-direction: column;

    gap: 12px;

  }


  .menu-overlay {

    padding:
      0 22px;

  }


  .menu-footer {

    left: 22px;

    right: 22px;

  }


  .navigation button {

    gap: 15px;

  }


  .nav-text {

    flex-direction: column;

    gap: 3px;

  }


  .nav-text strong {

    font-size: 34px;

  }

}


/* =========================
   菜单标题
========================= */

.section-heading {

  max-width: 1400px;

  margin:
    0 auto 55px;

  display: flex;

  justify-content:
    space-between;

  align-items: flex-end;

  border-bottom:
    1px solid
    rgba(39, 35, 31, .15);

  padding-bottom: 22px;

}


.section-title {

  margin: 0;

}


.full-menu-button {

  display: flex;

  align-items: center;

  gap: 18px;

  border: 0;

  border-bottom:
    1px solid #27231f;

  background: transparent;

  padding: 8px 0;

  color: #27231f;

  cursor: pointer;

  font-size: 12px;

  letter-spacing: 2px;

  transition:
    gap .3s ease,
    opacity .3s ease;

}


.full-menu-button:hover {

  gap: 25px;

  opacity: .65;

}


.button-arrow {

  font-size: 18px;

  line-height: 1;

}


@media (max-width: 700px) {

  .section-heading {

    align-items: flex-start;

    gap: 25px;

    flex-direction: column;

  }


  .full-menu-button {

    align-self: flex-start;

  }

}

</style>
