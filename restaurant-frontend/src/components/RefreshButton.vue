<template>

  <transition name="refresh-fade">

    <button
      v-if="visible"
      class="global-refresh"
      title="刷新页面"
      @click="refresh"
    >

      <svg
        class="refresh-icon"
        :class="{ spinning }"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="2"
        stroke-linecap="round"
        stroke-linejoin="round"
      >

        <path d="M21 12a9 9 0 1 1-2.64-6.36" />

        <polyline points="21 3 21 9 15 9" />

      </svg>

    </button>

  </transition>

</template>


<script setup>

import { ref, computed } from "vue"

import { useRoute } from "vue-router"


const route = useRoute()

const spinning = ref(false)


// 首页不显示
const visible = computed(() => route.path !== "/")


function refresh() {

  if (spinning.value) {

    return

  }

  spinning.value = true

  // 短暂转一圈再刷新，给出点击反馈
  setTimeout(() => {

    location.reload()

  }, 350)

}

</script>


<style scoped>

.global-refresh {

  position: fixed;

  right: 22px;

  bottom: 96px;

  z-index: 1800;

  width: 44px;

  height: 44px;

  border-radius: 50%;

  border: 1px solid #e7e1da;

  background: #ffffff;

  color: #3f3933;

  cursor: pointer;

  display: flex;

  align-items: center;

  justify-content: center;

  box-shadow: 0 6px 18px rgba(50, 40, 30, .10);

  transition:
    background .2s,
    color .2s,
    transform .2s;

}


.global-refresh:hover {

  background: #3f3933;

  color: #ffffff;

  transform: translateY(-2px);

}


.refresh-icon {

  width: 18px;

  height: 18px;

}


.refresh-icon.spinning {

  animation: refresh-spin .6s linear infinite;

}


@keyframes refresh-spin {

  to {

    transform: rotate(360deg);

  }

}


@media (prefers-reduced-motion: no-preference) {

  .refresh-fade-enter-active,

  .refresh-fade-leave-active {

    transition: opacity .25s;

  }

  .refresh-fade-enter-from,

  .refresh-fade-leave-to {

    opacity: 0;

  }

}

</style>
