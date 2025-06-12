<template>
  <div class="living-room-panel">
    <div class="panel-header">
      <h2>客厅控制</h2>
      <button class="close-btn" @click="$emit('close')">×</button>
    </div>

    <div class="control-sections">
      <!-- 灯光控制 -->
      <section class="control-card">
        <h3 class="section-title">💡 灯光</h3>
        <div class="device-grid">
          <div
            v-for="(light, index) in lights"
            :key="index"
            class="device-item"
            :class="{ active: light.status }"
          >
            <div class="device-icon">{{ light.icon }}</div>
            <span class="device-name">{{ light.name }}</span>
            <button
              class="toggle-btn"
              :class="{ on: light.status }"
              @click="toggleDevice(light, 'lights')"
            >
              {{ light.status ? '开' : '关' }}
            </button>
          </div>
        </div>
      </section>

      <!-- 电器控制 -->
      <section class="control-card">
        <h3 class="section-title">📺 电器</h3>
        <div class="device-grid">
          <div
            v-for="(appliance, index) in appliances"
            :key="index"
            class="device-item"
            :class="{ active: appliance.status }"
          >
            <div class="device-icon">{{ appliance.icon }}</div>
            <span class="device-name">{{ appliance.name }}</span>
            <button
              class="toggle-btn"
              :class="{ on: appliance.status }"
              @click="toggleDevice(appliance, 'appliances')"
            >
              {{ appliance.status ? '开' : '关' }}
            </button>
          </div>
        </div>

        <!-- 新增电视音量控制 -->
        <div class="volume-control" v-if="selectedAppliance === 'tv'">
          <h4>电视音量</h4>
          <div class="slider-container">
            <input
              type="range"
              min="0"
              max="100"
              v-model="tvVolume"
              class="volume-slider"
              @input="adjustVolume"
            />
            <div class="volume-indicator">
              <span class="volume-icon">🔊</span>
              <span class="volume-value">{{ tvVolume }}%</span>
            </div>
          </div>
          <div class="volume-presets">
            <button @click="setVolume(25)">低</button>
            <button @click="setVolume(50)">中</button>
            <button @click="setVolume(75)">高</button>
          </div>
        </div>
      </section>

      <!-- 窗帘控制 -->
      <section class="control-card">
        <h3 class="section-title">🪟 窗帘</h3>
        <div class="curtain-control">
          <div class="slider-container">
            <input
              type="range"
              min="0"
              max="100"
              v-model="curtainPosition"
              class="curtain-slider"
            />
            <div class="percentage">{{ curtainPosition }}%</div>
          </div>
          <div class="curtain-btns">
            <button class="preset-btn" @click="setCurtain(0)">全关</button>
            <button class="preset-btn" @click="setCurtain(50)">半开</button>
            <button class="preset-btn" @click="setCurtain(100)">全开</button>
          </div>
        </div>
      </section>

      <!-- 新增环境控制 -->
      <section class="control-card">
        <h3 class="section-title">🌡️ 环境</h3>
        <div class="environment-control">
          <div class="env-item">
            <span>温度: {{ temperature }}°C</span>
            <div class="env-buttons">
              <button @click="adjustTemp(-1)">-</button>
              <button @click="adjustTemp(1)">+</button>
            </div>
          </div>
          <div class="env-item">
            <span>湿度: {{ humidity }}%</span>
            <div class="env-buttons">
              <button @click="adjustHumidity(-5)">-</button>
              <button @click="adjustHumidity(5)">+</button>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { defineEmits, ref, watch } from 'vue'
import axios from 'axios'

const lights = ref([
  { name: '主灯', icon: '💡', status: true},
  { name: '沙发灯', icon: '🛋', status: false },
  { name: '落地灯', icon: '🪔', status: false },
  { name: '氛围灯', icon: '✨', status: false },
])

const appliances = ref([
  { name: '电视', icon: '📺', status: false, type: 'tv' },
  { name: '音响', icon: '🔊', status: false, type: 'audio' },
  { name: '空调', icon: '❄️', status: true, type: 'ac' },
  { name: '空气净化器', icon: '🍃', status: false, type: 'air' },
])

