<!-- src/views/Profile.vue -->

<template>
    <div class="profile-container">
        <h1>个人信息</h1>
        <div class="profile-card">
            <p><strong>用户名：</strong> {{ username }}</p>
            <p><strong>角色：</strong> {{ roleName }}</p>
            <p><strong>注册时间：</strong> {{ userInfo.registeredAt }}</p>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const userInfo = ref({
    username: '',
    role: '',
    registeredAt: ''
})

onMounted(async () => {
    try {
        const token = localStorage.getItem('authToken')
        if (!token) return

        // 调用后端接口 /api/user/profile 获取当前用户信息（请根据后端实际接口路径修改）
        const response = await axios.get('/api/user/profile', {
            headers: { Authorization: `Bearer ${token}` }
        })
        if (response.data && response.data.data) {
            userInfo.value.username = response.data.data.username
            userInfo.value.role = response.data.data.role
            // 假设后端返回 registeredAt 字段
            userInfo.value.registeredAt = response.data.data.registeredAt
        }
    } catch (err) {
        console.error('获取用户信息失败', err)
    }
})
</script>

<style scoped>
.profile-container {
    background-color: #fff;
    padding: 24px;
    border-radius: 8px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.profile-container h1 {
    margin-bottom: 16px;
}

.profile-card {
    line-height: 1.8;
}
</style>
