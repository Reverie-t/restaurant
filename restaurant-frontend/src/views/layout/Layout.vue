<template>
  <el-container class="layout">
    <!-- Sidebar -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="aside">
      <div class="logo" :class="{ 'logo-collapse': isCollapse }">
        <span v-if="!isCollapse" class="logo-text">餐馆管理系统</span>
        <span v-else class="logo-text">餐</span>
      </div>

      <el-menu
        router
        :default-active="currentRoute"
        :collapse="isCollapse"
        class="side-menu"
      >
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <template #title>首页</template>
        </el-menu-item>

        <el-menu-item index="/user">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>

        <el-menu-item index="/category">
          <el-icon><Menu /></el-icon>
          <template #title>分类管理</template>
        </el-menu-item>

        <el-menu-item index="/dish">
          <el-icon><Food /></el-icon>
          <template #title>菜品管理</template>
        </el-menu-item>

        <el-menu-item index="/table">
          <el-icon><Grid /></el-icon>
          <template #title>餐桌管理</template>
        </el-menu-item>

        <el-menu-item index="/order">
          <el-icon><List /></el-icon>
          <template #title>订单管理</template>
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
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  HomeFilled, User, Menu, Food, Grid, List, Fold, Expand
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const isCollapse = ref(false)
const currentRoute = computed(() => route.path)

const user = JSON.parse(localStorage.getItem("user"))
const username = user?.username || "未登录"

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
  background: var(--surface-deep);
  transition: width 0.3s var(--ease);
  overflow: hidden;
  position: relative;
}

/* The signature element: a single amber line at the very top */
.aside::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: var(--accent);
  z-index: 1;
}

.logo {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.logo-text {
  color: var(--text-inverse);
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 0.08em;
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
  color: rgba(240, 237, 232, 0.55);
  height: 44px;
  line-height: 44px;
  margin: 2px 8px;
  border-radius: var(--radius-md);
  font-size: 14px;
}

.side-menu .el-menu-item:hover {
  background: rgba(255, 255, 255, 0.06);
  color: var(--text-inverse);
}

.side-menu .el-menu-item.is-active {
  background: var(--accent);
  color: #ffffff;
}

/* ── Header ── */
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: var(--surface-card);
  border-bottom: 1px solid var(--border);
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
  color: var(--text-2);
  font-size: 14px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: var(--sp-3);
}

.user-avatar {
  background: var(--accent);
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
  background: var(--surface-warm);
  padding: var(--sp-5);
  overflow-y: auto;
}
</style>
