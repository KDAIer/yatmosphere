<template>
  <div class="auth-container">
    <video 
      autoplay 
      muted 
      playsinline
      class="auth-background" 
      ref="videoRef"
      @ended="handleVideoEnded"
    >
      <!-- 使用已验证可访问的路径 -->
      <source src="/src/assets/videos/background.mp4" type="video/mp4">
      您的浏览器不支持视频播放。
    </video>

    <!-- 添加背景音乐 -->
    <audio autoplay loop volume="0.2" >
      <source src="/src/assets/audio/Ready.mp3" type="audio/mp3">
      您的浏览器不支持音频播放。
    </audio>

    
    <div class="auth-form">
        <div class="title-with-image">
            <img src="../assets/images/logo.png" alt="智能家居系统图片" class="system-image" />
            <h2>智能家居系统</h2>
          </div>
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

        <!-- 成功提示 (注册成功时显示) -->
        <div v-if="successMessage" class="success-message">
          {{ successMessage }}

          <!-- 仅当是管理员时显示邀请码和按钮 -->
          <template v-if="userType === 'admin' && inviteCodeGenerated">
            <span class="invite-code">
              您的家庭邀请码: <strong>{{ inviteCodeGenerated }}</strong>
            </span>

            <div class="action-buttons">
              <button type="button" @click="copyInviteCode" class="secondary-btn">
                {{ isCopied ? '已复制!' : '复制邀请码' }}
              </button>
              <button type="button" @click="goToLogin" class="primary-btn">前往登录</button>
            </div>
          </template>
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
import { ref, onMounted, nextTick } from 'vue'
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

// 视频引用
const videoRef = ref(null)

// 视频淡出状态
const isVideoFading = ref(false)

// // 组件挂载后确保视频播放
// onMounted(async () => {
//   await nextTick()
  
//   if (videoRef.value) {
//     console.log('开始尝试播放视频...')
//     console.log('视频元素存在:', !!videoRef.value)
//     console.log('视频src:', videoRef.value.currentSrc || videoRef.value.src)
    
//     // 确保视频可见
//     videoRef.value.style.display = 'block'
//     videoRef.value.style.visibility = 'visible'
//     videoRef.value.style.opacity = '1'
    
//     // 直接尝试播放
//     try {
//       setTimeout(async () => {
//         try {
//           console.log('视频readyState:', videoRef.value.readyState)
//           console.log('视频paused:', videoRef.value.paused)
          
//           if (videoRef.value.readyState >= 3) {
//             await videoRef.value.play()
//             console.log('视频播放成功')
//             console.log('视频是否在播放:', !videoRef.value.paused)
//           } else {
//             console.log('视频未准备好，等待加载...')
//             videoRef.value.addEventListener('canplay', async () => {
//               try {
//                 await videoRef.value.play()
//                 console.log('延迟播放成功')
//               } catch (e) {
//                 console.log('延迟播放失败:', e)
//               }
//             }, { once: true })
//           }
//         } catch (error) {
//           console.log('视频播放失败:', error)
//           // 添加点击播放的提示
//           const container = document.querySelector('.auth-container')
//           if (container && !container.dataset.clickAdded) {
//             container.dataset.clickAdded = 'true'
//             container.addEventListener('click', async () => {
//               try {
//                 await videoRef.value.play()
//                 console.log('用户交互后播放成功')
//               } catch (e) {
//                 console.log('用户交互后播放仍然失败:', e)
//                 handleVideoPlayError()
//               }
//             }, { once: true })
//             console.log('请点击页面以播放视频')
//           }
//         }
//       }, 200)
//     } catch (error) {
//       console.log('视频加载失败:', error)
//       handleVideoPlayError()
//     }
//   } else {
//     console.log('视频元素未找到')
//   }
// })

// // 视频播放失败处理
// const handleVideoPlayError = () => {
//   console.log('使用备用背景')
//   if (videoRef.value) {
//     videoRef.value.style.display = 'none'
//   }
// }

// 组件挂载后确保视频播放
onMounted(async () => {
  await nextTick()
  if (videoRef.value) {
    console.log('开始尝试播放视频...')
    try {
      if (videoRef.value.readyState >= 3) {
        await videoRef.value.play()
        console.log('视频播放成功')
      } else {
        videoRef.value.addEventListener('canplay', async () => {
          await videoRef.value.play()
          console.log('延迟播放成功')
        }, { once: true })
      }
    } catch (error) {
      console.log('视频播放失败:', error)
      handleVideoPlayError()
    }
  }
})

