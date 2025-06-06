<!-- src/views/Profile.vue -->

<template>
    <div class="profile-page">
        <!-- 个人信息卡片 -->
        <section class="profile-container card">
            <div class="profile-header">
                <!-- 大头像，可点击上传新头像 -->
                <div class="avatar-large" @click="onClickAvatar" title="点击上传头像">
                    <img :src="user.avatar || defaultAvatar" alt="用户头像" class="avatar-large-img" />
                </div>
                <div class="profile-info">
                    <h1>{{ username }}</h1>
                    <p><strong>角色：</strong> {{ roleName }}</p>
                    <p class="invite-code">
                        <strong>家庭邀请码：</strong> {{ inviteCode }}
                        <button class="copy-btn" @click="copyInviteCode" title="复制邀请码">复制</button>
                    </p>
                    <button class="logout-btn" @click="doLogout" title="退出登录">退出登录</button>
                </div>
            </div>
        </section>

        <!-- 家庭成员列表 -->
        <section class="member-list card">
            <h2>家庭成员</h2>
            <div class="member-table-container">
                <div class="member-table">
                    <div class="table-body">
                        <div v-for="member in familyMembers" :key="member.id" class="member-row"
                            :class="{ admin: member.isAdmin }">
                            <div class="member-info">
                                <span class="member-name">{{ member.name }}</span>
                                <span v-if="member.isAdmin" class="admin-badge">管理员</span>
                                <span class="home-badge" :class="member.isHome ? 'home' : 'away'"
                                    @click="toggleHomeStatus(member)" title="点击切换在家/离家状态">
                                    {{ member.isHome ? '在家' : '不在家' }}
                                </span>
                            </div>
                            <div class="member-todos" v-if="member.todos && member.todos.length">
                                <div v-for="(todo, idx) in member.todos" :key="idx" class="todo-item">
                                    {{ todo }}
                                </div>
                            </div>
                            <div v-else class="no-todos">暂无待办</div>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <!-- 隐藏的文件输入框，用于选择头像 -->
        <input type="file" ref="avatarInput" accept="image/png, image/jpeg" style="display: none"
            @change="onFileChange" />
    </div>
</template>

<script setup>
import { ref, onMounted, watch, watchEffect } from 'vue'
import axios from 'axios'

// 从 Profile.js 导入家庭成员相关逻辑
import {
    familyMembers,
    loadFamilyMembersFromApi
} from './Profile.js'

// 从 DashboardLogic.js 导入全局状态与方法
import {
    username,
    roleName,
    inviteCode,
    toggleHomeStatus,
    environmentData
} from './DashboardLogic.js'

import { useRouter } from 'vue-router'

// 默认头像占位
const defaultAvatar = '/src/assets/images/user.png'

// 本地用户数据：avatar、username、inviteCode，来自 localStorage 或默认值
const user = ref({
    avatar: '',
    username: '',
    inviteCode: ''
})

// 文件输入框引用，用来触发系统文件选择
const avatarInput = ref(null)
const AVATAR_KEY_PREFIX = 'dashboard_user_avatar_' // localStorage 键名前缀

const router = useRouter()

// 点击大头像触发文件上传
const onClickAvatar = () => {
    if (avatarInput.value) {
        avatarInput.value.click()
    }
}

// 选择完文件后读取并存储到 localStorage
const onFileChange = (e) => {
    const file = e.target.files[0]
    if (!file) return

    // 仅限 JPG/PNG 且小于 2MB
    if (!file.type.match(/^image\/(png|jpeg)$/) || file.size > 2 * 1024 * 1024) {
        alert('请上传 JPG/PNG 且小于 2MB 的图片')
        return
    }

    const reader = new FileReader()
    reader.onload = (ev) => {
        const base64Data = ev.target.result
        user.value.avatar = base64Data
        // 将头像存到 localStorage，以 “键前缀 + 用户名” 作为键
        const key = AVATAR_KEY_PREFIX + username.value
        localStorage.setItem(key, base64Data)
    }
    reader.readAsDataURL(file)
}

// 监听 username 变化，在登录/登出或页面刷新时从 localStorage 读取头像
watch(
    username,
    (newUsername) => {
        if (!newUsername) {
            user.value.avatar = ''
            user.value.username = ''
            user.value.inviteCode = ''
            return
        }
        const key = AVATAR_KEY_PREFIX + newUsername
        const saved = localStorage.getItem(key)
        if (saved) {
            user.value.avatar = saved
        } else {
            user.value.avatar = ''
        }
        user.value.username = newUsername
        user.value.inviteCode = inviteCode.value || ''
    },
    { immediate: true }
)

