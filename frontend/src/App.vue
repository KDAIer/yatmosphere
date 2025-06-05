<!-- src/App.vue -->
<template>
  <div id="app">
    <!-- 如果路由是 /login，就只渲染登录页 -->
    <template v-if="isLoginRoute">
      <router-view />
    </template>

    <!-- 其他路由，渲染带侧边栏的布局 -->
    <template v-else>
      <div class="layout-wrapper">
        <Sidebar />
        <div class="main-content">
          <router-view />
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import Sidebar from './components/Sidebar.vue'

const route = useRoute()
const isLoginRoute = computed(() => route.path === '/login')
</script>

<style scoped>
.layout-wrapper {
  display: flex;
  min-height: 100vh;
}

.main-content {
  flex: 1;
  padding: 0;
}
</style>
