<!-- src/views/Profile.vue -->
<template>
  <div class="profile-page">
    <!-- 顶部可爱横幅 -->
    <div class="banner">
      🐰 欢迎来到家庭管理中心 🐰
    </div>

    <!-- 个人信息卡片 -->
    <section class="profile-container card">
      <div class="profile-header">
        <!-- 大头像，可点击上传新头像 -->
        <div class="avatar-large" @click="onClickAvatar" title="点击上传头像">
          <img :src="user.avatar || defaultAvatar" alt="用户头像" class="avatar-large-img" />
          <!-- 可爱外框小星星 -->
          <div class="star-decor star-decor-1">✨</div>
          <div class="star-decor star-decor-2">🌟</div>
        </div>
        <div class="profile-info">
          <h1 class="profile-title">Hi, {{ username }}！</h1>
          <p class="role-line">🎀 <strong>角色：</strong> {{ roleName }}</p>
          <p class="invite-code">
            🎁 <strong>家庭邀请码：</strong> {{ inviteCode }}
            <button class="copy-btn" @click="copyInviteCode" title="复制邀请码">复制</button>
          </p>
          <button class="logout-btn" @click="doLogout" title="退出登录">💖 退出登录 💖</button>
        </div>
      </div>
    </section>

    <!-- 个人待办录入 -->
    <section class="todo-summary card">
      <h2 class="section-title">📝 个人待办</h2>
      <div class="todo-input">
        <input
          v-model="newTodoText"
          placeholder="输入待办事项"
          class="todo-input-text"
        />
        <input
          type="date"
          v-model="newTodoDate"
          class="todo-input-date"
        />
        <button class="add-todo-btn" @click="addTodo">添加</button>
      </div>
    </section>

    <!-- 今日待办统计 -->
    <section class="todo-summary card">
      <h2 class="section-title">📊 今日待办统计</h2>
      <div class="todo-counts">
        <div class="todo-item">
          <span class="member-name">你</span>
          <span class="count">{{ todaysTodosCount }}</span> 条待办
        </div>
      </div>
    </section>

    <!-- 家庭在家人数统计 -->
    <section class="env-summary card">
      <h2 class="section-title">🏠 家庭在家人数</h2>
      <div class="env-items">
        <div class="env-item">
          <span class="env-label">在家人数：</span>
          <span class="env-value">{{ peopleCount }}</span>
        </div>
      </div>
    </section>

    <!-- 一周待办日历 -->
    <section class="calendar-section card">
      <h2 class="section-title">📆 一周待办日历</h2>
      <div class="calendar-controls">
        <button class="cal-btn" @click="prevWeek">‹</button>
        <span class="cal-month">{{ weekRangeText }}</span>
        <button class="cal-btn" @click="nextWeek">›</button>
      </div>
      <div class="calendar-grid">
        <div class="day-header" v-for="(day, idx) in dayNames" :key="idx">
          周{{ day }}
        </div>
        <div
          v-for="(date, idx) in weekDates"
          :key="idx"
          class="day-cell"
          :class="{ today: isToday(date) }"
        >
          <div class="date-number">{{ date.getDate() }}</div>
          <div class="day-todos">
            <div
              v-for="(todo, i) in todosForDate(date)"
              :key="i"
              class="todo-item"
            >
              {{ todo.text }}
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 家庭成员列表 -->
    <section class="member-list card">
      <h2 class="member-title">👨‍👩‍👧‍👦 家庭成员</h2>
      <div class="member-table-container">
        <div class="member-table">
          <div class="table-body">
            <div
              v-for="member in familyMembers"
              :key="member.id"
              class="member-row"
              :class="{ admin: member.isAdmin }"
            >
              <div class="member-info">
                <span class="member-name">🌸 {{ member.name }}</span>
                <span v-if="member.isAdmin" class="admin-badge">管理员</span>
                <span
                  class="home-badge"
                  :class="member.isHome ? 'home' : 'away'"
                  @click="toggleHomeStatus(member)"
                  title="点击切换在家/离家状态"
                >
                  {{ member.isHome ? '🏠 在家' : '✈️ 不在家' }}
                </span>
              </div>
              <div class="member-todos" v-if="member.todos && member.todos.length">
                <div
                  v-for="(todo, idx) in member.todos"
                  :key="idx"
                  class="todo-item"
                >
                  📝 {{ typeof todo === 'object' ? todo.text : todo }}
                </div>
              </div>
              <div v-else class="no-todos">暂无待办 💤</div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 隐藏的文件输入框，用于选择头像 -->
    <input
      type="file"
      ref="avatarInput"
      accept="image/png, image/jpeg"
      style="display: none"
      @change="onFileChange"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, watch, watchEffect, computed } from 'vue'
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

