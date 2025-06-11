<template>
  <div class="chat-page">
    <div class="chat-header">
      <h2><span class="gradient-text">Yatmosphere</span> 智能助手</h2>
    </div>

    <div class="chat-window" ref="chat-window">
      <div
        v-for="(msg, i) in messages"
        :key="i"
        :class="['chat-message', msg.role]"
      >
        <div class="bubble">
          <!-- 将原来的 <p>{{ msg.content }}</p> 改为渲染 HTML -->
          <div 
            class="markdown-body" 
            v-html="renderMarkdown(msg.content)">
          </div>
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

// 导入 Markdown-it 和 DOMPurify
import MarkdownIt from 'markdown-it'
import DOMPurify from 'dompurify'
// 如果需要代码高亮：
import hljs from 'highlight.js'  // 确保已安装 highlight.js
import 'highlight.js/styles/github.css'  // 可选：高亮样式

const messages = ref([])
const userInput = ref('')
const loading = ref(false)
const chatWindow = ref(null)

// 初始化 Markdown-it 实例，开启代码高亮支持
const md = new MarkdownIt({
  html: true,
  linkify: true,
  typographer: true,
  highlight(str, lang) {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return '<pre class="hljs"><code>' +
               hljs.highlight(str, { language: lang, ignoreIllegals: true }).value +
               '</code></pre>';
      } catch (__) {}
    }
    // 不是特定语言或高亮失败，使用自动检测
    try {
      const res = hljs.highlightAuto(str);
      return '<pre class="hljs"><code>' + res.value + '</code></pre>';
    } catch (__) {}
    // 最后回退，不高亮
    return '<pre class="hljs"><code>' + md.utils.escapeHtml(str) + '</code></pre>';
  }
});

// 将 Markdown 转为安全的 HTML
function renderMarkdown(text) {
  if (!text) return '';
  const rawHtml = md.render(text);
  // 使用 DOMPurify 清理，防止 XSS
  return DOMPurify.sanitize(rawHtml);
}

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
        { role: 'system', content: '智汇云端，问道未来' },
        { role: 'assistant', content: '您好！我是 Yatmosphere 智能助手，随时为您解答疑惑，开启智能对话新体验！' }
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
