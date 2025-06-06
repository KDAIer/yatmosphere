<template>
  <div class="flat-bedroom-control">
    <div class="control-header">
      <h3>卧室控制</h3>
      <button class="close-btn" @click="$emit('close')">×</button>
    </div>

    <!-- 设备选择下拉框 -->
    <div class="device-selector">
      <label for="device">选择卧室：</label>
      <select id="device" v-model="selectedRoom" @change="loadRoomSettings">
        <option v-for="room in rooms" :key="room.id" :value="room.id">
          {{ room.name }} ({{ room.id }})
        </option>
      </select>
    </div>

    <div class="control-body">
      <!-- 主照明控制 -->
      <div class="section">
        <h4 class="section-title">主照明</h4>
        <div class="light-control">
          <div class="light-switch">
            <label class="switch">
              <input type="checkbox" v-model="mainLightOn" @change="toggleMainLight"/>
              <span class="slider round"></span>
            </label>
            <span class="control-label">{{ mainLightOn ? '开启' : '关闭' }}</span>
          </div>
          <div class="light-brightness" v-if="mainLightOn">
            <label>亮度调节：</label>
            <input
              type="range"
              min="10"
              max="100"
              step="5"
              v-model="mainLightBrightness"
              @input="adjustMainLightBrightness"
            />
            <span class="brightness-value">{{ mainLightBrightness }}%</span>
          </div>
          <div class="light-color" v-if="mainLightOn">
            <label>色温：</label>
            <div class="color-temp-buttons">
              <button
                v-for="temp in colorTemps"
                :key="temp.value"
                class="color-temp-btn"
                :class="{ active: mainLightColorTemp === temp.value }"
                @click="changeLightColorTemp(temp.value)"
                :style="{ backgroundColor: temp.color }"
              >
                {{ temp.name }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 氛围灯控制 -->
      <div class="section">
        <h4 class="section-title">氛围灯</h4>
        <div class="ambient-light-control">
          <div class="light-switch">
            <label class="switch">
              <input type="checkbox" v-model="ambientLightOn" />
              <span class="slider round"></span>
            </label>
            <span class="control-label">{{ ambientLightOn ? '开启' : '关闭' }}</span>
          </div>
          <div class="color-picker" v-if="ambientLightOn">
            <label>灯光颜色：</label>
            <input type="color" v-model="ambientLightColor" @change="updateAmbientLightColor" />
            <div class="preset-colors">
              <div
                v-for="color in presetColors"
                :key="color"
                class="color-preset"
                :style="{ backgroundColor: color }"
                @click="ambientLightColor = color"
              ></div>
            </div>
          </div>
          <div class="light-mode" v-if="ambientLightOn">
            <label>灯光模式：</label>
            <select v-model="ambientLightMode" @change="changeAmbientLightMode">
              <option value="static">静态</option>
              <option value="breath">呼吸</option>
              <option value="rainbow">彩虹</option>
              <option value="strobe">闪烁</option>
            </select>
          </div>
        </div>
      </div>

      <!-- 窗帘控制 -->
      <div class="section">
        <h4 class="section-title">窗帘控制</h4>
        <div class="curtain-control">
          <div class="curtain-buttons">
            <button class="curtain-btn open" @click="controlCurtain('open')">全开</button>
            <button class="curtain-btn stop" @click="controlCurtain('stop')">停止</button>
            <button class="curtain-btn close" @click="controlCurtain('close')">全关</button>
          </div>
          <div class="curtain-position">
            <label>窗帘位置：</label>
            <input
              type="range"
              min="0"
              max="100"
              v-model="curtainPosition"
              @input="adjustCurtainPosition"
            />
            <span class="position-value">{{ curtainPosition }}%</span>
          </div>
        </div>
      </div>

      <!-- 插座控制 -->
      <div class="section">
        <h4 class="section-title">智能插座</h4>
        <div class="outlet-control">
          <div class="outlet-item" v-for="outlet in outlets" :key="outlet.id">
            <div class="outlet-info">
              <span class="outlet-name">{{ outlet.name }}</span>
              <span class="outlet-status" :class="{ on: outlet.status }">
                {{ outlet.status ? '开启' : '关闭' }}
              </span>
            </div>
            <label class="switch">
              <input type="checkbox" v-model="outlet.status" @change="toggleOutlet(outlet.id)" />
              <span class="slider round"></span>
            </label>
          </div>
        </div>
      </div>

      <!-- 音响控制 -->
      <div class="section">
        <h4 class="section-title">卧室音响</h4>
        <div class="audio-control">
          <div class="audio-basic">
            <button class="audio-btn prev" @click="audioControl('prev')">⏮</button>
            <button
              class="audio-btn play-pause"
              @click="audioControl(audioPlaying ? 'pause' : 'play')"
            >
              {{ audioPlaying ? '⏸' : '⏵' }}
            </button>
            <button class="audio-btn next" @click="audioControl('next')">⏭</button>
          </div>
          <div class="audio-volume">
            <label>音量：</label>
            <input
              type="range"
              min="0"
              max="100"
              v-model="audioVolume"
              @input="adjustAudioVolume"
            />
            <span class="volume-value">{{ audioVolume }}%</span>
          </div>
          <div class="audio-source">
            <label>音源：</label>
            <select v-model="audioSource" @change="changeAudioSource">
              <option value="bluetooth">蓝牙</option>
              <option value="wifi">Wi-Fi</option>
              <option value="aux">AUX输入</option>
            </select>
          </div>
        </div>
      </div>

      <!-- 场景模式 -->
      <div class="section">
        <h4 class="section-title">场景模式</h4>
        <div class="scene-control">
          <button
            v-for="scene in scenes"
            :key="scene.id"
            class="scene-btn"
            @click="activateScene(scene.id)"
          >
            {{ scene.name }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineEmits, ref } from 'vue'
import axios from 'axios'

// 房间选择
const selectedRoom = ref('master')
const rooms = ref([
  { id: 'master', name: '主卧' },
  { id: 'guest', name: '客卧' },
  { id: 'kids', name: '儿童房' },
])

// 主照明控制
const mainLightOn = ref(true)
const mainLightBrightness = ref(80)
const mainLightColorTemp = ref('warm')
const colorTemps = ref([
  { name: '暖光', value: 'warm', color: '#FFD39B' },
  { name: '自然', value: 'natural', color: '#FFFACD' },
  { name: '冷光', value: 'cool', color: '#E0FFFF' },
])

// 氛围灯控制
const ambientLightOn = ref(false)
const ambientLightColor = ref('#FF69B4')
const ambientLightMode = ref('static')
const presetColors = ref([
  '#FF0000',
  '#FF7F00',
  '#FFFF00',
  '#00FF00',
  '#0000FF',
  '#4B0082',
  '#9400D3',
  '#FF69B4',
])

// 窗帘控制
const curtainPosition = ref(50)
const curtainState = ref('stopped')

// 插座控制
const outlets = ref([
  { id: 'outlet1', name: '床头插座', status: true },
  { id: 'outlet2', name: '电视插座', status: false },
  { id: 'outlet3', name: '加湿器插座', status: true },
])

// 音响控制
const audioPlaying = ref(false)
const audioVolume = ref(60)
const audioSource = ref('bluetooth')

// 场景模式
const scenes = ref([
  { id: 'sleep', name: '睡眠模式' },
  { id: 'reading', name: '阅读模式' },
  { id: 'movie', name: '观影模式' },
  { id: 'romantic', name: '浪漫模式' },
  { id: 'morning', name: '晨起模式' },
])

const loadRoomSettings = () => {
  // 加载房间设置的逻辑
}

const adjustMainLightBrightness = async () => {//???????????
  // console.log(mainLightBrightness.value)
  try {
    const token = localStorage.getItem('authToken')
    const res = await axios.post(
      `/light/brightness?deviceName=卧室灯&brightness=${mainLightBrightness.value}`,
      null,
      {
        headers: {
          'Content-Type': 'application/json',
          'Authorization': token
        }
      }
    );

    if (res.data.data === true) {
      console.log('Brightness adjusted successfully:', res.data);
      emit('refresh-devices'  )
    } else {
      console.error("卧室灯光设置失败")
    }
  } catch (error) {
    console.error('Failed to adjust brightness:', error);
  }
};
const changeLightColorTemp =async (temp) => {
  mainLightColorTemp.value = temp
  const bedlight = '卧室灯'
  const token = localStorage.getItem('authToken')
  if(mainLightColorTemp.value == 'warm'){
    const res = await axios.post(

      `/light/warmlight?deviceName=${encodeURIComponent(bedlight)}`,
      null,
      {
        headers: {
          'Content-Type': 'application/json',
          'Authorization': token
        }
      }
    )
    if (res.data.data === true) {
      console.log(`温暖设置成功`)
      emit('refresh-devices')
    } else {
      console.error("卧室灯光温暖设置失败")
    }
  }else if (mainLightColorTemp.value == 'cool'){
    const res = await axios.post(

      `/light/coldlight?deviceName=${encodeURIComponent(bedlight)}`,
      null,
      {
        headers: {
          'Content-Type': 'application/json',
          'Authorization': token
        }
      }
    )
    if (res.data.data === true) {
      console.log(`冷设置成功`)
      emit('refresh-devices')
    } else {
      console.error("卧室灯光冷设置失败")
    }
  }else {
    const res = await axios.post(

      `/light/naturelight?deviceName=${encodeURIComponent(bedlight)}`,
      null,
      {
        headers: {
          'Content-Type': 'application/json',
          'Authorization': token
        }
      }
    )
    if (res.data.data === true) {
      console.log(`自然设置成功`)
      emit('refresh-devices')
    } else {
      console.error("卧室灯光自然设置失败")
    }
  }
}

const updateAmbientLightColor = () => {
  // 更新氛围灯颜色的逻辑
}

const changeAmbientLightMode = () => {
  // 更改氛围灯模式的逻辑
}

const controlCurtain = (action) => {
  if (action === 'open') {
    curtainPosition.value = 100
    curtainState.value = 'opened'
  } else if (action === 'close') {
    curtainPosition.value = 0
    curtainState.value = 'closed'
  } else {
    curtainState.value = 'stopped'
  }
}

const adjustCurtainPosition = () => {
  // 调整窗帘位置的逻辑
}
const emit = defineEmits(['refresh-devices'])
const toggleMainLight =async () =>{
  //主照明开启关闭逻辑

  // mainLightOn.value = mainLightOn.value
  console.log('mainlighton',mainLightOn.value)

  try {

    const bedlight = '卧室灯'
    const token = localStorage.getItem('authToken')

    // console.log(`/light/on?deviceName=${encodeURIComponent(bedlight)}`)
    if (mainLightOn.value){
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
          console.log(`设置成功`)
        emit('refresh-devices')
      } else {
        console.error("卧室灯光设置失败")
      }
    }else {
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
        console.log(`设置成功`)
        emit('refresh-devices')
      } else {
        console.error("卧室灯光设置失败")
      }
    }

  } catch (e) {
    console.error("卧室灯光设置失败", e)
  }

}
const toggleOutlet = (outletId) => {
  const outlet = outlets.value.find((o) => o.id === outletId)
  if (outlet) {
    // 切换插座状态的逻辑
  }
}

