<!-- src/components/Dashboard.vue -->
<template>
  <div class="dashboard" :class="{ 'mobile-layout': isMobileView }">

    <audio ref="bgMusicRef" autoplay loop preload="auto" style="display: none;">
      <source src="/src/assets/audio/海愿 - 塞壬唱片-MSR、Eagle Wei.mp3" type="audio/mpeg">
      您的浏览器不支持音频播放。
    </audio>

    <!-- 头部 -->
    <header class="header">
      <img src="/src/assets/images/logo.png" alt="Logo" class="header-logo" />
      <h1>智能家居控制中心</h1>

      <button class="view-toggle-btn" @click="toggleViewMode" :title="isMobileView ? '切换到电脑版' : '切换到移动版'">
        <span class="icon">{{ isMobileView ? '💻切换到电脑版' : '📱切换到移动版' }}</span>
      </button>

      <div class="user-info">
        <button @click="toggleMusic" class="music-btn">
          <font-awesome-icon icon="music" class="music-icon" />
          <font-awesome-icon :icon="isPlaying ? 'pause' : 'play'" class="player-icon" />
          <!-- {{ isPlaying ? '暂停音乐' : '播放音乐' }} -->
        </button>


        <button @click="toggleTheme" class="theme-btn">
          <font-awesome-icon :icon="theme === 'light' ? 'moon' : 'sun'" />
          {{ theme === 'light' ? '黑夜模式' : '白天模式' }}
        </button>
        <!-- 头部小头像：只做“打开弹窗” -->
        <div class="user-avatar" @click="showUserModal = true">
          <img :src="user.avatar || '/src/assets/images/user.png'" alt="用户头像" class="avatar-img" />
        </div>
        <!-- <span>当前用户：{{ username }} {{ roleName }}</span> -->
        <button @click="logout" class="logout-btn">退出登录</button>
      </div>

      <!-- 用户信息弹窗 -->
      <div class="user-modal" v-if="showUserModal" @click.self="showUserModal = false">
        <div class="modal-content">
          <div class="modal-header">
            <h3>个人信息</h3>
            <button class="close-btn" @click="showUserModal = false">×</button>
          </div>
          <div class="modal-body">
            <div class="avatar-large">
              <img :src="user.avatar || '/src/assets/images/user.png'" alt="用户头像" @click="onClickAvatar"
                class="avatar-large-img" />
            </div>
            <div class="user-details">
              <div class="detail-item">
                <span class="detail-label">用户名:</span>
                <span class="detail-value">{{ username }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">角色:</span>
                <span class="detail-value">{{ roleName }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">家庭邀请码:</span>
                <span class="detail-value">{{ inviteCode }}</span>
                <button class="copy-btn" @click="copyInviteCode">复制</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </header>

    <!-- 隐藏的文件输入框，用于弹窗中大头像选择 -->
    <input type="file" ref="avatarInput" accept="image/png, image/jpeg" style="display: none;" @change="onFileChange" />



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
      <!-- 右侧列表区域 -->
      <div class="right-column">
        <!-- 设备列表 -->
        <section class="device-list card">
          <div class="device-list-header">
            <h2>设备列表</h2>
            <div class="device-actions">
              <button class="action-btn add-btn" @click="showAddDeviceModal = true">添加设备</button>
              <button class="action-btn remove-btn" @click="showRemoveDeviceModal = true">移除设备</button>
            </div>
          </div>
          <div class="device-table-container" @refresh-devices="fetchAllDevices">
            <div class="device-table">
              <div class="table-header">
                <span>设备ID</span>
                <span>设备名称</span>
                <span>状态</span>
                <span>详情</span>
              </div>
              <div class="table-body">
                <div v-for="device in devices" :key="device.id" class="table-row"
                  :class="{ 'device-on': device.state }">
                  <span>{{ device.id }}</span>
                  <span>{{ device.name }}</span>
                  <span>{{ device.state ? '开启' : '关闭' }}</span>
                  <span>{{ device.details || '-' }}</span>
                </div>
              </div>
              <div class="table-body">
                <div v-for="device in allDevices" :key="device.id" class="table-row"
                  :class="{ 'device-on': device.state }">
                  <span>{{ device.id }}</span>
                  <span>{{ device.name }}</span>
                  <span>{{ device.state ? '开启' : '关闭' }}</span>
                  <span>{{ device.details || '-' }}</span>
                </div>
              </div>
            </div>
          </div>
          <!-- 添加设备弹窗 -->
          <div class="device-modal" v-if="showAddDeviceModal" @click.self="showAddDeviceModal = false">
            <div class="modal-content">
              <h3>添加新设备</h3>
              <div class="form-group">
                <label>设备类型:</label>
                <select v-model="newDevice.type" @change="resetDeviceForm">
                  <option value="airConditioner">空调</option>
                  <option value="light">灯</option>
                </select>
              </div>

              <!-- 通用字段 -->
              <div class="form-group">
                <label>设备名称:</label>
                <input type="text" v-model="newDevice.deviceName" placeholder="例如: 客厅空调/卧室灯">
              </div>
              <div class="form-group">
                <label>设备ID:</label>
                <input type="text" v-model="newDevice.deviceId" placeholder="例如: AC001/LT001">
              </div>
              <div class="form-group">
                <label>初始状态:</label>
                <label class="switch">
                  <input type="checkbox" v-model="newDevice.status"
                    @click="triggerParticleEffect($event, 'add-device-switch')" />
                  <span class="slider round"></span>
                  <span class="switch-label">{{ newDevice.status ? '开启' : '关闭' }}</span>
                  <div class="particle-container" :class="{ active: activeParticle === 'add-device-switch' }">
                    <span class="particle" v-for="n in 8" :key="n" :style="{ '--angle': `${(n - 1) * 45}deg` }"></span>
                  </div>
                </label>
              </div>

              <!-- 空调特有字段 -->
              <template v-if="newDevice.type === 'airConditioner'">
                <div class="form-group">
                  <label>温度(℃):</label>
                  <input type="range" v-model="newDevice.temperature" min="16" max="30" step="0.5">
                  <span class="value-display">{{ newDevice.temperature }}℃</span>
                </div>
                <div class="form-group">
                  <label>模式:</label>
                  <select v-model="newDevice.mode">
                    <option value="cool">制冷</option>
                    <option value="heat">制热</option>
                    <option value="dry">除湿</option>
                    <option value="fan">送风</option>
                  </select>
                </div>
                <div class="form-group">
                  <label>风速等级:</label>
                  <select v-model="newDevice.fanLevel">
                    <option v-for="n in 5" :value="n">{{ n }}档</option>
                  </select>
                </div>
              </template>

              <!-- 灯特有字段 -->
              <template v-if="newDevice.type === 'light'">
                <div class="form-group">
                  <label>亮度(%):</label>
                  <input type="range" v-model="newDevice.brightness" min="0" max="100">
                  <span class="value-display">{{ newDevice.brightness }}%</span>
                </div>
                <div class="form-group">
                  <label>色温:</label>
                  <select v-model="newDevice.colorTemp">
                    <option value="natural">自然</option>
                    <option value="warm">暖光</option>
                    <option value="cool">冷光</option>
                  </select>
                </div>
              </template>

              <div class="modal-actions">
                <button class="cancel-btn" @click="showAddDeviceModal = false">取消</button>
                <button class="confirm-btn" @click="addDevice">确认添加</button>
              </div>
            </div>
          </div>

          <!-- 移除设备弹窗 -->
          <div class="device-modal" v-if="showRemoveDeviceModal" @click.self="showRemoveDeviceModal = false">
            <div class="modal-content">
              <h3>移除设备</h3>
              <div class="form-group">
                <label>选择要移除的设备:</label>
                <select v-model="selectedDeviceToRemove">
                  <option v-for="device in devices" :value="{ deviceId: device.id, deviceName: device.name }">
                    {{ device.name }} ({{ device.id }})
                  </option>
                </select>
              </div>
              <div class="modal-actions">
                <button class="cancel-btn" @click="showRemoveDeviceModal = false">取消</button>
                <button class="confirm-btn" @click="removeDevice" :disabled="!selectedDeviceToRemove">确认移除</button>
              </div>
            </div>
          </div>
        </section>

        <!-- 家庭成员 -->
        <section class="member-list card">
          <h2>家庭成员</h2>
          <div class="member-table-container">
            <div class="member-table">
              <div class="table-body">
                <div v-for="member in familyMembers" :key="member.id" class="member-row"
                  :class="{ admin: member.isAdmin }">
                  <div class="member-info">
                    <span class="member-name">{{ member.name }}</span>
                    <span v-if="member.isAdmin" class="admin-badge">管理员</span>
                    <span class="home-badge" :class="member.isHome ? 'home' : 'away'" @click="toggleHomeStatus(member)">
                      {{ member.isHome ? '在家' : '不在家' }}
                    </span>
                  </div>
                  <div class="member-todos" v-if="member.todos && member.todos.length">
                    <div v-for="(todo, idx) in member.todos" :key="idx" class="todo-item">
                      {{ todo }}
                    </div>
                  </div>
                  <div v-else class="no-todos">暂无待办</div>
                </div>
              </div>
            </div>
          </div>
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
const emit = defineEmits(['refresh-devices'])
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

// 获取网络强度
const getNetworkStrength = () => {
  const networkDevice = quickDevices.value.find(d => d.type === 'network')
  return networkDevice ? networkDevice.signalStrength : 0
}

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

const updateDeviceInfo = ({ id, temperature, mode }) => {
  const idx = devices.value.findIndex(d => d.id === id)
  if (idx !== -1) {
    // 用新对象替换，确保响应式
    devices.value[idx] = {
      ...devices.value[idx],
      details: `${temperature}℃ ${mode === 'cool' ? '制冷模式' : mode === 'heat' ? '制热模式' : ''}`
    }
  }
}

import { ref, onMounted, nextTick } from 'vue'
import { useMusicPlayer } from './DashboardLogic.js'

// 音频相关
const bgMusicRef = ref(null)

const { initMusic, bgMusic } = useMusicPlayer()


onMounted(fetchAllDevices)

const isPlaying = ref(false)

// 修改后的播放/暂停音频函数
const toggleMusic = () => {
  if (bgMusicRef.value) {
    if (isPlaying.value) {
      // 当前正在播放，点击暂停
      bgMusicRef.value.pause()
      isPlaying.value = false
      console.log('音频已暂停')
    } else {
      // 当前暂停中，点击播放
      bgMusicRef.value.volume = 0.2
      bgMusicRef.value.play().then(() => {
        isPlaying.value = true
        console.log('音频播放成功')
      }).catch(err => {
        console.error('音频播放失败:', err)
      })
    }
  } else {
    console.error('音频元素未找到，无法操作')
  }
}

import { watch } from 'vue'

// 用户数据
const showUserModal = ref(false)
const user = ref({
  avatar: '',
  username: username.value,
  // fullName: '张伟', (注册时也没有显示真实姓名，这块感觉没有必要)
  inviteCode: ''
  // registerTime: '2023-05-15 14:30:22' （注册时间显示也没有必要）
})

// 仅在弹窗中点击大头像才触发
const avatarInput = ref(null)

const AVATAR_KEY_PREFIX = 'dashboard_user_avatar_'  // 键名前缀

// 点击弹窗中大头像时触发文件选择
const onClickAvatar = () => {
  if (avatarInput.value) {
    avatarInput.value.click()
  }
}

// 用户选完新头像后保存到 localStorage（键名带当前用户名前缀）
const onFileChange = (e) => {
  const file = e.target.files[0]
  if (!file) return
  if (!file.type.match(/^image\/(png|jpeg)$/) || file.size > 2 * 1024 * 1024) {
    alert('请上传 JPG/PNG 且小于 2MB 的图片')
    return
  }
  const reader = new FileReader()
  reader.onload = (ev) => {
    const base64Data = ev.target.result
    user.value.avatar = base64Data
    // 存到localStorage
    const key = AVATAR_KEY_PREFIX + username.value
    localStorage.setItem(key, base64Data)
  }
  reader.readAsDataURL(file)
}

// 监视 username 变化：包括页面首次挂载和后续登录/登出导致 username 改变
watch(
  username,
  (newUsername) => {
    // 如果 newUsername 为空或未定义，直接清空 avatar，让页面显示默认头像
    if (!newUsername) {
      user.value.avatar = ''
      user.value.username = ''  // modal 里用户名也置空
      return
    }
    // 读取key
    const key = AVATAR_KEY_PREFIX + newUsername
    const saved = localStorage.getItem(key)
    if (saved) {
      user.value.avatar = saved
    } else {
      user.value.avatar = ''  // 没找到，显示默认
    }
    user.value.username = newUsername
  },
  {
    immediate: true,
  }
)

const doLogout = () => {
  logout()
}

// 复制邀请码函数
const copyInviteCode = () => {
  navigator.clipboard.writeText(user.value.inviteCode)
    .then(() => {
      alert('邀请码已复制到剪贴板')
    })
    .catch(err => {
      console.error('复制失败:', err)
    })
}

// 设备管理相关状态
const showAddDeviceModal = ref(false)
const showRemoveDeviceModal = ref(false)
const selectedDeviceToRemove = ref('')
const newDevice = ref({
  type: 'airConditioner',
  deviceName: '',
  deviceId: '',
  status: true,
  // 空调参数
  temperature: 24,
  mode: 'cool',
  fanLevel: 3,
  // 灯参数
  brightness: 80,
  colorTemp: 'natural'
})

// 重置设备表单
const resetDeviceForm = () => {
  newDevice.value = {
    type: newDevice.value.type,
    deviceName: '',
    deviceId: '',
    status: true,
    temperature: 24,
    mode: 'cool',
    fanLevel: 3,
    brightness: 80,
    colorTemp: 'natural'
  }
}

// 添加设备
const addDevice = () => {
  const device = { ...newDevice.value }
  if (device.type === 'airConditioner') {
    deviceList.value.push({
      id: device.deviceId,
      name: device.deviceName,
      status: device.status ? '开启' : '关闭',
      temperature: `${device.temperature}℃`,
      mode: {
        cool: '制冷',
        heat: '制热',
        dry: '除湿',
        fan: '送风'
      }[device.mode],
      fanLevel: `${device.fanLevel}档`
    })
  } else {
    deviceList.value.push({
      id: device.deviceId,
      name: device.deviceName,
      status: device.status ? '开启' : '关闭',
      brightness: `${device.brightness}%`,
      colorTemp: {
        natural: '自然',
        warm: '暖光',
        cool: '冷光'
      }[device.colorTemp]
    })
  }
  showAddDeviceModal.value = false
  resetDeviceForm()
}

// 移除设备
const removeDevice = async () => {
  if (!selectedDeviceToRemove.value || !selectedDeviceToRemove.value.deviceName) {
    console.error('未选择设备或设备名称为空');
    return;
  }

  try {
    const token = localStorage.getItem('authToken');
    const res = await axios.post(`/device/deleteByDeviceName`, null, {
      params: { deviceName: selectedDeviceToRemove.value.deviceName },
      headers: {
        'Content-Type': 'application/json',
        'Authorization': token
      }
    });

    if (res.data.status === 200 && res.data.data === true) {
      console.log('设备删除成功:', selectedDeviceToRemove.value.deviceName);

      // 更新前端设备列表
      devices.value = devices.value.filter(device => device.id !== selectedDeviceToRemove.value.deviceId);

      // 触发设备刷新事件
      emit('refresh-devices');

      // 清除选择并关闭弹窗
      selectedDeviceToRemove.value = null;
      showRemoveDeviceModal.value = false;
    } else {
      console.error('设备删除失败:', res.data.msg);
    }
  } catch (err) {
    console.error('删除设备请求异常:', err);
  }
};

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
  customScenes.value = customScenes.value.filter(scene => scene.id !== sceneId)
}

// 确认删除
const confirmDeleteScene = () => {
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

const toggleViewMode = () => {
  isMobileView.value = !isMobileView.value
  // 可选：保存到本地存储
  localStorage.setItem('preferredView', isMobileView.value ? 'mobile' : 'desktop')
}

// 可选：初始化时读取本地存储偏好
onMounted(() => {
  const savedView = localStorage.getItem('preferredView')
  if (savedView) {
    isMobileView.value = savedView === 'mobile'
  }
})

</script>

<style scoped>
@import './DashboardStyles.css';
</style>
