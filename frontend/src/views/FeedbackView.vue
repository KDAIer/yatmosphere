<template>
  <div class="feedback-container">
    <button @click="$router.back()" class="back-btn" aria-label="返回上一页">← 返回</button>
    <h1>提交使用反馈</h1>
    <form @submit.prevent="submitFeedback">
      <div class="form-group">
        <div class="floating-group">
          <input
            id="email"
            v-model="email"
            type="email"
            placeholder=" "
            required
          />
          <label for="email">您的邮箱</label>
        </div>
      </div>
      <div class="form-group">
        <div class="floating-group textarea-group">
          <textarea
            id="content"
            v-model="content"
            placeholder=" "
            required
            rows="4"
          ></textarea>
          <label for="content">反馈内容</label>
        </div>
      </div>
        <div class="button-wrapper">
    <button type="submit">提交</button>
  </div>
    </form>
    <div v-if="statusMessage" :class="['status', statusClass]">
      {{ statusMessage }}
    </div>
  </div>
</template>


<script>
export default {
  name: 'FeedbackView',
  data() {
    return {
      email: '',
      content: '',
      statusMessage: ''
    }
  },
  methods: {
    submitFeedback() {
      console.log('反馈：', this.email, this.content)
      this.statusMessage = '感谢您的反馈！'
      this.email = ''
      this.content = ''
    }
  }
}
</script>

<style scoped>
:root {
  --primary-color: #4a90e2; /* 主色，可根据主题修改 */
  --primary-focus: rgba(74, 144, 226, 0.6);
  --input-bg: rgba(255, 255, 255, 0.2); /* 半透明白 */
  --input-bg-focus: rgba(255, 255, 255, 0.3);
  --container-bg-start: rgba(255, 255, 255, 0.4);
  --container-bg-end: rgba(255, 255, 255, 0.1);
  --border-color: rgba(255, 255, 255, 0.4);
  --text-color: #222;
  --label-color: #666;
  --label-active-color: var(--primary-color);
  --shadow-color: rgba(0, 0, 0, 0.1);
}


/* 容器 */
.feedback-container {
  width: 90%;
  max-width: 800px;
  margin: 2rem auto;
  padding: 32px;
  background: linear-gradient(135deg, var(--container-bg-start), var(--container-bg-end));
  backdrop-filter: blur(10px);
  border: 1px solid rgba(0, 0, 0, 0.15);
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  color: var(--text-color);
  transition: transform 0.2s, box-shadow 0.2s;
}
.feedback-container:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px var(--shadow-color);
}

/* 返回按钮 */
.back-btn {
  display: inline-flex;
  align-items: center;
  font-size: 0.9rem;
  background: none;
  border: none;
  /* color: var(--primary-color); */
  color: rgb(59, 180, 255);
  cursor: pointer;
  padding: 6px 10px;
  border-radius: 6px;
  transition: background-color 0.2s;
}
.back-btn:hover {
  background-color: rgba(74, 144, 226, 0.1);
}

/* 标题 */
h1 {
  font-size: 1.75rem;
  margin: 1rem 0 2rem;
  text-align: center;
}

/* 表单组 */
.form-group {
  margin-bottom: 1.5rem;
}

/* 浮动标签容器 */
.floating-group {
  position: relative;
  width: 100%;
}

/* 通用 input/textarea 样式 */
.floating-group input,
.floating-group textarea {
  width: 100%;
  padding: 16px 12px 4px; /* 上部留给 label */
  background-color: var(--input-bg);
  border: 2px solid rgba(19, 157, 243, 0.47);
  border-radius: 8px;
  color: var(--text-color);
  font-size: 1rem;
  backdrop-filter: blur(8px);
  transition: background-color 0.2s, border-color 0.2s, box-shadow 0.2s;
  resize: none;
}
.floating-group textarea {
  padding: 20px 12px 4px; /* 调整顶部空间 */
  min-height: 100px;
}

/* label 初始态 */
.floating-group label {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  pointer-events: none;
  color: var(--label-color);
  font-size: 1rem;
  background: transparent;
  transition: all 0.2s ease-out;
  padding: 0 4px;
}

/* focus 或非 placeholder-shown 时，label 上浮 */
.floating-group input:focus + label,
.floating-group input:not(:placeholder-shown) + label,
.floating-group textarea:focus + label,
.floating-group textarea:not(:placeholder-shown) + label {
  top: 0px;
  transform: translateY(-50%) scale(0.85);
  color: var(--label-active-color);
  background-color: rgba(255, 255, 255, 0.5);
}

/* input focus 状态 */
.floating-group input:focus,
.floating-group textarea:focus {
  outline: none;
  background-color: var(--input-bg-focus);
  border-color: var(--primary-focus);
  box-shadow: 0 0 0 3px rgba(255, 255, 255, 0);
}

/* placeholder 隐藏（内容为空时保持 placeholder-shown 状态配合浮动）*/
.floating-group input::placeholder,
.floating-group textarea::placeholder {
  color: transparent;
}

/* 提交按钮 */
button[type="submit"] {
  display: inline-block;
  background-color: var(--primary-color);
  color: var(--text-color);
  font-size: 1rem;
  padding: 14px 28px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s, transform 0.1s, box-shadow 0.2s;
  backdrop-filter: blur(8px);
}
button[type="submit"]:hover {
  background-color: darken(var(--primary-color), 10%); /* 或手动调整深一点 */
  box-shadow: 0 4px 12px var(--shadow-color);
}
button[type="submit"]:active {
  transform: scale(0.97);
}
button[type="submit"][disabled] {
  background-color: #aaa;
  cursor: not-allowed;
  box-shadow: none;
}

.button-wrapper {
  text-align: center;
}
/* 状态消息 */
.status {
  margin-top: 1rem;
  font-size: 0.95rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  opacity: 0;
  animation: fadeIn 0.3s forwards;
}
.status.success {
  color: #28a745;
}
.status.error {
  color: #dc3545;
}

/* 动画 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-5px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式 */
@media (max-width: 480px) {
  .feedback-container {
    margin: 1rem;
    padding: 20px;
  }
  h1 {
    font-size: 1.5rem;
  }
  button[type="submit"] {
    width: 100%;
    text-align: center;
  }
}
</style>
