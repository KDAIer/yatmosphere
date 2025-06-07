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
        <input v-model="newTodoText" placeholder="输入待办事项" class="todo-input-text" />
        <input type="date" v-model="newTodoDate" class="todo-input-date" />
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
import { useRouter } from 'vue-router'

// 从 Profile.js 导入家庭成员相关逻辑
import {
  familyMembers,
  loadFamilyMembersFromApi,
  mergeTodosFromStorage
} from './Profile.js'

// 从 DashboardLogic.js 导入全局状态与方法
import {
  username,
  roleName,
  inviteCode,
  toggleHomeStatus,
  environmentData, 
  useTimeUpdater
} from './DashboardLogic.js'

function formatLocalDate(date) {
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

useTimeUpdater()

// 默认头像占位
const defaultAvatar = '/src/assets/images/user.png'

// 本地用户数据：avatar
const user = ref({ avatar: '' })

// 文件输入框引用
const avatarInput = ref(null)
const AVATAR_KEY_PREFIX = 'dashboard_user_avatar_'

const router = useRouter()

// 头像上传 & 本地存储
const onClickAvatar = () => avatarInput.value?.click()
const onFileChange = e => {
  const file = e.target.files[0]
  if (!file) return
  if (!file.type.match(/^image\/(png|jpeg)$/) || file.size > 2 * 1024 * 1024) {
    alert('请上传 JPG/PNG 且小于 2MB 的图片')
    return
  }
  const reader = new FileReader()
  reader.onload = ev => {
    user.value.avatar = ev.target.result
    localStorage.setItem(AVATAR_KEY_PREFIX + username.value, ev.target.result)
  }
  reader.readAsDataURL(file)
}

// 监听 username 变化，加载头像 & 邀请码
watch(username, newName => {
  if (!newName) return (user.value.avatar = '')
  const saved = localStorage.getItem(AVATAR_KEY_PREFIX + newName)
  user.value.avatar = saved || ''
}, { immediate: true })

// 登出 & 复制邀请码
const doLogout = () => {
  localStorage.removeItem('authToken')
  localStorage.removeItem('username')
  localStorage.removeItem('role')
  router.push('/login')
}
const copyInviteCode = () => {
  if (!inviteCode.value) return
  navigator.clipboard.writeText(inviteCode.value)
    .then(() => alert('邀请码已复制到剪贴板'))
    .catch(() => {})
}

// 页面挂载：本地缓存优先，否则调用 API
onMounted(async () => {
  username.value = localStorage.getItem('username') || ''
  roleName.value = localStorage.getItem('role') === 'admin' ? '管理员' : '普通用户'
  inviteCode.value = localStorage.getItem('inviteCode') || ''

  // —— 强制走 API 拿最新家庭成员 —— 
  await loadFamilyMembersFromApi()

  // —— 合并本地待办，不覆盖 todos —— 
  mergeTodosFromStorage()
})

// 同步在家人数
watchEffect(() => {
  const count = familyMembers.value.filter(m => m.isHome).length
  environmentData.people.value = `${count}人`
})

// —— 新增：初始化完成前的标志 —— 
const isInitializing = ref(true)

// —— 替换掉原有的深度 watch(familyMembers) 用下面的 —— 
watch(familyMembers, newVal => {
  // 只有在初始化阶段结束后，才把更新写入 localStorage
  if (!isInitializing.value) {
    localStorage.setItem(
      'familyMembers',
      JSON.stringify(familyMembers.value)
    )
  }
}, { deep: true })


const peopleCount = computed(() => environmentData.people.value)

// —— 待办事项相关 ——

// 当前用户对象
const currentMember = computed(() =>
  familyMembers.value.find(m => m.name === username.value)
)

const newTodoText = ref('')
const newTodoDate = ref('')

const addTodo = () => {
  if (!newTodoText.value || !newTodoDate.value) {
    return alert('请输入待办事项内容并选择日期')
  }
  if (!currentMember.value) {
    return alert('未找到当前用户，请确认用户名')
  }
  if (!Array.isArray(currentMember.value.todos)) {
    currentMember.value.todos = []
  }
  currentMember.value.todos = currentMember.value.todos.map(item =>
    typeof item === 'string'
      ? { text: item, date: formatLocalDate(new Date()) }
      : item
  )
  currentMember.value.todos.push({
    text: newTodoText.value,
    date: newTodoDate.value
  })
  newTodoText.value = ''
  newTodoDate.value = ''
  localStorage.setItem('familyMembers', JSON.stringify(familyMembers.value))
}

const todaysTodosCount = computed(() => {
  if (!currentMember.value?.todos) return 0
  const today = formatLocalDate(new Date())
  return currentMember.value.todos.filter(t => t.date === today).length
})

// —— 一周待办日历 —— 
function getMonday(d) {
  const date = new Date(d)
  const day = date.getDay()
  const diff = (day === 0 ? -6 : 1) - day
  date.setDate(date.getDate() + diff)
  return new Date(date.getFullYear(), date.getMonth(), date.getDate())
}
const weekStart = ref(getMonday(new Date()))
const weekDates = computed(() =>
  Array.from({ length: 7 }, (_, i) => {
    const dt = new Date(weekStart.value)
    dt.setDate(dt.getDate() + i)
    return dt
  })
)
const dayNames = ['一','二','三','四','五','六','日']
const isToday = date => {
  const t = new Date()
  return (
    date.getFullYear() === t.getFullYear() &&
    date.getMonth() === t.getMonth() &&
    date.getDate() === t.getDate()
  )
}
function todosForDate(date) {
  const ds = formatLocalDate(date)
  const list = []
  familyMembers.value.forEach(m => {
    m.todos?.forEach(t => {
      if (t.date === ds) list.push({ text: `${m.name}: ${t.text}` })
    })
  })
  return list
}
const prevWeek = () => weekStart.value.setDate(weekStart.value.getDate() - 7)
const nextWeek = () => weekStart.value.setDate(weekStart.value.getDate() + 7)
const weekRangeText = computed(() => {
  const s = weekStart.value
  const e = new Date(s); e.setDate(e.getDate() + 6)
  const f = n => (n<10?'0'+n:n)
  return `${s.getFullYear()}/${f(s.getMonth()+1)}/${f(s.getDate())}` +
         ` - ${e.getFullYear()}/${f(e.getMonth()+1)}/${f(e.getDate())}`
})
</script>

<style scoped>
@import './Profile.css'
</style>