// 在监测到 environmentData.people 里更新后，解析出数字

// 实时同步“在家人数”到 environmentData
watchEffect(() => {
  const count = familyMembers.value.filter(m => m.isHome).length
  environmentData.people.value = `${count}人`
})

// 本地存储 familyMembers
watch(familyMembers, (newVal) => {
  localStorage.setItem('familyMembers', JSON.stringify(newVal))
}, { deep: true })

const peopleCount = computed(() => {
  const val = environmentData.people.value // e.g. "3人"
  return val
})


// 本地存储 familyMembers
watch(familyMembers, (newVal) => {
  localStorage.setItem('familyMembers', JSON.stringify(newVal))
}, { deep: true })

// —— 新增：待办事项相关逻辑 ——

// 获取当前用户在 familyMembers 中对应的成员对象
const currentMember = computed(() => {
  return familyMembers.value.find(m => m.name === username.value)
})

// 新待办输入状态
const newTodoText = ref('')
const newTodoDate = ref('')

// 添加待办
const addTodo = () => {
  if (!newTodoText.value || !newTodoDate.value) {
    alert('请输入待办事项内容并选择日期')
    return
  }
  if (!currentMember.value) {
    alert('未能找到当前用户，请先确认用户名是否正确')
    return
  }
  // 初始化 todos 数组（如果之前只有字符串，这里直接转换为对象数组）
  if (!Array.isArray(currentMember.value.todos)) {
    currentMember.value.todos = []
  }
  // 如果原 todos 中是字符串模式，将其保留为今天的任务
  currentMember.value.todos = currentMember.value.todos.map(item => {
    if (typeof item === 'string') {
      // 假设旧格式的未指定日期，默认归为今天
      return { text: item, date: new Date().toISOString().split('T')[0] }
    }
    return item
  })
  // 添加新的待办
  currentMember.value.todos.push({
    text: newTodoText.value,
    date: newTodoDate.value
  })
  // 清空输入
  newTodoText.value = ''
  newTodoDate.value = ''
  // 更新 localStorage
  localStorage.setItem('familyMembers', JSON.stringify(familyMembers.value))
}

// 今日待办数量
const todaysTodosCount = computed(() => {
  if (!currentMember.value || !currentMember.value.todos) return 0
  const todayStr = new Date().toISOString().split('T')[0]
  return currentMember.value.todos.filter(todo => todo.date === todayStr).length
})

// —— 新增：一周待办日历逻辑 ——

// 获取本周周一日期
function getMonday(d) {
  const date = new Date(d)
  const day = date.getDay()
  const diff = (day === 0 ? -6 : 1) - day
  date.setDate(date.getDate() + diff)
  return new Date(date.getFullYear(), date.getMonth(), date.getDate())
}

// 存储当前周一
const weekStart = ref(getMonday(new Date()))

// 生成本周 7 天日期数组
const weekDates = computed(() => {
  const arr = []
  for (let i = 0; i < 7; i++) {
    const tmp = new Date(weekStart.value)
    tmp.setDate(tmp.getDate() + i)
    arr.push(tmp)
  }
  return arr
})

// 星期几中文简写
const dayNames = ['一', '二', '三', '四', '五', '六', '日']

// 判断是否为今天
function isToday(date) {
  const today = new Date()
  return (
    date.getFullYear() === today.getFullYear() &&
    date.getMonth() === today.getMonth() &&
    date.getDate() === today.getDate()
  )
}

// 获取某天所有待办（含成员名称）
function todosForDate(date) {
  const dateStr = date.toISOString().split('T')[0]
  const list = []
  familyMembers.value.forEach(member => {
    if (member.todos && Array.isArray(member.todos)) {
      member.todos.forEach(todo => {
        if (todo.date === dateStr) {
          list.push({ text: `${member.name}: ${todo.text}` })
        }
      })
    }
  })
  return list
}

// 上一周/下一周
const prevWeek = () => {
  const tmp = new Date(weekStart.value)
  tmp.setDate(tmp.getDate() - 7)
  weekStart.value = tmp
}
const nextWeek = () => {
  const tmp = new Date(weekStart.value)
  tmp.setDate(tmp.getDate() + 7)
  weekStart.value = tmp
}

// 周展示范围文字
const weekRangeText = computed(() => {
  const start = weekStart.value
  const end = new Date(start)
  end.setDate(end.getDate() + 6)
  const padZero = (n) => (n < 10 ? '0' + n : n)
  return (
    `${start.getFullYear()}/${padZero(start.getMonth() + 1)}/${padZero(start.getDate())}` +
    ' - ' +
    `${end.getFullYear()}/${padZero(end.getMonth() + 1)}/${padZero(end.getDate())}`
  )
})
</script>

<style scoped>
@import './Profile.css'
</style>
