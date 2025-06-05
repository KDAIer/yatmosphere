<template>
    <aside :class="['sidebar', { collapsed }]">
        <!-- 侧边栏头部：包含折叠按钮和（展开时显示的）标题 -->
        <div class="sidebar-header">
            <button class="collapse-btn" @click="toggleCollapse" :title="collapsed ? '展开侧边栏' : '收起侧边栏'">
                <span v-if="collapsed">▶</span>
                <span v-else>◀</span>
            </button>
            <h2 v-if="!collapsed" class="header-title">智能家居</h2>
        </div>

        <!-- 导航菜单项 -->
        <nav class="sidebar-nav">
            <ul>
                <li>
                    <router-link to="/dashboard" :class="{ active: isActive('/dashboard') }" class="nav-link"
                        :title="collapsed ? '仪表盘' : ''">
                        <!-- 建议将下列 span 换为你项目中实际的 SVG/Icon -->
                        <span class="icon">🏠</span>
                        <transition name="fade">
                            <span v-if="!collapsed" class="label">仪表盘</span>
                        </transition>
                        <!-- 激活时显示左侧高亮进度条 -->
                        <span v-if="isActive('/dashboard')" class="active-bar"></span>
                    </router-link>
                </li>
                <li>
                    <router-link to="/profile" :class="{ active: isActive('/profile') }" class="nav-link"
                        :title="collapsed ? '个人信息' : ''">
                        <span class="icon">👤</span>
                        <transition name="fade">
                            <span v-if="!collapsed" class="label">个人信息</span>
                        </transition>
                        <span v-if="isActive('/profile')" class="active-bar"></span>
                    </router-link>
                </li>
                <li>
                    <router-link to="/devices" :class="{ active: isActive('/devices') }" class="nav-link"
                        :title="collapsed ? '设备管理' : ''">
                        <span class="icon">💡</span>
                        <transition name="fade">
                            <span v-if="!collapsed" class="label">设备管理</span>
                        </transition>
                        <span v-if="isActive('/devices')" class="active-bar"></span>
                    </router-link>
                </li>
            </ul>
        </nav>

        <!-- 底部退出登录 -->
        <div class="sidebar-footer">
            <button class="logout-btn" @click="handleLogout" :title="collapsed ? '退出登录' : ''">
                <span class="icon">🚪</span>
                <transition name="fade">
                    <span v-if="!collapsed" class="label">退出登录</span>
                </transition>
            </button>
        </div>
    </aside>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

// collapsed = true: 侧边栏收起，false: 展开
const collapsed = ref(false)

function toggleCollapse() {
    collapsed.value = !collapsed.value
}

/** 判断当前路由是否处于激活状态，用于高亮显示 */
const isActive = (path) => route.path === path

/** 退出登录：清除 localStorage，然后跳回 /login */
function handleLogout() {
    localStorage.removeItem('authToken')
    localStorage.removeItem('username')
    localStorage.removeItem('role')
    router.push('/login')
}
</script>

<style scoped>
/* 整体侧边栏样式 */
.sidebar {
    display: flex;
    flex-direction: column;
    background: linear-gradient(to bottom, #2e3a4e, #1f2732);
    /* 渐变背景 */
    color: #e0e6ed;
    transition: width 0.3s ease, background 0.3s ease;
    width: 220px;
    min-height: 100vh;
    box-shadow: 2px 0 12px rgba(0, 0, 0, 0.25);
    position: relative;
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

/* 收起状态，仅显示图标 */
.sidebar.collapsed {
    width: 64px;
}

/* 侧边栏头部 */
.sidebar-header {
    display: flex;
    align-items: center;
    padding: 18px 16px;
    border-bottom: 1px solid rgba(224, 230, 237, 0.08);
}

/* 折叠按钮 */
.collapse-btn {
    background: none;
    border: none;
    color: #e0e6ed;
    cursor: pointer;
    font-size: 18px;
    margin-right: 14px;
    padding: 6px;
    transition: background-color 0.2s ease;
}

.collapse-btn:hover {
    background-color: rgba(224, 230, 237, 0.12);
    border-radius: 6px;
}

/* header 标题，仅在展开时显示 */
.header-title {
    margin: 0;
    font-size: 20px;
    font-weight: 600;
    white-space: nowrap;
    color: #f1f5f9;
}

/* 导航菜单 */
.sidebar-nav {
    flex: 1;
    padding-top: 20px;
}

.sidebar-nav ul {
    list-style: none;
    padding: 0;
    margin: 0;
}

.sidebar-nav li {
    margin-bottom: 6px;
}

/* 每个导航链接 */
.nav-link {
    display: flex;
    align-items: center;
    padding: 10px 16px;
    position: relative;
    color: #cfd8e3;
    text-decoration: none;
    border-radius: 8px;
    margin: 4px 8px;
    transition:
        background-color 0.2s ease,
        transform 0.2s ease,
        box-shadow 0.2s ease;
}

.nav-link:hover {
    background-color: rgba(255, 255, 255, 0.08);
    transform: translateX(2px);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.nav-link.active {
    background-color: #3a4756;
    color: #ffffff;
}

.nav-link.active:hover {
    background-color: #3a4756;
    transform: none;
    box-shadow: none;
}

/* 高亮进度条（左侧） */
.active-bar {
    position: absolute;
    left: 0;
    top: 4px;
    bottom: 4px;
    width: 4px;
    background-color: #1abc9c;
    border-radius: 4px 0 0 4px;
}

/* 图标 */
.icon {
    font-size: 20px;
    width: 24px;
    text-align: center;
    flex-shrink: 0;
}

/* 文本标签 */
.label {
    margin-left: 14px;
    font-size: 15px;
    white-space: nowrap;
    flex-shrink: 0;
    color: inherit;
    transition: opacity 0.3s ease, transform 0.3s ease;
}

/* 收起状态，隐藏文字时保留过渡 */
.sidebar.collapsed .label {
    opacity: 0;
    transform: translateX(-8px);
}

/* 底部退出按钮容器 */
.sidebar-footer {
    padding: 18px 16px;
    border-top: 1px solid rgba(224, 230, 237, 0.08);
}

/* 退出按钮 */
.logout-btn {
    display: flex;
    align-items: center;
    width: 100%;
    background: none;
    border: none;
    color: #cfd8e3;
    font-size: 15px;
    padding: 10px 16px;
    border-radius: 8px;
    cursor: pointer;
    position: relative;
    transition:
        background-color 0.2s ease,
        transform 0.2s ease,
        box-shadow 0.2s ease;
}

.logout-btn:hover {
    background-color: rgba(255, 255, 255, 0.08);
    transform: translateX(2px);
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
}

.logout-btn .icon {
    margin-right: 14px;
}

.sidebar.collapsed .logout-btn .label {
    opacity: 0;
    transform: translateX(-8px);
}

/* 文字渐隐过渡 */
.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.2s ease, transform 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
    opacity: 0;
    transform: translateX(-8px);
}

.fade-enter-to,
.fade-leave-from {
    opacity: 1;
    transform: translateX(0);
}
</style>
