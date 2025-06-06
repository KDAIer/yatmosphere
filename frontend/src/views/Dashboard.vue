<!-- src/components/Dashboard.vue -->
<template>
  <div class="dashboard" :class="{ 'mobile-layout': isMobileView }">
    <div v-if="showModeDialog" class="mode-dialog-overlay">
      <div class="mode-dialog">
        <h3>请选择访问模式</h3>
        <div class="mode-buttons">
          <button class="toggle-btn" @click="toggleViewMode">
          {{ isMobileView ? '切换到电脑' : '切换到手机' }}
          <br>
          </button>
          <button class="close-btn" @click="close_toggleViewMode">
          {{ '进入系统' }}
          </button>
        </div>
      </div>
    </div>
    <!-- 头部 -->
    <header class="header">
      <!-- 左侧：Logo + 标题 -->
      <div class="header-left">
        <img src="/src/assets/images/logo.png" alt="Logo" class="header-logo" />
        <h1 class="header-title">智能家居控制中心</h1>
      </div>

      <!-- 右侧：搜索框 + 通知 + 头像 -->
      <div class="header-right">
        <!-- 搜索框 -->
        <div class="search-container">
          <input type="text" v-model="searchQuery" @focus="showSearchResults = true" @keyup.enter="onSearch"
            placeholder="搜索设备…" class="search-input" />
          <button class="search-btn" @click="onSearch" title="搜索">🔍</button>
          <!-- 搜索结果下拉 -->
          <div v-if="showSearchResults" class="search-results">
            <div v-if="filteredResults.length">
              <div v-for="(result, idx) in filteredResults" :key="idx" class="result-item"
                @click="selectResult(result)">
                {{ result }}
              </div>
            </div>
            <div v-else class="no-results">未找到相关设备</div>
          </div>
        </div>

        <!-- 通知图标 -->
        <div class="notification-container" @click="openNotifications" title="查看通知">
          <span class="notification-icon">🔔</span>
          <span v-if="unreadCount > 0" class="notification-badge">{{ unreadCount }}</span>
        </div>

        <!-- 用户头像，点击跳转到 /profile -->
        <div class="user-avatar" @click="goToProfile" title="查看个人信息">
          <img :src="user.avatar || defaultAvatar" alt="用户头像" class="avatar-img" />
        </div>
      </div>
    </header>

    <!-- 通知弹窗 -->
    <div v-if="showNotifications" class="modal-backdrop" @click.self="closeNotifications">
      <div class="notification-modal">
        <div class="modal-header">
          <h2>通知中心</h2>
          <button class="close-modal-btn" @click="closeNotifications">×</button>
        </div>
        <div class="modal-body">
          <div v-if="notifications.length">
            <div v-for="(note, idx) in notifications" :key="idx" class="notification-item">
              <p class="note-text">{{ note.text }}</p>
              <span class="note-time">{{ note.time }}</span>
            </div>
          </div>
          <div v-else class="no-notifications">暂无通知</div>
        </div>
      </div>
    </div>

    <!-- 主内容区 -->
    <main class="main-content">
      <!-- 左侧2x3网格布局 -->
      <div class="grid-layout">
        <!-- 第一行 -->
        <div class="grid-row">
          <!-- 快速控制 -->
          <section class="quick-control card">
            <h2>快速控制</h2>
            <div class="device-grid">
              <div class="device-card" v-for="(device, index) in quickDevices" :key="device.id"
                :class="{ 'device-on': device.state }">
                <div class="device-icon">
                  <img :src="device.icon" class="device-icon" alt="device icon" />
                </div>
                <div class="device-info">
                  <h3>{{ device.name }}</h3>
                  <p class="device-status">{{ device.status }}</p>
                  <div v-if="device.type === 'network' && device.state" class="signal-strength">
                    <div class="wifi-icon" :data-strength="Math.ceil(device.signalStrength / 20)">
                      <span></span>
                    </div>
                  </div>
                </div>
                <div class="device-control">
                  <label class="switch">
                    <input type="checkbox" :checked="device.state" @change="handleDeviceAction(device)"
                      @click="triggerParticleEffect($event, device.id)" :disabled="device.type === 'emergency'" />
                    <span class="slider"></span>
                    <div class="particle-container" :class="{ active: activeParticle === device.id }">
                      <span class="particle" v-for="n in 8" :key="n"
                        :style="{ '--angle': `${(n - 1) * 45}deg` }"></span>
                    </div>
                  </label>
                </div>
              </div>
            </div>
          </section>

          <!-- 网络弹窗 -->
          <Teleport to="body">
            <div v-if="networkModalVisible" class="network-modal">
              <div class="modal-content">
                <h3>网络设置</h3>
                <div class="network-info">
                  <p>当前连接：家庭Wi-Fi_5G</p>
                  <p>信号强度：
                    <span class="wifi-icon inline" :data-strength="Math.ceil(getNetworkStrength() / 25)">
                      <span></span>
                    </span>
                    {{ getNetworkStrength() }}%
                  </p>
                </div>
                <button class="btn" @click="networkModalVisible = false">关闭</button>
              </div>
            </div>
          </Teleport>



          <!-- 区域控制 -->
          <section class="area-control card">
            <h2>区域控制</h2>
            <div class="grid-area-selector" ref="gridItems">
              <div class="grid-area-item" v-for="(area, index) in areas" :key="index" @click="toggleArea(area.id)"
                :class="{ active: activeArea === area.id, 'has-sub': area.children }">
                <!-- <font-awesome-icon :icon="area.icon" class="area-icon" /> -->
                <img :src="area.icon" class="area-icon" alt="area icon" />
                <span class="area-name">{{ area.name }}</span>
              </div>
            </div>
            <div class="area-details" v-if="activeArea" @click.self="activeArea = null">
              <component :is="getAreaComponent(activeArea)" @close="activeArea = null"
                @refresh-devices="fetchAllDevices" class="modal-content" />
            </div>
          </section>
        </div>
        <!-- @refresh-devices="fetchAllDevices" -->
        <!-- @update-device="updateDeviceInfo" -->

        <!-- 第二行 -->
        <div class="grid-row">
          <!-- 环境监测 -->
          <section class="environment card">
            <h2>环境监测</h2>
            <div class="data-grid">
              <template v-for="(data, key) in environmentData" :key="key">
                <div class="data-card" :class="{ 'time-card': key === 'time' }" v-if="key === 'time'">
                  <div class="data-label">{{ data.label }}</div>
                  <div class="data-value time-value">{{ currentTime || '加载中...' }}</div>
                </div>
                <div class="data-card" v-else>
                  <div class="data-label">{{ data.label }}</div>
                  <div class="data-value">{{ data.value }}</div>
                </div>
              </template>
            </div>
          </section>

          <!-- 智能场景 -->
          <section class="scenes card">
            <div class="scenes-header">
              <h2>智能场景</h2>
              <button class="add-scene-btn" @click="showSceneCreator = true">
                <span>+ 自定义场景</span>
              </button>
            </div>

            <div class="scenes-container">
              <div class="scenes-grid">
                <!-- 预设场景 -->
                <button v-for="(scene, index) in scenes" :key="index" class="scene-btn"
                  @click="activateScene(scene.id)">
                  <span class="scene-icon">{{ scene.icon }}</span>
                  {{ scene.name }}
                </button>

                <!-- 自定义场景 -->
                <button v-for="scene in customScenes" :key="'custom-' + scene.id" class="scene-btn custom-scene"
                  @click="activateScene(scene.id)" @contextmenu.prevent="editScene(scene.id)">
                  <span class="scene-icon">{{ scene.icon }}</span>
                  <span class="scene-name">{{ scene.name }}</span>
                  <span class="scene-delete" @click.stop="deleteScene(scene.id)">×</span>
                </button>
              </div>
            </div>

            <!-- 场景创建/编辑弹窗 -->
            <div class="scene-modal" v-if="showSceneCreator" @click.self="showSceneCreator = false">
              <div class="scene-modal-content">
                <h3>{{ editingScene ? '编辑场景' : '创建场景' }}</h3>

                <div class="form-group">
                  <label>场景名称</label>
                  <input v-model="newScene.name" placeholder="输入场景名称">
                </div>

                <div class="form-group">
                  <label>选择图标</label>
                  <div class="icon-grid">
                    <div v-for="icon in sceneIcons" :key="icon" class="icon-option"
                      :class="{ selected: newScene.icon === icon }" @click="newScene.icon = icon">
                      {{ icon }}
                    </div>
                  </div>
                </div>

                <div class="modal-actions">
                  <button v-if="editingScene" class="delete-btn" @click="confirmDeleteScene">
                    删除
                  </button>
                  <button class="cancel-btn" @click="cancelEdit">取消</button>
                  <button class="confirm-btn" @click="saveScene">
                    {{ editingScene ? '保存' : '创建' }}
                  </button>
                </div>
              </div>
            </div>
          </section>

        </div>
      </div>
      <!--权限错误弹窗-->
      <div class="access-error-modal" v-if="showAccessError" @click.self="showAccessError = false">
        <div class="modal-content">
          <h3 class="error-title"><span style="color: red">⚠</span>权限错误</h3>
          <p class="error-message">&nbsp;您没有权限使用此功能，请联系管理员&nbsp;</p>
        </div>
      </div>

    <!-- 右侧：天气 + GPS 模块 -->
        <div class="right-column">
          <!-- 天气 -->
          <section class="cute-weather-panel">
            <div class="weather-header">
              <div class="header-icon">☁️</div>
              <div class="header-text">天气播报</div>
            </div>
            <div class="weather-body" v-if="!weather.loading">
              <div class="weather-main">
                <div class="weather-icon" :class="weatherIconClass"></div>
                <div class="weather-temp">{{ weather.currentTemp }}℃</div>
              </div>
              <div class="weather-desc">{{ weather.description }}</div>
              <div class="weather-minmax">
                <div class="min-box">
                  <span class="arrow-down">⬇️</span>
                  <span>{{ weather.tempMin }}℃</span>
                </div>
                <div class="max-box">
                  <span class="arrow-up">⬆️</span>
                  <span>{{ weather.tempMax }}℃</span>
                </div>
              </div>
              <div class="weather-footer">
                <div class="footer-item">
                  <span class="footer-icon">💧</span>
                  <span class="footer-text">{{ weather.humidity }}%</span>
                </div>
                <div class="footer-item">
                  <span class="footer-icon">💨</span>
                  <span class="footer-text">{{ weather.windSpeed }}km/h</span>
                </div>
              </div>
            </div>
            <div class="weather-loading" v-else>加载中…</div>
          </section>

          <!-- GPS 定位 -->
          <section class="cute-gps-panel">
            <div class="gps-header">
              <div class="gps-icon">📍</div>
              <div class="gps-text">当前位置</div>
            </div>
            <div class="gps-body" v-if="!gps.loading && !gps.error">
              <div class="gps-coords-card">
                <div class="coord-item">
                  <span class="coord-label">城市：</span>
                  <span class="coord-value">{{ gps.city }}</span>
                </div>
                <div class="coord-item">
                  <span class="coord-label">纬度：</span>
                  <span class="coord-value">{{ gps.latitude.toFixed(5) }}</span>
                </div>
                <div class="coord-item">
                  <span class="coord-label">经度：</span>
                  <span class="coord-value">{{ gps.longitude.toFixed(5) }}</span>
                </div>
              </div>
                <div class="map-container">
                  <iframe
                    v-if="!gps.loading && !gps.error"
                    :src="osmEmbedUrl"
                    class="map-iframe"
                    frameborder="0"
                    scrolling="no"
                  ></iframe>
                <div class="map-pin"></div>
              </div>
            </div>
            <div class="gps-loading" v-if="gps.loading">正在获取定位…</div>
            <div class="gps-error" v-if="gps.error">{{ gps.error }}</div>
          </section>
        </div>
    </main>


  </div>
