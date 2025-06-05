<!-- src/views/Profile.vue -->

<template>
    <div class="profile-container">
        <h1>个人信息</h1>
        <div class="profile-card">
            <p><strong>用户名：</strong> {{ username }}</p>
            <p><strong>角色：</strong> {{ roleName }}</p>
            <p><strong>家庭邀请码：</strong> {{ inviteCode }}</p>
        </div>
    </div>
        <!-- 家庭成员 -->
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
                <span class="home-badge" :class="member.isHome ? 'home' : 'away'" @click="toggleHomeStatus(member)">
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
</template>



<script setup>
import { ref, onMounted, watch, watchEffect } from 'vue'
import {familyMembers, loadFamilyMembersFromStorage, loadFamilyMembersFromApi} from './Profile.js'
import axios from 'axios'
import {
  theme,
  toggleTheme,
  inviteCode,
  username,
  roleName,
  environmentData,
  toggleHomeStatus,
} from './DashboardLogic.js'

//   const getCurrentAccount = () => {
//     //const account = localStorage.getItem('account')
//     const account = localStorage.getItem('username')
//     if (!account) {
//       console.warn('未在 localStorage 中找到 account，使用默认值 admin')
//     }
//     return account || 'admin'
//   }

// const loadFamilyMembers = async () => {
//   const account = getCurrentAccount()
//   console.log('加载家庭成员，当前账号:', account)
//   try {
//     const res = await axios.get('/api/family-members', {
//       params: { account }
//     })
//     if (res.data.status === 200) {
//       familyMembers.value = res.data.data.map((member, index) => ({
//         ...member,
//         id: index
//       }))
//       console.log('家庭成员加载成功:', familyMembers.value)
//     } else {
//       console.error('家庭成员请求失败:', res.data.msg)
//     }
//   } catch (err) {
//     console.error('家庭成员请求异常:', err)
//   }
// }

onMounted(async () => {
  username.value = localStorage.getItem('username') || ''
  roleName.value = localStorage.getItem('role') === 'admin' ? '管理员' : '普通用户'
  inviteCode.value = localStorage.getItem('inviteCode') || ''

  if (familyMembers.value.length === 0) {
    await loadFamilyMembersFromApi()
  }
})

// 自动监听在家人数
watchEffect(() => {
  const count = familyMembers.value.filter(m => m.isHome).length
  environmentData.people.value = `${count}人`
})

watch(familyMembers, (newVal) => {
  localStorage.setItem('familyMembers', JSON.stringify(newVal))
}, { deep: true })
 
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