// 登出逻辑，清空 token 并跳转登录
const doLogout = () => {
    localStorage.removeItem('authToken')
    localStorage.removeItem('username')
    localStorage.removeItem('role')
    router.push('/login')
}

// 复制邀请码到剪贴板
const copyInviteCode = () => {
    if (!user.value.inviteCode) return
    navigator.clipboard.writeText(user.value.inviteCode)
        .then(() => {
            alert('邀请码已复制到剪贴板')
        })
        .catch(err => {
            console.error('复制失败:', err)
        })
}

// 页面挂载时，初始化数据
onMounted(async () => {
    // 从 localStorage 提取用户名和角色
    const storedUser = localStorage.getItem('username') || ''
    const storedRole = localStorage.getItem('role')
    if (storedUser) {
        // username.value 已被 watch 监听，触发头像加载
    }
    // inviteCode 在 DashboardLogic.js 里已初始化为 localStorage 中的值

    // 如果 familyMembers 为空，则调用 API 加载
    if (familyMembers.value.length === 0) {
        await loadFamilyMembersFromApi()
    }
})

// 实时同步“在家人数”到环境数据
watchEffect(() => {
    const count = familyMembers.value.filter(m => m.isHome).length
    environmentData.people.value = `${count}人`
})

// 本地存储 familyMembers
watch(familyMembers, (newVal) => {
    localStorage.setItem('familyMembers', JSON.stringify(newVal))
}, { deep: true })
</script>

<style scoped>
/* 整页背景与间距 */
.profile-page {
    display: flex;
    flex-direction: column;
    gap: 24px;
    padding: 24px;
    background-color: #f5f7fa;
}

/* 通用卡片样式 */
.card {
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
    padding: 24px;
}

/* 个人信息卡片 */
.profile-container {
    display: flex;
    flex-direction: column;
}

.profile-header {
    display: flex;
    align-items: center;
    gap: 24px;
}

.avatar-large {
    width: 96px;
    height: 96px;
    border-radius: 50%;
    overflow: hidden;
    cursor: pointer;
    border: 2px solid #d9d9d9;
    transition: border-color 0.2s ease;
}

.avatar-large:hover {
    border-color: #409eff;
}

.avatar-large-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

/* 用户信息详情 */
.profile-info {
    flex: 1;
}

.profile-info h1 {
    margin: 0;
    font-size: 24px;
    color: #333;
}

.profile-info p {
    margin: 8px 0;
    color: #666;
}

.invite-code {
    display: flex;
    align-items: center;
    gap: 12px;
}

.copy-btn {
    background-color: #409eff;
    color: #fff;
    border: none;
    padding: 4px 10px;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.2s ease;
}

.copy-btn:hover {
    background-color: #2879c7;
}

.logout-btn {
    margin-top: 12px;
    background-color: #ff4d4f;
    color: #fff;
    border: none;
    padding: 6px 14px;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.2s ease;
}

.logout-btn:hover {
    background-color: #d9363e;
}

/* 家庭成员列表 */
.member-list h2 {
    margin-top: 0;
    color: #333;
    font-size: 20px;
    margin-bottom: 16px;
}

.member-table-container {
    overflow-x: auto;
}

.member-table {
    width: 100%;
}

.table-body {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

/* 单行成员 */
.member-row {
    padding: 12px 16px;
    border: 1px solid #e0e0e0;
    border-radius: 6px;
    display: flex;
    flex-direction: column;
    gap: 8px;
    background-color: #fafafa;
}

.member-row.admin {
    border-color: #409eff;
}

.member-info {
    display: flex;
    align-items: center;
    gap: 12px;
    flex-wrap: wrap;
}

.member-name {
    font-weight: 500;
    color: #333;
}

.admin-badge {
    background-color: #409eff;
    color: #fff;
    padding: 2px 6px;
    border-radius: 4px;
    font-size: 12px;
}

.home-badge {
    font-size: 12px;
    padding: 4px 8px;
    border-radius: 6px;
    cursor: pointer;
    transition: background-color 0.2s ease;
}

.home-badge.home {
    background-color: #d1f0d1;
    color: #2e7d32;
}

.home-badge.away {
    background-color: #ffe0e0;
    color: #c62828;
}

.home-badge:hover {
    filter: brightness(95%);
}

/* 待办事项 */
.member-todos {
    margin-left: 24px;
}

.todo-item {
    background-color: #fff;
    padding: 6px 10px;
    border-radius: 4px;
    font-size: 14px;
    color: #444;
    border: 1px solid #e0e0e0;
}

.no-todos {
    margin-left: 24px;
    font-size: 14px;
    color: #999;
}

/* 淡入淡出动画 */
.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
    opacity: 0;
}

.fade-enter-to,
.fade-leave-from {
    opacity: 1;
}
</style>