</template>

<script setup>
import axios from 'axios'
import AirConditioner from '@/components/AirConditioner.vue'
const devices = ref([])
import { defineEmits } from 'vue'
import defaultAvatar from '@/assets/images/user.png'
const emit = defineEmits(['refresh-devices'])
import { ref, onMounted, computed, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import {
  theme,
  toggleTheme,
  inviteCode,
  username,
  activeArea,
  areas,
  quickDevices,
  currentTime,
  environmentData,
  scenes,
  allDevices,
  familyMembers,
  toggleHomeStatus,
  toggleArea,
  getAreaComponent,
  handleDeviceAction,
  networkModalVisible,
  showNetworkModal,
  logout,
  activeParticle,
  triggerParticleEffect,
  useTimeUpdater, // 导入时间更新组合函数
  FontAwesomeIcon,
  roleName,
  faFan,
  faHouse,
  faUtensils,
  faBed,
  faPowerOff,
  faLock,
  faShieldAlt,
  faExclamationTriangle,
  faWifi,
} from './DashboardLogic.js'

// 调用时间更新组合函数
useTimeUpdater()

const activateScene = (sceneId) => {
  console.log('Activating scene:', sceneId)
}


// 用户头像相关逻辑
const router = useRouter()

const user = ref({ avatar: '' })

// 页面挂载时可从 localStorage 或后端获取用户头像
onMounted(async () => {
  const storedAvatar = localStorage.getItem('dashboard_user_avatar_' + localStorage.getItem('username'))
  if (storedAvatar) {
    user.value.avatar = storedAvatar
  } else {
  }
})

// 点击头像直接跳转到 /profile
function goToProfile() {
  router.push('/profile')
}

// 天气预报
const weather = ref({
  loading: true,
  currentTemp: '--',
  tempMin: '--',
  tempMax: '--',
  humidity: '--',
  windSpeed: '--',
  description: '…'
})
// 根据温度、时间段判断一个简单图标类
const weatherIconClass = computed(() => {
  if (weather.value.description.includes('晴')) return 'icon-sun'
  if (weather.value.description.includes('云')) return 'icon-cloud'
  if (weather.value.description.includes('雨')) return 'icon-rain'
  return 'icon-sun'
})

async function loadWeather(lat, lon) {
  try {
    const url = `https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${lon}&hourly=temperature_2m,relativehumidity_2m,windspeed_10m&current_weather=true&timezone=auto`
    const res = await axios.get(url)
    const data = res.data
    if (data.current_weather) {
      const cw = data.current_weather
      weather.value.currentTemp = Math.round(cw.temperature)
      // 查找当日最高/最低
      const todayHourly = data.hourly.time.map((t, i) => {
        const dt = new Date(t)
        if (
          dt.getFullYear() === new Date().getFullYear() &&
          dt.getMonth() === new Date().getMonth() &&
          dt.getDate() === new Date().getDate()
        ) {
          return {
            temp: data.hourly.temperature_2m[i],
            humid: data.hourly.relativehumidity_2m[i],
            wind: data.hourly.windspeed_10m[i]
          }
        }
        return null
      }).filter((x) => x !== null)
      if (todayHourly.length) {
        const temps = todayHourly.map((h) => h.temp)
        weather.value.tempMin = Math.round(Math.min(...temps))
        weather.value.tempMax = Math.round(Math.max(...temps))
        // 当下湿度、风速
        const idxNow = data.hourly.time.indexOf(data.current_weather.time)
        if (idxNow >= 0) {
          weather.value.humidity = data.hourly.relativehumidity_2m[idxNow]
          weather.value.windSpeed = data.hourly.windspeed_10m[idxNow]
        }
      }
      // 简单描述：根据温度与湿度判断
      if (weather.value.currentTemp >= weather.value.tempMax * 0.8) {
        weather.value.description = '晴天'
      } else if (weather.value.humidity >= 80) {
        weather.value.description = '潮湿'
      } else {
        weather.value.description = '多云'
      }
    }
  } catch (e) {
    console.error('获取天气失败', e)
    weather.value.description = '数据获取失败'
  } finally {
    weather.value.loading = false
  }
}

// GPS 定位
const gps = ref({
  loading: true,
  error: '',
  latitude: 0,
  longitude: 0,
  city: '未知'
})
// 反向地理解析函数
async function reverseGeocode(lat, lon) {
  try {
    const url = `https://nominatim.openstreetmap.org/reverse?lat=${lat}&lon=${lon}&format=json`
    const res = await axios.get(url)
    if (res.data && res.data.address) {
      const addr = res.data.address
      gps.value.city = addr.city || addr.town || addr.village || '未知'
    }
  } catch (e) {
    console.warn('反向地理解析失败', e)
    gps.value.city = '未知'
  }
}

function loadGPS() {
  if (!navigator.geolocation) {
    gps.value.error = '浏览器不支持定位'
    gps.value.loading = false
    weather.value.loading = false
    return
  }
  navigator.geolocation.getCurrentPosition(
    async (pos) => {
      gps.value.latitude = pos.coords.latitude
      gps.value.longitude = pos.coords.longitude
      await reverseGeocode(gps.value.latitude, gps.value.longitude)
      gps.value.loading = false
      loadWeather(gps.value.latitude, gps.value.longitude)
    },
    (err) => {
      gps.value.error = '定位授权失败'
      gps.value.loading = false
      weather.value.loading = false
    }
  )
}

onMounted(() => {
  loadGPS()
})

// OSM 嵌入式地图 URL
const osmEmbedUrl = computed(() => {
  if (!gps.value.latitude) return ''
  const lat = gps.value.latitude
  const lon = gps.value.longitude
  const delta = 0.01
  const minLat = (lat - delta).toFixed(5)
  const maxLat = (lat + delta).toFixed(5)
  const minLon = (lon - delta).toFixed(5)
  const maxLon = (lon + delta).toFixed(5)
  // bbox=最小经度,最小纬度,最大经度,最大纬度
  // marker=纬度,经度
  return `https://www.openstreetmap.org/export/embed.html?bbox=${minLon},${minLat},${maxLon},${maxLat}&layer=mapnik&marker=${lat},${lon}`
})


// 获取网络强度
const getNetworkStrength = () => {
  const networkDevice = quickDevices.value.find(d => d.type === 'network')
  return networkDevice ? networkDevice.signalStrength : 0
}

// 监听设备列表变化，自动更新
const fetchAllDevices = async () => {
  try {
    const token = localStorage.getItem('authToken')
    const res = await axios.get('/device/getall', {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': token
      }
    })
    if (res.data && Array.isArray(res.data.data)) {
      devices.value = res.data.data.map(item => ({
        id: item.deviceId,
        name: item.deviceName,
        category: item.category,
        state: item.status,
        details: item.detail
      }))
    }
    console.log('设备列表', devices.value)
  } catch (e) {
    console.error('获取设备列表失败', e)
  }
}

