<!-- src/components/Dashboard.vue -->
<template>
  <div class="dashboard" :class="{ 'mobile-layout': isMobileView }">
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
      <!-- 右侧列表区域 -->
      <div class="right-column">
        <!-- 设备列表 -->
        <section class="device-list card">
          <div class="device-list-header">
            <h2>设备列表</h2>
            <div class="device-actions">
              <button class="action-btn add-btn" @click="canAddDevice">添加设备</button>
              <button class="action-btn remove-btn" @click="canRemoveDevice">移除设备</button>
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
              <h3>添加新设备(请在弹窗最下方确认添加)</h3>
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
                  <br>
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
                  <br>
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
      </div>

      <!--权限错误弹窗-->
      <div class="access-error-modal" v-if="showAccessError" @click.self="showAccessError = false">
        <div class="modal-content">
          <h3 class="error-title"><span style="color: red">⚠</span>权限错误</h3>
          <p class="error-message">&nbsp;您没有权限使用此功能，请联系管理员&nbsp;</p>
        </div>
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
import { watch } from 'vue'
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


// 设备管理相关逻辑
onMounted(fetchAllDevices)
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
// 添加设备
const addDevice = async () => {
  const device = { ...newDevice.value };
  const token = localStorage.getItem('authToken');
  let apiUrl = '';
  let payload = {};

  if (device.type === 'airConditioner') {
    apiUrl = '/aircon/add';
    payload = {
      deviceName: device.deviceName,
      deviceId: device.deviceId,
      category: '空调',
      status: device.status,
      detail: `${device.temperature}℃ ${device.mode === 'cool' ? '制冷模式' : device.mode === 'heat' ? '制热模式' : ''}`,
      temperature: device.temperature,
      mode: {
        cool: '制冷',
        heat: '制热',
        dry: '除湿',
        fan: '送风'
      }[device.mode],
      fanLevel: device.fanLevel,
      timer: 0
    };
  } else if (device.type === 'light') {  
    apiUrl = '/light/add';
    payload = {
      deviceName: device.deviceName,
      deviceId: device.deviceId,
      category: '灯',
      status: device.status,
      detail: {
        natural: '自然',
        warm: '暖光',
        cool: '冷光'
      }[device.colorTemp],
      colorTemp: device.colorTemp,
      brightness: device.brightness
    };
  }

  try {
    const res = await axios.post(apiUrl, payload, {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': token
      }
    });

    if (res.data.status === 200 && (res.data.data === true||res.data.data.success== true)) {
      console.log('设备添加成功:', device.deviceName);

      // 将新设备加入到前端的 devices 列表中
      devices.value.push({
        id: device.deviceId,
        name: device.deviceName,
        category: device.type === 'airConditioner' ? '空调' : '灯',
        state: device.status,
        details: device.type === 'airConditioner'
          ? `${device.temperature}℃ ${device.mode === 'cool' ? '制冷模式' : device.mode === 'heat' ? '制热模式' : ''}`
          : `${device.brightness}% ${device.colorTemp === 'natural' ? '自然' : device.colorTemp === 'warm' ? '暖光' : '冷光'}`
      });

      // `${device.brightness}% ${device.colorTemp === 'natural' ? '自然' : device.colorTemp === 'warm' ? '暖光' : '冷光'}`

      fetchAllDevices(); // 刷新设备列表
      showAddDeviceModal.value = false;
      resetDeviceForm();
    } else {
      console.error('设备添加失败:', res.data.data.message);
    }
  } catch (err) {
    console.error('添加设备请求异常:', err);
  }
};

// 移除设备
const removeDevice = async () => {
  if (!selectedDeviceToRemove.value || !selectedDeviceToRemove.value.deviceName) {
    console.error('未选择设备或设备名称为空');
    return;
  }

  try {
    const token = localStorage.getItem('authToken');
    const res = await axios.post(`/device/deleteByDeviceId`, null, {
      params: { deviceId: selectedDeviceToRemove.value.deviceId },
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
