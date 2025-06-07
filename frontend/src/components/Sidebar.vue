<template>
    <!-- 打开按钮：当侧边栏隐藏时显示 -->
    <button v-if="!showSidebar" class="open-btn" @click="showSidebar = true" title="打开侧边栏">
        ☰
    </button>

    <!-- 侧边栏主体 -->
    <aside v-show="showSidebar" :class="['sidebar', { collapsed }]">
        <!-- 侧边栏头部：关闭按钮（✖）和折叠按钮（❯/❮） -->
        <div class="sidebar-header">
            <button v-if="!collapsed" class="close-btn" @click="showSidebar = false" title="关闭侧边栏">✖</button>

            <button class="collapse-btn" @click.stop="toggleCollapse" :title="collapsed ? '展开侧边栏' : '收起侧边栏'">
                <span v-if="collapsed">❯</span>
                <span v-else>❮</span>
            </button>

            <!-- 标题仅在展开时显示 -->
            <h2 v-if="!collapsed" class="header-title">Yatmosphere</h2>
        </div>

        <!-- 导航菜单 -->
        <nav class="sidebar-nav">
            <ul>
                <li>
                    <router-link to="/dashboard" class="nav-link" :class="{ active: isActive('/dashboard') }"
                        :title="collapsed ? '仪表盘' : ''">
                        <span class="icon">🏠</span>
                        <transition name="fade">
                            <span v-if="!collapsed" class="label">仪表盘</span>
                        </transition>
                        <span v-if="isActive('/dashboard')" class="active-bar"></span>
                    </router-link>
                </li>
                <li>
                    <router-link to="/profile" class="nav-link" :class="{ active: isActive('/profile') }"
                        :title="collapsed ? '家庭管理' : ''">
                        <span class="icon">👪️</span>
                        <transition name="fade">
                            <span v-if="!collapsed" class="label">家庭管理</span>
                        </transition>
                        <span v-if="isActive('/profile')" class="active-bar"></span>
                    </router-link>
                </li>
                <li>
                    <router-link to="/devices" class="nav-link" :class="{ active: isActive('/devices') }"
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

        <!-- 额外控制面板：仅在未折叠时显示 -->
        <transition name="slide-fade">
            <div v-if="!collapsed" class="extras-panel">
                <!-- BGM 播放/暂停 -->
                <button class="extras-btn" @click="toggleMusic" :title="isPlaying ? '暂停背景音乐' : '播放背景音乐'">
                    <span class="icon">🎼</span>
                    <span class="label">BGM {{ isPlaying ? '暂停' : '播放' }}</span>
                </button>
                <!-- 黑夜/白天模式切换 -->
                <button class="extras-btn" @click="toggleTheme" :title="theme === 'light' ? '切换到黑夜模式' : '切换到白天模式'">
                    <span class="icon">{{ theme === 'light' ? '🌙' : '☀️' }}</span>
                    <span class="label">{{ theme === 'light' ? '黑夜模式' : '白天模式' }}</span>
                </button>
                <!-- 移动/电脑版切换 -->
                <button class="extras-btn" @click="toggleViewMode" :title="isMobileView ? '切换到电脑版' : '切换到移动版'">
                    <span class="icon">{{ isMobileView ? '💻' : '📱' }}</span>
                    <span class="label">{{ isMobileView ? '电脑版' : '移动版' }}</span>
                </button>
            </div>
        </transition>

        <!-- 底部：退出登录，仅在未折叠时显示 -->
        <transition name="slide-fade">
            <div v-if="!collapsed" class="sidebar-footer">
                <button class="logout-btn" @click="handleLogout" title="退出登录">
                    <span class="icon"></span>
                    <span class="label">退出登录</span>
                </button>
            </div>
        </transition>

        <!-- 隐藏的 BGM 播放器 -->
        <audio ref="bgMusicRef" autoplay loop preload="auto" style="display: none;">
            <source src="/src/assets/audio/海愿 - 塞壬唱片-MSR、Eagle Wei.mp3" type="audio/mpeg" />
            您的浏览器不支持音频播放。
        </audio>
    </aside>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

// 侧边栏是否显示
const showSidebar = ref(false)
// 侧边栏折叠状态
const collapsed = ref(false)

