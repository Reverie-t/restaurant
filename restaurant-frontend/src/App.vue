<script setup>
import { RouterView } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElConfigProvider } from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import en from 'element-plus/es/locale/lang/en'
import RefreshButton from '@/components/RefreshButton.vue'
import { computed } from 'vue'
const { locale } = useI18n()
const elementLocale = computed(() =>
  locale.value === 'en-US' ? en : zhCn
)
</script>

<template>
  <el-config-provider :locale="elementLocale">
    <RouterView />
    <RefreshButton />
  </el-config-provider>
</template>
