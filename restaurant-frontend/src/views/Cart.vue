<template>

  <div class="cart-page">

    <header class="cart-header">

      <div
        class="logo"
        @click="router.push('/')"
      >
        MORI
      </div>

      <button
        class="back-button"
        @click="router.push('/menu/order')"
      >
        ← CONTINUE ORDER
      </button>

    </header>


    <main class="cart-content">

      <div class="cart-title">

        <span>YOUR ORDER</span>

        <h1>点餐车</h1>

      </div>


      <!-- 空购物车 -->

      <div
        v-if="cart.items.length === 0"
        class="empty-cart"
      >

        <p>
          你的点餐车还是空的。
        </p>

        <button
          @click="router.push('/menu')"
        >
          浏览菜单 →
        </button>

      </div>


      <!-- 购物车 -->

      <div
        v-else
        class="cart-list"
      >

        <div
          v-for="item in cart.items"
          :key="item.id"
          class="cart-item"
        >

          <div class="item-image">

            <img
              v-if="item.image"
              :src="getDishImage(item.image)"
              :alt="item.name"
            />

          </div>


          <div class="item-info">

            <span>
              {{ item.categoryName }}
            </span>

            <h2>
              {{ item.name }}
            </h2>

            <p>
              ¥{{ item.price }}
            </p>

          </div>


          <div class="quantity">

            <button
              @click="cart.decrease(item.id)"
            >
              −
            </button>

            <span>
              {{ item.quantity }}
            </span>

            <button
              @click="cart.increase(item.id)"
            >
              +
            </button>

          </div>


          <div class="item-total">

            ¥{{
              (
                item.price *
                item.quantity
              ).toFixed(2)
            }}

          </div>


          <button
            class="remove"
            @click="cart.removeItem(item.id)"
          >
            ×
          </button>

        </div>


        <!-- 底部 -->

        <div class="cart-summary">

          <div>

            <span>
              {{ cart.totalQuantity }} 份
            </span>

            <strong>
              ¥{{ cart.totalAmount.toFixed(2) }}
            </strong>

          </div>


          <button
            class="checkout-button"
            @click="checkout"
          >
            确认订单
            <span>↗</span>
          </button>

        </div>

      </div>

    </main>

  </div>

</template>


<script setup>

import { useRouter } from "vue-router"

import { useCartStore } from "@/stores/cart"


const router = useRouter()

const cart = useCartStore()


const API_BASE_URL =
  "http://localhost:8080"


function getDishImage(image) {

  if (!image) {
    return ""
  }

  if (image.startsWith("http")) {
    return image
  }

  return API_BASE_URL + image

}


function checkout() {

  if (cart.totalQuantity === 0) {
    return
  }

  router.push("/menu/confirm")

}

</script>


<style scoped>

.cart-page {

  min-height: 100vh;

  background: #f5f1ea;

  color: #27231f;

  padding:
    35px 6vw 100px;

}


.cart-header {

  display: flex;

  justify-content: space-between;

  align-items: center;

  padding-bottom: 30px;

  border-bottom:
    1px solid rgba(39,35,31,.15);

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


.cart-content {

  max-width: 1200px;

  margin: 80px auto;

}


.cart-title span {

  font-size: 11px;

  letter-spacing: 4px;

  opacity: .5;

}


.cart-title h1 {

  margin:
    15px 0 60px;

  font-family:
    "Cormorant Garamond",
    serif;

  font-size: 64px;

  font-weight: 400;

}


.cart-item {

  display: grid;

  grid-template-columns:
    150px
    1fr
    140px
    120px
    30px;

  gap: 30px;

  align-items: center;

  padding: 25px 0;

  border-top:
    1px solid rgba(39,35,31,.15);

}


.item-image {

  width: 150px;

  height: 150px;

  overflow: hidden;

  background: #5c5147;

}


.item-image img {

  width: 100%;

  height: 100%;

  object-fit: cover;

}


.item-info span {

  font-size: 10px;

  letter-spacing: 3px;

  opacity: .5;

}


.item-info h2 {

  margin: 10px 0;

  font-family:
    "Cormorant Garamond",
    serif;

  font-size: 28px;

  font-weight: 400;

}


.item-info p {

  margin: 0;

  opacity: .7;

}


.quantity {

  display: flex;

  align-items: center;

  gap: 18px;

}


.quantity button {

  border: 0;

  background: transparent;

  cursor: pointer;

  font-size: 20px;

}


.quantity span {

  min-width: 20px;

  text-align: center;

}


.item-total {

  font-family:
    "Cormorant Garamond",
    serif;

  font-size: 22px;

}


.remove {

  border: 0;

  background: transparent;

  cursor: pointer;

  font-size: 20px;

  opacity: .5;

}


.cart-summary {

  margin-top: 50px;

  padding-top: 30px;

  border-top:
    1px solid #27231f;

}


.cart-summary > div {

  display: flex;

  justify-content: space-between;

  align-items: center;

}


.cart-summary span {

  font-size: 12px;

  letter-spacing: 2px;

}


.cart-summary strong {

  font-family:
    "Cormorant Garamond",
    serif;

  font-size: 34px;

  font-weight: 400;

}


.checkout-button {

  width: 100%;

  margin-top: 30px;

  padding: 20px 0;

  border: 0;

  border-bottom:
    1px solid #27231f;

  background: transparent;

  display: flex;

  justify-content: space-between;

  cursor: pointer;

  font-size: 12px;

  letter-spacing: 3px;

}


.checkout-button span {

  font-size: 20px;

}


.empty-cart {

  padding: 100px 0;

  border-top:
    1px solid rgba(39,35,31,.15);

}


.empty-cart p {

  opacity: .6;

}


.empty-cart button {

  margin-top: 20px;

  border: 0;

  border-bottom: 1px solid;

  background: transparent;

  padding: 10px 0;

  cursor: pointer;

  letter-spacing: 2px;

}


@media (max-width: 800px) {

  .cart-title h1 {

    font-size: 48px;

  }


  .cart-item {

    grid-template-columns:
      100px 1fr;

    gap: 20px;

  }


  .item-image {

    width: 100px;

    height: 100px;

  }


  .quantity,
  .item-total,
  .remove {

    grid-column: 2;

  }

}

</style>
