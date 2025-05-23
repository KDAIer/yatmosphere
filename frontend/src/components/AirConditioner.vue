<template>
  <div class="flat-air-control">
    <div class="control-header">
      <h3>空调控制</h3>
      <button class="close-btn" @click="$emit('close')">×</button>
    </div>

    <!-- 设备选择下拉框 -->
    <div class="device-selector">
      <label for="device">选择设备：</label>
      <select id="device" v-model="selectedDevice" @change="loadDeviceSettings">
        <option v-for="device in devices" :key="device.id" :value="device.id">
          {{ device.name }} ({{ device.id }})
        </option>
      </select>
    </div>

    <div class="control-body">
      <!-- 电源控制 -->
      <div class="power-control">
        <button class="power-btn" :class="{ on: isPowerOn }" @click="togglePower">
          {{ isPowerOn ? '关闭' : '开启' }}
        </button>
      </div>

      <!-- 温度显示与调节 -->
      <div class="temp-control">
        <button class="temp-btn" @click="adjustTemp(-0.5)">-</button>
        <div class="temp-display">
          <span class="current-temp">{{ currentTemp }}</span>
          <span class="temp-unit">℃</span>
        </div>
        <button class="temp-btn" @click="adjustTemp(0.5)">+</button>
      </div>

      <!-- 模式切换 -->
      <div class="mode-switch">
        <button
          v-for="mode in modes"
          :key="mode.value"
          class="mode-btn"
          :class="{ active: currentMode === mode.value }"
          @click="changeMode(mode.value)"
        >
          <span class="mode-icon">{{ mode.icon }}</span>
          <span class="mode-name">{{ mode.name }}</span>
        </button>
      </div>

      <!-- 新增功能区域 -->
      <div class="additional-controls">
        <!-- 节能模式开关 -->
        <div class="control-group eco-mode">
          <label class="switch">
            <input type="checkbox" v-model="isEcoMode" />
            <span class="slider round"></span>
          </label>
          <span class="control-label">节能模式 {{ isEcoMode ? 'ON' : 'OFF' }}</span>
        </div>

        <!-- 风量等级控制 -->
        <div class="control-group fan-speed">
          <label>风量等级：</label>
          <div class="speed-levels">
            <button
              v-for="level in 5"
              :key="level"
              class="speed-btn"
              :class="{ active: fanSpeed === level }"
              @click="setFanSpeed(level)"
            >
              {{ level }}
            </button>
          </div>
        </div>

        <!-- 定时功能 -->
        <div class="control-group timer">
          <label>定时：</label>
          <select v-model="timerOption" @change="handleTimerChange">
            <option value="0">关闭定时</option>
            <option value="1">1小时后关</option>
            <option value="2">2小时后关</option>
            <option value="3">3小时后关</option>
            <option value="custom">自定义...</option>
          </select>
          <input
            v-if="timerOption === 'custom'"
            type="time"
            v-model="customTime"
            @change="setCustomTimer"
          />
        </div>

        <!-- 灯光控制 -->
        <div class="control-group light-control">
          <label class="switch">
            <input type="checkbox" v-model="isLightOn" />
            <span class="slider round"></span>
          </label>
          <span class="control-label">空调灯光 {{ isLightOn ? 'ON' : 'OFF' }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const isPowerOn = ref(true)
const currentTemp = ref(22)
const currentMode = ref('cool')
const selectedDevice = ref(null)
const devices = ref([])
const isEcoMode = ref(false)
const fanSpeed = ref(3)
const timerOption = ref('0')
const customTime = ref('00:00')
const isLightOn = ref(true)

const modes = [
  { name: '制冷', value: 'cool', icon: '❄️' },
  { name: '制热', value: 'heat', icon: '🔥' },
  { name: '除湿', value: 'dry', icon: '💧' },
  { name: '送风', value: 'fan', icon: '🌪️' },
]

const togglePower = () => {
  isPowerOn.value = !isPowerOn.value
}

const adjustTemp = (delta) => {
  currentTemp.value = Math.min(30, Math.max(16, currentTemp.value + delta))
}

const changeMode = (mode) => {
  currentMode.value = mode
}

const loadDeviceSettings = () => {
  // 加载设备设置的逻辑
}

const setFanSpeed = (level) => {
  fanSpeed.value = level
}

const handleTimerChange = () => {
  // 处理定时器变化的逻辑
}

const setCustomTimer = () => {
  // 设置自定义定时的逻辑
}
</script>

<style scoped>
.flat-air-control {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  width: 400px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  border: 1px solid #e0e0e0;
}

.control-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid #f0f0f0;
}