const curtainPosition = ref(50)
const tvVolume = ref(30)
const selectedAppliance = ref(null)
const temperature = ref(24)
const humidity = ref(50)
const emit = defineEmits(['refresh-devices'])
// 组件挂载时获取设备状态
// const mainLight = ref(null)
// // 获取主灯状态的方法
//
// import { onActivated, onDeactivated } from 'vue'

// 替代onMounted/onUnmounted
// onActivated(() => {
//   fetchLightStatus()
//   // 组件激活时执行
// })

// onDeactivated(() => {
//   // 组件停用时清理资源
// })
const initializeLights = async () => {
  try {
    // 等待获取主灯状态
    const mainLightStatus = await fetchLightStatus()

    // 更新主灯状态
    if (lights.value.length > 0) {
      lights.value[0].status = mainLightStatus
    }
  } catch (error) {
    console.error('初始化灯具状态失败:', error)
  }
}
import {  onMounted } from 'vue'; // 添加 onMounted 导入
// 在组件挂载时调用初始化函数
onMounted(() => {

  initializeLights()
})
const fetchLightStatus = async () => {
  // mainLight.value = lights.value[0] || null
  console.log("this is fechtlight")
  // try {
    const token = localStorage.getItem('authToken')
    const res = await axios.get('/light/getstatus', {
      params: { deviceName: '客厅灯' },
      headers: {
        'Authorization': token
      }
    }
    )
    console.log(res.data.data)
    return res.data.data

}


const toggleDevice = async (device, type) => {
  device.status = !device.status
  if (device.type === 'tv') {
    selectedAppliance.value = device.status ? 'tv' : null
  }
  if (type == 'lights') {
    const bedlight = '客厅灯'
    const token = localStorage.getItem('authToken')

    // console.log(`/light/on?deviceName=${encodeURIComponent(bedlight)}`)
    if (device.status) {
      const res = await axios.post(
          `/light/on?deviceName=${encodeURIComponent(bedlight)}`,
        null,
        {
          headers: {
            'Content-Type': 'application/json',
            'Authorization': token
          }
        }
      )
      if (res.data.data === true) {
        console.log(`客厅设置成功`)
        emit('refresh-devices')
      } else {
        console.error("客厅灯光设置失败")
      }
    } else {
      const res = await axios.post(
        `/light/off?deviceName=${encodeURIComponent(bedlight)}`,
        null,
        {
          headers: {
            'Content-Type': 'application/json',
            'Authorization': token
          }
        }
      )
      if (res.data.data === true) {
        console.log(`客厅灯光设置成功`)
        emit('refresh-devices')
      } else {
        console.error("客厅卧室灯光设置失败")
      }
    }
  }
}
const setCurtain = (value) => {
  curtainPosition.value = value
}

const adjustVolume = () => {
  console.log(`音量调整为: ${tvVolume.value}%`)
}

const setVolume = (value) => {
  tvVolume.value = value
}

const adjustTemp = (delta) => {
  temperature.value = Math.min(30, Math.max(16, temperature.value + delta))
}

const adjustHumidity = (delta) => {
  humidity.value = Math.min(80, Math.max(30, humidity.value + delta))
}

// 监听电视状态变化
watch(
  () => appliances.value.find((a) => a.type === 'tv')?.status,
  (newVal) => {
    if (!newVal) {
      tvVolume.value = 0
    }
  },
)
</script>

<style scoped>
.living-room-panel {
  width: 500px;
  padding: 1.2rem;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.2rem;
  padding-bottom: 0.8rem;
  border-bottom: 1px solid #eee;
}

.panel-header h2 {
  font-size: 1.4rem;
  margin: 0;
  color: #2c3e50;
}

.close-btn {
  font-size: 1.8rem;
  line-height: 1;
  padding: 0 0.8rem 0.2rem;
  background: none;
  border: none;
  cursor: pointer;
  color: #666;
  transition: all 0.2s;
}

.close-btn:hover {
  color: #ce3c31;
  transform: scale(1.1);
}

.control-sections {
  display: grid;
  gap: 1.2rem;
}

.control-card {
  background: var(--color-device-card-bg);
  border-radius: 10px;
  padding: 1.2rem;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  color: var(--color-text);
}