// 设备管理相关逻辑
onMounted(fetchAllDevices);
// 设备管理相关状态
const showAddDeviceModal = ref(false)
const showRemoveDeviceModal = ref(false)
//要删除的部分
//要删除的部分
// 自定义场景
const customScenes = ref([])

// 场景管理状态
const showSceneCreator = ref(false)
const editingScene = ref(null)
const newScene = ref({
  id: null,
  name: '',
  icon: '✨'
})

const sceneIcons = [
  // 房间/区域
  '🏠', '🛏️', '🛋️', '🚿', '🧻', '🍽️', '🏙️', '🌳',

  // 活动场景
  '🎬', '🎮', '🎵', '🎤', '📖', '✍️', '🧘', '🏋️',

  // 设备/功能
  '💡', '📱', '💻', '🖥️', '🔌', '🔋', '📶', '🔊',

  // 自然/时间
  '🌞', '🌙', '☀️', '🌤️', '⛅', '🌧️', '❄️', '✨',

  // 安全/工具
  '🔒', '🔑', '🛡️', '🚨', '⏰', '📅', '🔄', '🎚️'
];

// 创建新场景
const createScene = () => {
  if (!newScene.value.name.trim()) return

  const sceneId = 'custom-' + Date.now()
  customScenes.value.push({
    id: sceneId,
    name: newScene.value.name,
    icon: newScene.value.icon
  })

  resetSceneForm()
  showSceneCreator.value = false
}