// 视频播放结束处理
const handleVideoEnded = async () => {
  if (videoRef.value) {
    isVideoFading.value = true // 触发淡出
    console.log('视频结束，开始淡出')
    // 等待淡出动画完成（0.5s）
    setTimeout(async () => {
      videoRef.value.currentTime = 0 // 重置到开头
      isVideoFading.value = false // 触发淡入
      console.log('视频重置，开始淡入')
      try {
        await videoRef.value.play()
        console.log('视频重新播放')
      } catch (error) {
        console.log('重新播放失败:', error)
        handleVideoPlayError()
      }
    }, 10000) // 淡出时间 + 停顿时间
  }
}

// 视频播放失败处理
const handleVideoPlayError = () => {
  console.log('使用备用背景')
  if (videoRef.value) {
    videoRef.value.style.display = 'none'
    document.querySelector('.auth-container')?.classList.add('fallback-bg')
  }
}


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
    localStorage.setItem('username', username.value)
    localStorage.setItem('role', response.data.data.role || 'member')
    localStorage.setItem('inviteCode', response.data.data.inviteCode || '')
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
        localStorage.setItem('username', username.value)
        localStorage.setItem('role', response.data.data.role)
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


// 复制邀请码
const copyInviteCode = async () => {
  try {
    if (navigator.clipboard && window.isSecureContext) {
      await navigator.clipboard.writeText(inviteCodeGenerated.value)
      isCopied.value = true
      setTimeout(() => (isCopied.value = false), 2000)
    } else {
      const textArea = document.createElement('textarea')
      textArea.value = inviteCodeGenerated.value
      document.body.appendChild(textArea)
      textArea.select()
      document.execCommand('copy')
      document.body.removeChild(textArea)
      isCopied.value = true
      setTimeout(() => (isCopied.value = false), 2000)
    }
  } catch (error) {
    console.error('复制失败:', error)
  }
}

// 手动跳转登录
const goToLogin = () => {
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
  position: relative;
  overflow: hidden;
  /* 默认备用背景 */
  background: url('/src/assets/images/background_login.jpg') center / cover no-repeat fixed;
}

/* 当视频加载失败时的额外备用背景 */
.auth-container.fallback-bg {
  background: url('/src/assets/images/background_login.jpg') center / cover no-repeat fixed;
}

/* 静态背景图片 */
.auth-container::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: url('/src/assets/images/background_login.jpg') center / cover no-repeat fixed;
  z-index: 0; /* 在视频下方 */
  opacity: 1; /* 默认显示 */
}

/* 视频 */
.auth-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  object-fit: cover;
  z-index: 1; /* 在背景图片上方 */
  pointer-events: none;
  transition: opacity 1.5s ease; /* 淡入淡出 */
  opacity: v-bind('isVideoFading ? 0 : 1'); /* 绑定淡出状态 */
}

.auth-form {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(10px);
  padding: 2.5rem;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  width: 400px;
  animation: fadeIn 0.5s ease;
  position: relative;
  z-index: 10;
}

.title-with-image {
  display: flex; /* 启用flex布局 */
  align-items: center; /* 垂直居中对齐 */
}

.system-image {
  width: 80px;  /* 方形图案宽度 */
  height:80px; /* 方形图案高度 */
  margin-right: 10px; /* 图片和文字间距 */
  transform: translateY(-12px); /* 向上移动10像素 */
}

/* 新增样式 */
.action-buttons {
  display: flex;
  gap: 1rem;
  margin-top: 1.5rem;
}

.primary-btn {
  background: #3182ce;
  color: rgb(255, 255, 255);
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
  font-size: 1rem;
}

input {
  width: 100%;
  padding: 1rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 1rem;
  transition: border-color 0.2s;
  background-color: rgba(255,255,255,0.5); /* 设置背景颜色为半透明 */
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

/* button[type='submit'] {
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
} */

button[type='submit'] {
  width: 100%;
  padding: 1rem;
  background: url('../assets/images/login_bnt.jpg') center / cover no-repeat;
  background-color: transparent; /* 显式设置背景透明 */
  color: rgba(10, 39, 50,0.5);
  border: none;
  border-radius: 8px;
  font-size: 1.2rem; /* 注意：字体非常大，可能需要调整 */
  font-weight: 500;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2), inset 0 1px 0 rgba(255, 255, 255, 0.1);
  position: relative;
  overflow: hidden;
}

button[type='submit']::after {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(255, 255, 255, 0.2), /* 降低光晕不透明度，保持图片可见 */
    transparent
  );
  transition: all 0.5s ease;
  pointer-events: none; /* 防止伪元素干扰交互 */
}

button[type='submit']:hover:not(:disabled) {
  background: url('../assets/images/login_bnt.jpg') center / cover no-repeat;
  background-color: transparent; /* 确保悬浮状态背景透明 */
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(43, 108, 176, 0.4);
}

button[type='submit']:hover:not(:disabled)::after {
  left: 100%; /* 光晕动画效果 */
}

button[type='submit']:disabled {
  opacity: 0.3; /* 调整禁用状态透明度，避免完全不可见 */
  cursor: not-allowed;
  box-shadow: none;
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