// 判断当前路由是否激活
const isActive = (path) => route.path === path

// 视图模式：移动或电脑
const isMobileView = ref(window.innerWidth < 768)
function toggleViewMode() {
    isMobileView.value = !isMobileView.value
    if (isMobileView.value) {
        document.body.classList.add('mobile-layout')
    } else {
        document.body.classList.remove('mobile-layout')
    }
}
window.addEventListener('resize', () => {
    isMobileView.value = window.innerWidth < 768
})

// BGM 播放控制
const bgMusicRef = ref(null)
const isPlaying = ref(true)
function toggleMusic() {
    if (!bgMusicRef.value) return
    if (isPlaying.value) {
        bgMusicRef.value.pause()
    } else {
        bgMusicRef.value.play().catch(() => {
            isPlaying.value = false
        })
    }
    isPlaying.value = !isPlaying.value
}
onMounted(() => {
    if (bgMusicRef.value) {
        bgMusicRef.value
            .play()
            .then(() => {
                isPlaying.value = true
            })
            .catch(() => {
                isPlaying.value = false
            })
    }
})

// 主题切换
const theme = ref('light')
function toggleTheme() {
    theme.value = theme.value === 'light' ? 'dark' : 'light'
    document.documentElement.setAttribute('data-theme', theme.value)
}

// “设置”面板显示/隐藏
const showExtras = ref(false)
function toggleExtras() {
    if (collapsed.value) {
        collapsed.value = false
    }
    showExtras.value = !showExtras.value
}

// 折叠/展开侧边栏
function toggleCollapse() {
    collapsed.value = !collapsed.value
    if (collapsed.value) {
        showExtras.value = false
    }
}

// 退出登录
function handleLogout() {
    localStorage.removeItem('authToken')
    localStorage.removeItem('username')
    localStorage.removeItem('role')
    router.push('/login')
}
</script>

<style scoped>
/* 打开按钮 */
.open-btn {
    position: fixed;
    top: 16px;
    left: 16px;
    width: 36px;
    height: 36px;
    background-color: rgba(30, 30, 30, 0.7);
    color: #ffffff;
    font-size: 20px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    z-index: 1000;
    transition: background-color 0.2s ease;
}

.open-btn:hover {
    background-color: rgba(30, 30, 30, 0.9);
}




/* 头部：上下两行布局 */
.sidebar-header {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    padding: 12px 16px;
    border-bottom: 1px solid rgba(224, 230, 237, 0.08);
}

.close-btn {
    position: absolute;
    top: 10px;
    right: 10px; /* 右上角定位 */
    background: none;
    border: none;
    color: #e0e6ed;
    font-size: 18px;
    cursor: pointer;
    margin-bottom: 8px;
    padding: 6px;
    transition: background-color 0.2s ease;
}

.close-btn:hover {
    background-color: rgba(224, 230, 237, 0.12);
    border-radius: 4px;
}

.collapse-btn {
    background: none;
    border: none;
    color: #e0e6ed;
    cursor: pointer;
    font-size: 18px;
    margin-bottom: 8px;
    padding: 6px;
    transition: background-color 0.2s ease;
}

.collapse-btn:hover {
    background-color: rgba(224, 230, 237, 0.12);
    border-radius: 6px;
}

/* 标题 */
.header-title {
    margin: 0;
    font-size: 20px;
    font-weight: 600;
    background: linear-gradient(120deg, #c035f7, #22a5fd); /* 渐变从左到右，颜色可调整 */
  -webkit-background-clip: text; /* 让背景渐变只应用于文本 */
  background-clip: text; /* 标准属性，兼容性更好 */
  color: transparent; /* 使文本颜色透明，显示渐变背景 */
  display: inline-block; /* 确保渐变正确应用 */
}

/* 导航菜单 */
.sidebar-nav {
    flex: 1;
    padding-top: 16px;
}

.sidebar-nav ul {
    list-style: none;
    padding: 0;
    margin: 0;
}

.sidebar-nav li {
    margin-bottom: 6px;
}

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
    background-color: #31557e;
    color: #ffffff;
}

.nav-link.active:hover {
    background-color: #071b32;
    transform: none;
    box-shadow: none;
}

