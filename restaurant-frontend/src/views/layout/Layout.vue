<template>
  <el-container class="layout">
    <!-- Sidebar -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="aside">
      <div class="logo" :class="{ 'logo-collapse': isCollapse }">
        <span v-if="!isCollapse" class="logo-text">{{ $t('app.title') }}</span>
        <span v-else class="logo-text">{{ $t('app.titleShort') }}</span>
      </div>

      <el-menu
        router
        :default-active="currentRoute"
        :collapse="isCollapse"
        class="side-menu"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <template #title>{{ $t('menu.home') }}</template>
        </el-menu-item>

        <el-menu-item index="/admin/user">
          <el-icon><User /></el-icon>
          <template #title>{{ $t('menu.user') }}</template>
        </el-menu-item>

        <el-menu-item index="/admin/category">
          <el-icon><Menu /></el-icon>
          <template #title>分类管理</template>
        </el-menu-item>

        <el-menu-item index="/admin/dish">
          <el-icon><Food /></el-icon>
          <template #title>菜品管理</template>
        </el-menu-item>

        <el-menu-item index="/admin/table">
          <el-icon><Grid /></el-icon>
          <template #title>餐桌管理</template>
        </el-menu-item>

        <el-menu-item index="/admin/order">
          <el-icon><List /></el-icon>
          <template #title>订单管理</template>
        </el-menu-item>

        <el-menu-item index="/admin/takeaway">
          <el-icon><TakeawayBox /></el-icon>
          <template #title>外卖管理</template>
        </el-menu-item>

        <el-menu-item index="/admin/ingredient">
          <el-icon><Box /></el-icon>
          <template #title>原材料管理</template>
        </el-menu-item>
        <el-menu-item index="/admin/expense">
          <el-icon><Money /></el-icon>
          <template #title>费用管理</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- Main area -->
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <span class="welcome-text">欢迎使用餐馆运营管理系统</span>
        </div>

        <div class="user-info">
  <el-avatar :size="30" class="user-avatar">
    {{ username.charAt(0) }}
  </el-avatar>
  <span class="username">{{ username }}</span>

  <el-dropdown @command="changeLocale">
    <span class="locale-trigger">
      {{ locale === 'zh-CN' ? '中文' : 'English' }}
    </span>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item command="zh-CN">中文</el-dropdown-item>
        <el-dropdown-item command="en-US">English</el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>

  <el-button type="danger" size="small" @click="logout" plain>
    退出登录
  </el-button>
</div>
      </el-header>

      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  HomeFilled, User, Menu, Food, Grid,Money,TakeawayBox,Box, List, Fold, Expand
} from '@element-plus/icons-vue'


import zhCn from 'element-plus/es/locale/lang/zh-cn'
import en from 'element-plus/es/locale/lang/en'

const { locale } = useI18n()



function changeLocale(lang) {
  locale.value = lang
  localStorage.setItem('locale', lang)
}
const router = useRouter()
const route = useRoute()

const isCollapse = ref(false)
const currentRoute = computed(() => route.path)

const user = JSON.parse(localStorage.getItem("user"))
const username = user?.username || t('auth.notLoggedIn')

const logout = () => {
  localStorage.removeItem("user")
  localStorage.removeItem("token")
  router.push("/login")
}
</script>

<style scoped>
.layout {
  height: 100vh;
}

/* ── Sidebar ── */
.aside {
  background: var(--btn-primary);
  transition: width 0.3s var(--ease);
  overflow: hidden;
}

.logo {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.logo-text {
  color: #fff;
  font-family: "Cormorant Garamond", serif;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 0.06em;
}

.logo-collapse .logo-text {
  font-size: 18px;
  letter-spacing: 0;
}

/* ── Menu items ── */
.side-menu {
  background: transparent;
  border: none;
}

.side-menu .el-menu-item {
  color: rgba(255, 255, 255, 0.5);
  height: 44px;
  line-height: 44px;
  margin: 2px 8px;
  font-size: 14px;
}

.side-menu .el-menu-item:hover {
  background: rgba(255, 255, 255, 0.08);
  color: #fff;
}

.side-menu .el-menu-item.is-active {
  background: rgba(255, 255, 255, 0.12);
  color: #fff;
}

/* ── Header ── */
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: var(--surface-card);
  border-bottom: 1px solid var(--border-light);
  padding: 0 var(--sp-6);
  height: 56px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: var(--sp-4);
}

.collapse-btn {
  font-size: 18px;
  cursor: pointer;
  color: var(--text-3);
  transition: color var(--duration) var(--ease);
}

.collapse-btn:hover {
  color: var(--text-1);
}

.welcome-text {
  color: var(--text-3);
  font-size: 14px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: var(--sp-3);
}

.user-avatar {
  background: var(--btn-primary);
  color: #fff;
  font-size: 13px;
  font-weight: 600;
}

.username {
  color: var(--text-2);
  font-size: 13px;
  font-weight: 500;
}

/* ── Content ── */
.main-content {
  background: var(--surface-page);
  padding: 0;
  overflow-y: auto;
}
</style>