// 编辑场景
const editScene = (sceneId) => {
  const scene = customScenes.value.find(s => s.id === sceneId)
  if (scene) {
    newScene.value = { ...scene }
    editingScene.value = sceneId
    showSceneCreator.value = true
  }
}

// 保存场景
const saveScene = () => {
  if (!newScene.value.name.trim()) return

  // 检查权限
  if (!checkRoleAccess()) {
    console.error('没有权限保存场景，当前角色:', localStorage.getItem('role'), '当前value:', roleAccess.value)
    return
  }

  if (editingScene.value) {
    // 更新现有场景
    const index = customScenes.value.findIndex(s => s.id === editingScene.value)
    if (index !== -1) {
      customScenes.value[index] = { ...newScene.value }
    }
  } else {
    // 创建新场景
    createScene()
    return
  }

  resetSceneForm()
  showSceneCreator.value = false
}

// 删除场景
const deleteScene = (sceneId) => {
  // customScenes.value = customScenes.value.filter(scene => scene.id !== sceneId)
  // 检查权限
  if (!checkRoleAccess()) {
    console.error('没有权限删除场景，当前角色:', localStorage.getItem('role'), '当前value:', roleAccess.value)
    return
  }
  customScenes.value = customScenes.value.filter(scene => scene.id !== sceneId)
}