const audioControl = (action) => {
  if (action === 'play') {
    audioPlaying.value = true
  } else if (action === 'pause') {
    audioPlaying.value = false
  } else if (action === 'prev') {
    // 上一首
  } else if (action === 'next') {
    // 下一首
  }
}

const adjustAudioVolume = () => {
  // 调整音量的逻辑
}

const changeAudioSource = () => {
  // 更改音源的逻辑
}

const activateScene = (sceneId) => {
  // 激活场景模式的逻辑
  const scene = scenes.value.find((s) => s.id === sceneId)
  if (scene) {
    // 根据场景ID执行不同的设置
  }
}
</script>

<style scoped>
.flat-bedroom-control {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  width: 350px;
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
  font-size: 1.8rem;
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

.device-selector {
  margin-bottom: 1.5rem;
  padding: 0.5rem;
  background: var(--color-device-card-bg);
  border-radius: 8px;
}

.device-selector select {
  padding: 0.5rem;
  border-radius: 6px;
  border: 1px solid #ddd;
  margin-left: 0.5rem;
  width: calc(100% - 80px);
}

.section {
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #f0f0f0;
}

.section:last-child {
  border-bottom: none;
}

.section-title {
  margin: 0 0 1rem 0;
  color: #444;
  font-size: 1.1rem;
}

/* 灯光控制样式 */
.light-control,
.ambient-light-control {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.light-switch {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.light-brightness,
.light-color,
.light-mode {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.light-brightness input[type='range'],
.audio-volume input[type='range'],
.curtain-position input[type='range'] {
  flex-grow: 1;
  margin: 0 0.5rem;
}

.brightness-value,
.volume-value,
.position-value {
  min-width: 40px;
  text-align: right;
}

.color-temp-buttons {
  display: flex;
  gap: 0.5rem;
}

.color-temp-btn {
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  color: #333;
  font-size: 0.8rem;
}

.color-temp-btn.active {
  border-color: #3182ce;
  box-shadow: 0 0 0 2px #e3f2fd;
}

/* 氛围灯颜色选择 */
.color-picker {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.preset-colors {
  display: flex;
  gap: 0.3rem;
  flex-wrap: wrap;
}

.color-preset {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  cursor: pointer;
  border: 1px solid #ddd;
}

.color-preset:hover {
  transform: scale(1.1);
}

/* 窗帘控制 */
.curtain-control {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.curtain-buttons {
  display: flex;
  gap: 0.5rem;
}

.curtain-btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  flex: 1;
}

.curtain-btn.open {
  background-color: #e3f2fd;
  color: #3182ce;
}

.curtain-btn.stop {
  background-color: #f5f5f5;
  color: #666;
}

.curtain-btn.close {
  background-color: #ffebee;
  color: #e53935;
}

/* 插座控制 */
.outlet-control {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
}

.outlet-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem;
  border-radius: 6px;
  background-color: var(--color-device-card-bg);
}

.outlet-info {
  display: flex;
  flex-direction: column;
}

.outlet-name {
  font-weight: bold;
}

.outlet-status {
  font-size: 0.8rem;
  color: #666;
}

.outlet-status.on {
  color: #4caf50;
}

/* 音响控制 */
.audio-control {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.audio-basic {
  display: flex;
  justify-content: center;
  gap: 1rem;
}

.audio-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: none;
  background-color: #f0f0f0;
  font-size: 1.2rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.audio-btn.play-pause {
  background-color: #3182ce;
  color: white;
}

.audio-btn:hover {
  background-color: #e0e0e0;
}

.audio-btn.play-pause:hover {
  background-color: #1a6fbb;
}

.audio-volume,
.audio-source {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.audio-source select {
  padding: 0.3rem;
  border-radius: 4px;
  border: 1px solid #ddd;
}

/* 场景控制 */
.scene-control {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 0.8rem;
}

.scene-btn {
  padding: 0.8rem;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background-color: #f5f5f5;
  cursor: pointer;
  transition: all 0.2s;
}

.scene-btn:hover {
  background-color: #e0e0e0;
}

/* 开关样式 */
.switch {
  position: relative;
  display: inline-block;
  width: 50px;
  height: 24px;
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
</style>
