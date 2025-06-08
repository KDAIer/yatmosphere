<template>
  <div id="app">
    <!-- 登录页单独处理 -->
    <template v-if="isLoginRoute">
      <router-view />
    </template>

    <!-- 主布局 -->
    <template v-else>
      <div class="layout-wrapper">
        <Sidebar />
        <div class="main-content">
          <router-view v-slot="{ Component, route }">
            <!-- 只对Dashboard组件进行缓存 -->
            <keep-alive include="Dashboard">
              <component
                :is="Component"
                :key="route.meta.usePathKey ? route.path : undefined"
              />
            </keep-alive>
          </router-view>
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