// 确认删除
const confirmDeleteScene = () => {
  // 检查权限
  if (!checkRoleAccess()) {
    console.error('没有权限删除场景，当前角色:', localStorage.getItem('role'), '当前value:', roleAccess.value)
    return
  }
  if (confirm('确定要删除这个场景吗？')) {
    deleteScene(editingScene.value)
    resetSceneForm()
    showSceneCreator.value = false
  }
}

// 取消编辑
const cancelEdit = () => {
  resetSceneForm()
  showSceneCreator.value = false
}

// 重置表单
const resetSceneForm = () => {
  newScene.value = { id: null, name: '', icon: '✨' }
  editingScene.value = null
}


const isMobileView = ref(false)
const showModeDialog = ref(true)

// 双端切换按钮
const toggleViewMode = () => {
  isMobileView.value = !isMobileView.value
}

const close_toggleViewMode = () => {
  showModeDialog.value = false
}

// 搜索状态与模拟结果
const searchQuery = ref('')
const showSearchResults = ref(false)

// 模拟设备列表
const devicesList = [
  '客厅空调',
  '卧室灯光',
  '厨房冰箱',
  '阳台空气净化器',
  '车库门传感器',
  '书房加湿器'
]

// 根据 searchQuery 过滤结果
const filteredResults = computed(() => {
  const q = searchQuery.value.trim().toLowerCase()
  if (!q) return []
  return devicesList.filter(d => d.includes(q))
})