/* 高亮进度条 */
.active-bar {
    position: absolute;
    left: 0;
    top: 4px;
    bottom: 4px;
    width: 4px;
    background-color: #1abc9c;
    border-radius: 4px 0 0 4px;
}

/* 图标、标签 */
.icon {
    font-size: 20px;
    width: 24px;
    text-align: center;
    flex-shrink: 0;
}

.label {
    margin-left: 14px;
    font-size: 15px;
    white-space: nowrap;
    flex-shrink: 0;
    color: inherit;
    transition: opacity 0.3s ease, transform 0.3s ease;
}

.sidebar.collapsed .label {
    opacity: 0;
    transform: translateX(-8px);
}

/* 设置按钮 */
.settings-toggle-container {
    display: flex;
    justify-content: flex-end;
    padding: 8px 16px;
}

.settings-toggle-btn {
    background: none;
    border: none;
    font-size: 18px;
    color: #e0e6ed;
    cursor: pointer;
    transition: background-color 0.2s ease, transform 0.2s ease;
    padding: 6px;
    border-radius: 6px;
}

.settings-toggle-btn:hover {
    background-color: rgba(224, 230, 237, 0.12);
    transform: scale(1.1);
}

/* 额外控制面板 */
.extras-panel {
    display: flex;
    flex-direction: column;
    padding: 8px 16px;
    background-color: rgba(0, 0, 0, 0.05);
    border-bottom: 1px solid rgba(224, 230, 237, 0.08);
}

.extras-btn {
    display: flex;
    align-items: center;
    background: none;
    border: none;
    color: #cfd8e3;
    font-size: 15px;
    padding: 8px 0;
    cursor: pointer;
    transition: background-color 0.2s ease, transform 0.2s ease;
}

.extras-btn:hover {
    background-color: rgba(255, 255, 255, 0.08);
    transform: translateX(4px);
}

.extras-btn .icon {
    margin-right: 10px;
    font-size: 18px;
}

.extras-panel .label {
    font-size: 15px;
    color: inherit;
}

/* 过渡：从上往下展开并渐显 */
.slide-fade-enter-active {
    transition: all 0.25s ease;
}

.slide-fade-leave-active {
    transition: all 0.2s ease;
}

.slide-fade-enter-from {
    opacity: 0;
    transform: translateY(-10px);
}

.slide-fade-enter-to {
    opacity: 1;
    transform: translateY(0);
}

.slide-fade-leave-from {
    opacity: 1;
    transform: translateY(0);
}

.slide-fade-leave-to {
    opacity: 0;
    transform: translateY(-10px);
}

/* 底部退出按钮 */
.sidebar-footer {
    padding: 18px 16px;
    border-top: 1px solid rgba(224, 230, 237, 0.08);
}

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





/* 基础侧边栏样式 */
.sidebar {
    display: flex;
    flex-direction: column;
    color: #e0e6ed;
    transition: width 0.3s ease, background 0.3s ease;
    width: 220px;
    min-height: 100vh;
    box-shadow: 2px 0 12px rgba(0, 0, 0, 0.25);
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    z-index: 999;
        position: fixed;
    top: 0;
    left: 0;
    height: 100vh;
    background: linear-gradient(190deg, #061e46, #0380b5ce);

}

/* 折叠状态 */
.sidebar.collapsed {
    width: 64px !important;
}


/* 桌面端样式 */
.sidebar:not(.mobile-view) {
    width: 220px;
    transition: width 0.3s ease;
}

.sidebar:not(.mobile-view).collapsed {
    width: 64px;
}

/* 移动端样式 */
.sidebar.mobile-view {
    width: 220px;
    transform: translateX(-100%);
    transition: transform 0.4s cubic-bezier(0.68, -0.55, 0.27, 1.55);
}

.sidebar.mobile-view:not(.collapsed) {
    transform: translateX(0);
}

/* 遮罩层样式 */
.sidebar-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.5);
    z-index: 999;
    opacity: 0;
    transition: opacity 0.3s ease;
}

.sidebar-overlay.active {
    opacity: 1;
}

/* 主内容区域调整 */
.main-content {
    transition: margin-left 0.3s ease;
}

.sidebar-open .main-content {
    margin-left: 220px;
}

.sidebar-collapsed .main-content {
    margin-left: 64px;
}




</style>