.close-btn {
  font-size: 2.5rem;
  line-height: 1;
  padding: 0 0.5rem;
  background: none;
  border: none;
  cursor: pointer;
  color: #666;
  transition: all 0.2s;
}

.close-btn:hover {
  color: #ce3c31;
}

.power-control {
  text-align: center;
  margin-bottom: 2rem;
}

.power-btn {
  padding: 0.8rem 2rem;
  border: none;
  border-radius: 24px;
  background: #e3f2fd;
  color: #3182ce;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 1rem;
}

.power-btn.on {
  background: #3182ce;
  color: white;
}

.temp-control {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.temp-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 2px solid #e2e8f0;
  background: white;
  font-size: 1.2rem;
  cursor: pointer;
  transition: all 0.2s;
}

.temp-btn:hover {
  background: #f5f5f5;
}

.temp-display {
  font-size: 2.5rem;
  font-weight: bold;
  color: #2c3e50;
  min-width: 80px;
  text-align: center;
}

.mode-switch {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.mode-btn {
  padding: 1rem;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  background: white;
  cursor: pointer;
  transition: all 0.2s;
  text-align: center;
}

.mode-btn:hover {
  background: #f8f8f8;
}

.mode-btn.active {
  border-color: #3182ce;
  background: #e3f2fd;
}

.mode-icon {
  font-size: 1.5rem;
  display: block;
  margin-bottom: 0.5rem;
}

/* 设备选择器样式 */
.device-selector {
  margin-bottom: 1.5rem;
  padding: 0.5rem;
  background: #f5f5f5;
  border-radius: 8px;
}

.device-selector select {
  padding: 0.5rem;
  border-radius: 6px;
  border: 1px solid #ddd;
  margin-left: 0.5rem;
  width: calc(100% - 80px);
}

/* 附加控制区域样式 */
.additional-controls {
  margin-top: 1.5rem;
  padding-top: 1.5rem;
  border-top: 1px solid #eee;
}

.control-group {
  display: flex;
  align-items: center;
  margin-bottom: 1rem;
  padding: 0.5rem;
  border-radius: 6px;
  transition: background 0.2s;
}

.control-group:hover {
  background: #f9f9f9;
}

.control-group label {
  margin-right: 0.5rem;
  font-size: 0.9rem;
  color: #555;
  min-width: 60px;
}

/* 开关样式 */
.switch {
  position: relative;
  display: inline-block;
  width: 50px;
  height: 24px;
  margin-right: 0.5rem;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  transition: 0.4s;
}

.slider.round {
  border-radius: 24px;
}

.slider.round:before {
  border-radius: 50%;
}

input:checked + .slider {
  background-color: #4caf50;
}

input:checked + .slider:before {
  transform: translateX(26px);
}

.slider:before {
  position: absolute;
  content: '';
  height: 16px;
  width: 16px;
  left: 4px;
  bottom: 4px;
  background-color: white;
  transition: 0.4s;
}

/* 风量等级按钮样式 */
.speed-levels {
  display: flex;
  gap: 0.5rem;
  margin-left: 0.5rem;
}

.speed-btn {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  border: 1px solid #ddd;
  background: white;
  cursor: pointer;
  transition: all 0.2s;
}

.speed-btn:hover {
  background: #f0f0f0;
}

.speed-btn.active {
  background: #3182ce;
  color: white;
  border-color: #3182ce;
}

/* 定时选择样式 */
.control-group.timer select {
  padding: 0.3rem;
  border-radius: 4px;
  border: 1px solid #ddd;
  margin-left: 0.5rem;
  min-width: 120px;
}

.control-group.timer input[type='time'] {
  padding: 0.3rem;
  border-radius: 4px;
  border: 1px solid #ddd;
  margin-left: 0.5rem;
}

.control-label {
  font-size: 0.9rem;
  color: #333;
}
</style>