function onSearch() {
  if (!searchQuery.value.trim()) {
    showSearchResults.value = false
    return
  }
  // 保持下拉打开，用户可点击结果
  showSearchResults.value = true
}

function selectResult(result) {
  // 示例：点击结果弹一个 alert，实际可跳转到设备详情页
  alert(`已选择设备：${result}`)
  showSearchResults.value = false
  searchQuery.value = ''
}

// 通知状态与模拟数据
const unreadCount = ref(3)
const showNotifications = ref(false)
const notifications = ref([
  { text: '客厅空调温度已调至 24℃', time: '10分钟前' },
  { text: '卧室灯光已开启', time: '30分钟前' },
  { text: '厨房冰箱门未关闭', time: '1小时前' }
])

function openNotifications() {
  showNotifications.value = true
  unreadCount.value = 0
}

function closeNotifications() {
  showNotifications.value = false
}

// 角色权限相关
const roleAccess = ref(false)
const showAccessError = ref(false)
// 检查角色权限函数
const checkRoleAccess = () => {
  localStorage.getItem('role') === 'admin' ? roleAccess.value = true : roleAccess.value = false
  if (!roleAccess.value) {
    console.error('当前角色没有权限访问此功能')
    showAccessError.value = true
  } else {
    showAccessError.value = false
  }
  return roleAccess.value
}
// 添加设备按钮需要检查角色权限
const canAddDevice = () => {
  if (!checkRoleAccess()) {
    console.error('没有权限添加设备，当前角色:', localStorage.getItem('role'), '当前value:', roleAccess.value)
    return
  }
  showAddDeviceModal.value = true
}
// 移除设备按钮需要检查角色权限
const canRemoveDevice = () => {
  if (!checkRoleAccess()) {
    console.error('没有权限移除设备，当前角色:', localStorage.getItem('role'), '当前value:', roleAccess.value)
    return
  }
  showRemoveDeviceModal.value = true
}

</script>

<style scoped>
@import './DashboardStyles.css';
</style>
