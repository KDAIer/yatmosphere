<!-- src/components/Dashboard.vue -->
<template>
  <div class="dashboard">
    <!-- 头部 -->
    <header class="header">
      <img src="/src/assets/images/logo.png" alt="Logo" class="header-logo" />
      <h1>智能家居控制中心</h1>
      <div class="user-info">
        <button @click="toggleTheme" class="theme-btn">
          <font-awesome-icon :icon="theme === 'light' ? 'moon' : 'sun'" />
          {{ theme === 'light' ? '黑夜模式' : '白天模式' }}
        </button>
        <span>当前用户：{{ username }} </span>
        <button @click="logout" class="logout-btn">退出登录</button>
      </div>
    </header>

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
            <div class="device-card" v-for="(device, index) in quickDevices" :key="device.id" :class="{ 'device-on': device.state }">
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
                  <input
                    type="checkbox"
                    :checked="device.state"
                    @change="handleDeviceAction(device)"
                    @click="triggerParticleEffect($event, device.id)"
                    :disabled="device.type === 'emergency'"
                  />
                  <span class="slider"></span>
                  <div class="particle-container" :class="{ active: activeParticle === device.id }">
                    <span class="particle" v-for="n in 8" :key="n" :style="{ '--angle': `${(n - 1) * 45}deg` }"></span>
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
              <div
                class="grid-area-item"
                v-for="(area, index) in areas"
                :key="index"
                @click="toggleArea(area.id)"
                :class="{ active: activeArea === area.id, 'has-sub': area.children }"
              >
                <!-- <font-awesome-icon :icon="area.icon" class="area-icon" /> -->
                 <img :src="area.icon" class="area-icon" alt="area icon" />
                <span class="area-name">{{ area.name }}</span>
              </div>
            </div>
            <div class="area-details" v-if="activeArea" @click.self="activeArea = null">
              <component
                :is="getAreaComponent(activeArea)"
                @close="activeArea = null"
                class="modal-content"
              />
            </div>
          </section>
        </div>

        <!-- 第二行 -->
        <div class="grid-row">
          <!-- 环境监测 -->
          <section class="environment card">
            <h2>环境监测</h2>
            <div class="data-grid">
              <template v-for="(data, key) in environmentData" :key="key">
                <div
                  class="data-card"
                  :class="{ 'time-card': key === 'time' }"
                  v-if="key === 'time'"
                >
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
            <h2>智能场景</h2>
            <div class="scene-grid">
              <button
                v-for="scene in scenes"
                :key="scene.id"
                class="scene-btn"
                @click="activateScene(scene.id)"
              >
                <span class="scene-icon">{{ scene.icon }}</span>
                <span class="scene-name">{{ scene.name }}</span>
              </button>
            </div>
          </section>
      
        </div>
      </div>
      <!-- 右侧列表区域 -->
      <div class="right-column">
        <!-- 设备列表 -->
        <section class="device-list card">
          <h2>设备列表</h2>
          <div class="device-table-container">
            <div class="device-table">
              <div class="table-header">
                <span>设备ID</span>
                <span>设备名称</span>
                <span>状态</span>
                <span>详情</span>
              </div>
              <div class="table-body">
                <div
                  v-for="device in allDevices"
                  :key="device.id"
                  class="table-row"
                  :class="{ 'device-on': device.state }"
                >
                  <span>{{ device.id }}</span>
                  <span>{{ device.name }}</span>
                  <span>{{ device.state ? '开启' : '关闭' }}</span>
                  <span>{{ device.details || '-' }}</span>
                </div>
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
                <div
                  v-for="member in familyMembers"
                  :key="member.id"
                  class="member-row"
                  :class="{ admin: member.isAdmin }"
                >
                  <div class="member-info">
                    <span class="member-name">{{ member.name }}</span>
                    <span v-if="member.isAdmin" class="admin-badge">管理员</span>
                    <span
                      class="home-badge"
                      :class="member.isHome ? 'home' : 'away'"
                      @click="toggleHomeStatus(member)"
                    >
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
import {
  theme,
  toggleTheme,
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

</script>

<style scoped>
@import './DashboardStyles.css';
</style>