.section-title {
  margin: 0 0 1rem 0;
  font-size: 1.1rem;
  color: var(--color-text);
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.device-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(80px, 1fr));
  gap: 1rem;
}

.device-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 1rem 0.5rem;
  border-radius: 8px;
  background: var(--color-device-card-bg);
  border: 1px solid #e2e8f0;
  transition: all 0.2s;
}

.device-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.device-item.active {
  border-color: #3182ce;
  background: var(--color-device-card-active-bg);
}

.device-icon {
  font-size: 1.8rem;
  margin-bottom: 0.5rem;
}

.device-name {
  font-size: 0.85rem;
  margin-bottom: 0.8rem;
  font-weight: 500;
  text-align: center;
}

.toggle-btn {
  padding: 0.3rem 0.8rem;
  font-size: 0.8rem;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  background: white;
  cursor: pointer;
  transition: all 0.2s;
  min-width: 50px;
}

.toggle-btn.on {
  background: #3182ce;
  color: white;
  border-color: #3182ce;
}

/* 音量控制样式 */
.volume-control {
  margin-top: 1.5rem;
  padding-top: 1rem;
  border-top: 1px dashed #ddd;
}

.volume-control h4 {
  margin: 0 0 0.8rem 0;
  font-size: 0.95rem;
  color: #4a5568;
}

.slider-container {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  margin-bottom: 0.8rem;
}

.volume-slider,
.curtain-slider {
  width: 100%;
  height: 8px;
  border-radius: 4px;
  background: #e2e8f0;
  outline: none;
  -webkit-appearance: none;
}

.volume-slider::-webkit-slider-thumb,
.curtain-slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: #3182ce;
  cursor: pointer;
}

.volume-indicator {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 0.9rem;
}

.volume-icon {
  font-size: 1.2rem;
}

.volume-value {
  font-weight: bold;
  color: #3182ce;
}

.volume-presets,
.curtain-btns {
  display: flex;
  justify-content: space-between;
  gap: 0.8rem;
  margin-top: 0.8rem;
}

.volume-presets button,
.preset-btn {
  flex: 1;
  padding: 0.4rem 0;
  font-size: 0.8rem;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  background: white;
  cursor: pointer;
  transition: all 0.2s;
}

.volume-presets button:hover,
.preset-btn:hover {
  background: #edf2f7;
}

.percentage {
  text-align: center;
  font-size: 0.9rem;
  color: #4a5568;
  margin-top: 0.3rem;
}

/* 环境控制样式 */
.environment-control {
  display: grid;
  gap: 1rem;
  background: var(--color-device-card-bg);

}

.env-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.8rem;
  background: var(--color-device-card-bg);
  border-radius: 8px;
  border: 1px solid #e2e8f0;

}

.env-item span {
  font-size: 0.9rem;
  font-weight: 500;

}

.env-buttons {
  display: flex;
  gap: 0.5rem;
}

.env-buttons button {
  width: 28px;
  height: 28px;
  border: 1px solid #e2e8f0;
  border-radius: 50%;
  background: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.9rem;
}

.env-buttons button:hover {
  background: #edf2f7;
}
</style>

<!-- 1. 整体结构变化
原代码（模态框）：
html
<div class="living-room-modal">
  <div class="modal-backdrop" @click="$emit('close')"></div>
  <div class="modal-content">

  </div>
</div>
修改后（直接嵌入）：
html
<div class="living-room-panel">

</div>
变化：移除了遮罩层和模态框层级结构，改为扁平化设计。

2. 尺寸与间距调整
原样式：
css
.modal-content {
  width: 600px;
  padding: 2rem;
  border-radius: 16px;
}
.device-item {
  padding: 1rem;
  margin-bottom: 0.8rem;
}
修改后：
css
.living-room-panel {
  width: 380px; /* 宽度缩小 */
  padding: 1rem; /* 内边距减小 */
  border-radius: 10px; /* 圆角缩小 */
}
.device-item {
  padding: 0.8rem 0.5rem; /* 内边距压缩 */
  margin-bottom: 0.5rem;
}
变化：所有尺寸按比例缩小约30%-40%。-->
