<template>
  <div class="auth-container">
    <div class="auth-form">
      <h2>智能家居系统</h2>
      <div class="form-tabs">
        <button class="tab-btn" :class="{ active: isLogin }" @click="switchForm(true)">登录</button>
        <button class="tab-btn" :class="{ active: !isLogin }" @click="switchForm(false)">
          注册
        </button>
      </div>

      <form @submit.prevent="isLogin ? handleLogin() : handleRegister()">
        <!-- 用户名 -->
        <div class="form-group">
          <label>用户名</label>
          <input type="text" v-model="username" placeholder="请输入用户名" required />
        </div>

        <!-- 密码 -->
        <div class="form-group">
          <label>密码</label>
          <input
            type="password"
            v-model="password"
            placeholder="请输入密码"
            required
            minlength="3"
          />
        </div>

        <!-- 注册额外字段 -->
        <template v-if="!isLogin">
          <!-- 用户类型 -->
          <div class="form-group">
            <label>用户类型</label>
            <div class="radio-group">
              <label>
                <input
                  type="radio"
                  v-model="userType"
                  value="admin"
                  @change="handleUserTypeChange"
                />
                <span>家庭管理员</span>
              </label>
              <label>
                <input
                  type="radio"
                  v-model="userType"
                  value="member"
                  @change="handleUserTypeChange"
                />
                <span>家庭成员</span>
              </label>
            </div>
          </div>

          <!-- 邀请码 (仅家庭成员可见) -->
          <div class="form-group" v-if="userType === 'member'">
            <label>家庭邀请码</label>
            <input
              type="text"
              v-model="inviteCode"
              placeholder="请输入家庭管理员提供的邀请码"
              required
            />
          </div>
        </template>

        <!-- 错误提示 -->
        <div v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>

        <!-- 成功提示 (注册成功时显示)-->
        <div v-if="successMessage" class="success-message">
          {{ successMessage }}
          <span v-if="inviteCodeGenerated" class="invite-code">
            您的家庭邀请码: <strong>{{ inviteCodeGenerated }}</strong>
          </span>
          <div class="action-buttons">
            <button type="button" @click="copyInviteCode" class="secondary-btn">
              {{ isCopied ? '已复制!' : '复制邀请码' }}
            </button>
            <button type="button" @click="goToLogin" class="primary-btn">前往登录</button>
          </div>
        </div> 

        <!-- 提交按钮 -->
        <button type="submit" :disabled="isLoading">
          {{ isLoading ? '处理中...' : isLogin ? '登录' : '注册' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const isLogin = ref(true)
const isCopied = ref(false)

// 表单数据
const username = ref('')
const password = ref('')
const userType = ref('admin')
const inviteCode = ref('')
const inviteCodeGenerated = ref('')

// 状态
const errorMessage = ref('')
const successMessage = ref('')
const isLoading = ref(false)

// 切换登录/注册表单
const switchForm = (login) => {
  isLogin.value = login
  resetMessages()
}

// 重置提示信息
const resetMessages = () => {
  errorMessage.value = ''
  successMessage.value = ''
  inviteCodeGenerated.value = ''
}

// 用户类型变化处理
const handleUserTypeChange = () => {
  inviteCode.value = ''
}

// 生成随机邀请码
const generateInviteCode = () => {
  return 'FAM-' + Math.random().toString(36).substring(2, 8).toUpperCase()
}

const handleLogin = async () => {
  if (!username.value || !password.value) {
    errorMessage.value = '请填写所有字段'
    return
  }

try {
  isLoading.value = true
  errorMessage.value = ''

  const response = await axios.post('/auth/login', {
    account: username.value,
    password: password.value
  }, {
    headers: { 'Content-Type': 'application/json' }
  })

  console.log('[Debug] 登录响应:', response.data)

  if (response.data?.data && response.data.data.token) {
    localStorage.setItem('authToken', response.data.data.token)
    localStorage.setItem('userType', response.data.data.userType || 'member')
    localStorage.setItem('username', username.value)
    successMessage.value = '登录成功！'
    router.push('/dashboard')
  } else {
    // 后端返回了错误，比如 data 是 null
    errorMessage.value = response.data.msg || '登录失败，请检查凭证'
  }
} catch (error) {
  console.error('[Debug] Axios Error:', error)
  if (error.response) {
    console.log('[Debug] 响应内容:', error.response.data)
    errorMessage.value = error.response.data?.msg || '登录失败，请检查凭证'
  } else if (error.request) {
    errorMessage.value = '服务器无响应，请检查网络连接'
  } else {
    errorMessage.value = '请求发送失败：' + error.message
  }
}

}


const handleRegister = async () => {
  if (!username.value || !password.value) {
    errorMessage.value = '请填写所有字段'
    return
  }

  if (userType.value === 'member' && !inviteCode.value) {
    errorMessage.value = '请输入家庭邀请码'
    return
  }

  isLoading.value = true
  errorMessage.value = ''
  successMessage.value = ''

  try {
    const payload = {
      account: username.value,
      password: password.value,
      userType: userType.value,
      inviteCode: userType.value === 'admin' ? generateInviteCode() : inviteCode.value
    }

    const response = await axios.post('/auth/register', payload, {
      headers: { 'Content-Type': 'application/json' }
    })
    console.log('[Debug] 注册响应:', response.data)
    if (response.data && response.data.data.success) {
      localStorage.setItem('authToken', response.data.data.token)
      localStorage.setItem('userType', userType.value)
      localStorage.setItem('username', username.value)
      
      if (userType.value === 'admin') {
        inviteCodeGenerated.value = payload.inviteCode
        successMessage.value = '注册成功！请妥善保存您的家庭邀请码：' + payload.inviteCode
        // 不自动跳转，等待管理员复制邀请码
            // 延迟 3 秒后自动跳转登录
        setTimeout(() => {
          isLogin.value = true
          resetMessages()
        }, 3000)

      } else {
        successMessage.value = '注册成功！您已加入家庭，3s后自动跳转登录'
        // 家庭成员注册后自动跳转登录
        setTimeout(() => {
          isLogin.value = true
          resetMessages()
        }, 3000)
      }
    } else {
      // 注册失败，显示后端msg或者默认信息
      errorMessage.value = response.data.data.message || '注册失败'
    }
  } catch (err) {
    if (err.response && err.response.data) {
      errorMessage.value = err.response.data.msg || '注册失败'
    } else if (err.request) {
      errorMessage.value = '服务器无响应，请检查网络连接'
    } else {
      errorMessage.value = '请求发送失败：' + err.message
    }
  } finally {
    isLoading.value = false
  }
}


//   setTimeout(() => {
//     try {
//       if (userType.value === 'admin') {
//         inviteCodeGenerated.value = generateInviteCode()
//         successMessage.value = '注册成功！请妥善保存您的家庭邀请码'
//         // 移除自动跳转逻辑
//       } else {
//         if (inviteCode.value !== 'DEMO-CODE') {
//           throw new Error('无效的家庭邀请码')
//         }
//         successMessage.value = '注册成功！您已加入家庭'
//         // 家庭成员注册仍然自动跳转
//         setTimeout(() => {
//           isLogin.value = true
//           resetMessages()
//         }, 3000)
//       }
//     } catch (err) {
//       errorMessage.value = err.message
//     } finally {
//       isLoading.value = false
//     }
//   }, 1500)
// }

// 新增方法：复制邀请码
const copyInviteCode = () => {
  navigator.clipboard.writeText(inviteCodeGenerated.value).then(() => {
    isCopied.value = true
    setTimeout(() => (isCopied.value = false), 2000)
  })
}

// 新增方法：手动跳转登录
const goToLogin = (event) => {
  event?.preventDefault()
  isLogin.value = true
  resetMessages()
}
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: #f0f2f5;
  background-image: linear-gradient(120deg, #a1c4fd 0%, #c2e9fb 100%);
}

.auth-form {
  background: white;
  padding: 2.5rem;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  width: 400px;
  animation: fadeIn 0.5s ease;
}

/* 新增样式 */
.action-buttons {
  display: flex;
  gap: 1rem;
  margin-top: 1.5rem;
}

.primary-btn {
  background: #3182ce;
  color: white;
  padding: 0.7rem 1.5rem;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  flex: 1;
}

.secondary-btn {
  background: white;
  color: #3182ce;
  padding: 0.7rem 1.5rem;
  border-radius: 6px;
  border: 1px solid #3182ce;
  cursor: pointer;
  flex: 1;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

h2 {
  text-align: center;
  color: #2c3e50;
  margin-bottom: 1.5rem;
  font-size: 1.8rem;
}

.form-tabs {
  display: flex;
  margin-bottom: 1.5rem;
  border-bottom: 1px solid #e2e8f0;
}

.tab-btn {
  flex: 1;
  padding: 0.8rem;
  background: none;
  border: none;
  font-size: 1rem;
  cursor: pointer;
  color: #718096;
  position: relative;
}

.tab-btn.active {
  color: #3182ce;
  font-weight: 500;
}

.tab-btn.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  right: 0;
  height: 2px;
  background: #3182ce;
}

.form-group {
  margin-bottom: 1.5rem;
}

label {
  display: block;
  margin-bottom: 0.6rem;
  color: #4a5568;
  font-size: 0.95rem;
}

input {
  width: 90%;
  padding: 1rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 1rem;
  transition: border-color 0.2s;
}

input:focus {
  border-color: #3182ce;
  outline: none;
}

.radio-group {
  display: flex;
  gap: 1.5rem;
  margin-top: 0.5rem;
}

.radio-group label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
  margin-bottom: 0;
}

.radio-group input[type='radio'] {
  width: auto;
  margin: 0;
}

button[type='submit'] {
  width: 100%;
  padding: 1rem;
  background: #3182ce;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 1.05rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  margin-top: 0.5rem;
}

button[type='submit']:hover:not(:disabled) {
  background: #2c6cb0;
  transform: translateY(-1px);
}

button[type='submit']:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.error-message {
  color: #dc2626;
  margin: 1rem 0;
  padding: 0.8rem;
  background: #fee2e2;
  border-radius: 6px;
  text-align: center;
  font-size: 0.9rem;
}

.success-message {
  color: #16a34a;
  margin: 1rem 0;
  padding: 0.8rem;
  background: #dcfce7;
  border-radius: 6px;
  text-align: center;
  font-size: 0.9rem;
  padding: 1.5rem;
}

.invite-code {
  display: block;
  margin: 1rem 0;
  font-size: 1.1rem;
  padding: 0.8rem;
  background: #f0f7ff;
  border-radius: 6px;
  color: #1e40af;
}

.invite-code strong {
  font-weight: 600;
  letter-spacing: 1px;
}
</style>