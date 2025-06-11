<template>
  <div class="chat-page">
    <div class="chat-header">
      <h2><span class="gradient-text">Yatmosphere</span> 智能问答</h2>
    </div>

    <div class="chat-window" ref="chat-window">
      <div
        v-for="(msg, i) in messages"
        :key="i"
        :class="['chat-message', msg.role]"
      >
        <div class="bubble">
          <p>{{ msg.content }}</p>
        </div>
      </div>
      <!-- 等待指示器 -->
      <div v-if="loading" class="chat-message assistant">
        <div class="bubble typing">
          <span class="dot" v-for="n in 3" :key="n"></span>
        </div>
      </div>
    </div>

    <div class="chat-input-card">
      <form class="chat-input-area" @submit.prevent="sendMessage">
        <textarea
          v-model="userInput"
          placeholder="请输入问题…"
          class="chat-input"
          :disabled="loading"
          @keydown.enter.prevent="handleEnter"
        ></textarea>
        <button
          type="submit"
          class="send-btn"
          :disabled="loading || !userInput.trim()"
        >
          <span class="arrow-icon">↑</span>
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import axios from 'axios'

const messages = ref([])
const userInput = ref('')
const loading = ref(false)
const chatWindow = ref(null)

// 滚动到底部
async function scrollToBottom() {
  await nextTick()
  if (chatWindow.value) {
    chatWindow.value.scrollTop = chatWindow.value.scrollHeight
  }
}

function handleEnter(e) {
  if (e.shiftKey) {
    // Shift + Enter 换行
    const textarea = e.target
    const start = textarea.selectionStart
    const end = textarea.selectionEnd
    userInput.value = userInput.value.slice(0, start) + '\n' + userInput.value.slice(end)
    nextTick(() => {
      textarea.selectionStart = textarea.selectionEnd = start + 1
    })
  } else {
    // Enter 发送消息
    sendMessage()
  }
}


// 初始化对话
onMounted(async () => {
  try {
    const res = await axios.get('/api/chat/init')
    if (res.data && Array.isArray(res.data)) {
      messages.value = res.data
    } else {
      // 兜底
      messages.value = [
        { role: 'system', content: '你是 Yatmosphere 智能助手，回答简明扼要。' },
        { role: 'assistant', content: '欢迎使用智能问答，有什么我可以帮您？' }
      ]
    }
    await scrollToBottom()
  } catch (err) {
    console.error('初始化聊天失败', err)
  }
})

async function sendMessage() {
  const question = userInput.value.trim()
  if (!question) return

  messages.value.push({ role: 'user', content: question })
  userInput.value = ''
  loading.value = true
  await scrollToBottom()

  try {
    const payload = { messages: messages.value.map(m => ({ role: m.role, content: m.content })) }
    console.log('[Debug] 发送 payload:', payload)
    const res = await axios.post('/api/chat', payload)
    console.log('[Debug] 后端响应 status:', res.status)
    console.log('[Debug] 后端响应 data:', res.data)
    const answer = res.data.data && (res.data.data.content || res.data.data.answer) || '抱歉，未能生成回答。'
    console.log('[Debug] 提取 answer:', answer)
    messages.value.push({ role: 'assistant', content: answer || '无回答' })
  } catch (err) {
    console.error('[Debug] 请求失败:', err, err.response?.data)
    messages.value.push({ role: 'assistant', content: '出错了，请稍后再试。' })
  } finally {
    loading.value = false
    await scrollToBottom()
  }
}

</script>

<style scoped>
@import './Chat.css';
</style>
