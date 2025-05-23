<template>
  <div class="kitchen-panel">
    <div class="panel-header">
      <h2>智慧厨房控制系统</h2>
      <button class="close-btn" @click="$emit('close')">×</button>
    </div>

    <div class="control-section">
      <!-- 智能冰箱 -->
      <div class="device-card expanded">
        <div class="device-header">
          <font-awesome-icon icon="snowflake" class="device-icon" />
          <h3>智能冰箱</h3>
          <div class="status-indicator" :class="{ active: fridge.status }"></div>
        </div>
        <div class="device-details">
          <!-- 温度控制 -->
          <div class="temp-control">
            <div class="temp-setter">
              <div class="temp-header">
                <font-awesome-icon icon="thermometer-half" class="temp-icon" />
                <label>冷藏室 (°C)</label>
              </div>
              <div class="slider-container">
                <input type="range" min="2" max="8" v-model="fridge.tempFresh" 
                      @input="updateFridgeTemp('fresh')" class="temp-slider">
                <span class="temp-value">{{ fridge.tempFresh }}°C</span>
              </div>
            </div>
            <div class="temp-setter">
              <div class="temp-header">
                <font-awesome-icon icon="icicles" class="temp-icon" />
                <label>冷冻室 (°C)</label>
              </div>
              <div class="slider-container">
                <input type="range" min="-24" max="-14" v-model="fridge.tempFrozen" 
                      @input="updateFridgeTemp('frozen')" class="temp-slider">
                <span class="temp-value">{{ fridge.tempFrozen }}°C</span>
              </div>
            </div>
          </div>

          <!-- 库存管理 -->
          <div class="inventory-management">
            <h4 class="section-title">
              <font-awesome-icon icon="box-open" class="section-icon" />
              食材管理
            </h4>
            <div class="inventory-list">
              <!-- 修改库存管理部分 -->
              <div class="inventory-item" v-for="(item, index) in fridge.inventory" :key="index">
                <div class="item-info">
                  <font-awesome-icon :icon="getFoodIcon(item.name)" class="food-icon" />
                  <span class="item-name">{{ item.name }}</span>
                </div>
                <div class="item-controls">
                  <div class="expiry-indicator" :class="getExpiryClass(item.expiryDays)">
                    {{ item.expiryDays }}天
                  </div>
                  <button class="delete-btn" @click.stop="removeItem(index)">
                    <font-awesome-icon icon="trash" />
                    <span class="delete-label">删除</span>
                  </button>
                </div>
              </div>
            </div>
            <button class="action-btn" @click="showAddModal = true">
              <font-awesome-icon icon="plus-circle" /> 添加食材
            </button>
          </div>

          <!-- 添加食材模态框 -->
          <div class="modal-overlay" v-if="showAddModal">
            <div class="inventory-modal">
              <h3>添加新食材</h3>
              <div class="modal-form">
                <div class="form-group">
                  <label>食材名称</label>
                  <input type="text" v-model="newItem.name" placeholder="请输入食材名称">
                </div>
                <div class="form-group">
                  <label>保质期（天）</label>
                  <input type="number" v-model.number="newItem.expiryDays" min="1" max="30">
                </div>
                <div class="modal-buttons">
                  <button class="cancel-btn" @click="cancelAdd">取消</button>
                  <button class="confirm-btn" @click="addItem">确认添加</button>
                </div>
              </div>
            </div>
          </div>

          <!-- 节能模式 -->
          <div class="energy-saving">
            <label class="switch">
              <input type="checkbox" v-model="fridge.energySaving">
              <span class="slider"></span>
            </label>
            <span class="energy-label">
              <font-awesome-icon icon="leaf" /> 节能模式
            </span>
          </div>
        </div>
      </div>

      <!-- 智能烤箱 -->
      <!-- 在template部分修改智能烤箱区块 -->
      <div class="device-card expanded">
        <div class="device-header">
          <font-awesome-icon :icon="['fas', 'fire']" class="device-icon" />
          <h3>智能烤箱</h3>
          <div class="cooking-status" :class="{ active: oven.status }">
            {{ oven.status ? '工作中' : '待机中' }}
          </div>
        </div>
        <div class="device-details">
          <div class="oven-controls">
            <!-- 模式选择 -->
            <div class="mode-grid">
              <div 
                v-for="mode in ovenModes"
                :key="mode"
                class="mode-card"
                :class="{ active: oven.mode === mode }"
                @click="oven.mode = mode"
              >
                <font-awesome-icon :icon="getOvenIcon(mode)" />
                <span>{{ mode }}</span>
              </div>
            </div>

            <!-- 温度时间控制 -->
            <div class="temp-time-card">
              <div class="control-group">
                <div class="input-field">
                  <label>温度设定</label>
                  <div class="number-input">
                    <button @click="oven.temperature > 50 ? oven.temperature -=5 : ''">-</button>
                    <input type="number" v-model="oven.temperature" min="50" max="250">
                    <span>°C</span>
                    <button @click="oven.temperature < 250 ? oven.temperature +=5 : ''">+</button>
                  </div>
                </div>
                <div class="input-field">
                  <label>时间设定</label>
                  <div class="number-input">
                    <button @click="oven.timer > 1 ? oven.timer -=1 : ''">-</button>
                    <input type="number" v-model="oven.timer" min="1" max="120">
                    <span>分钟</span>
                    <button @click="oven.timer < 120 ? oven.timer +=1 : ''">+</button>
                  </div>
                </div>
              </div>

              <!-- 预设菜谱 -->
              <div class="recipe-preset">
                <h4>快捷菜谱</h4>
                <div class="recipe-buttons">
                  <button
                    v-for="(recipe, name) in recipes"
                    :key="name"
                    @click="applyRecipe(name)"
                    :class="{ active: selectedRecipe === name }"
                  >
                    {{ name }}
                    <span class="recipe-info">{{ recipe.temp }}°C {{ recipe.time }}分钟</span>
                  </button>
                </div>
              </div>
            </div>

            <!-- 控制按钮 -->
            <button 
              class="start-btn"
              @click="startOven"
              :disabled="!oven.temperature || !oven.timer"
            >
              <font-awesome-icon :icon="oven.status ? 'stop' : 'play'" />
              {{ oven.status ? '停止烹饪' : '开始烹饪' }}
            </button>
          </div>
        </div>
      </div>

      <!-- 洗碗机 -->
      <!-- 修改template中的洗碗机部分 -->
      <div class="device-card expanded">
        <div class="device-header">
          <font-awesome-icon icon="spray-can" class="device-icon" />
          <h3>洗碗机</h3>
          <div class="progress-circle" 
              :style="{ '--progress': dishwasher.progress }"
              v-if="dishwasher.status">
            {{ Math.round(dishwasher.progress) }}%
          </div>
        </div>
        <div class="device-details">
          <div class="wash-controls">
            <div class="mode-grid">
              <div 
                v-for="mode in washModes"
                :key="mode.name"
                class="mode-card"
                :class="{ active: dishwasher.mode === mode.name }"
                @click="selectWashMode(mode.name)"
              >
                <h4>{{ mode.name }}</h4>
                <p>{{ mode.duration }}分钟</p>
                <p>用水 {{ mode.water }}升</p>
              </div>
            </div>
            <div class="supply-status">
              <div class="supply-item" :class="{ low: dishwasher.salt < 20 }">
                <font-awesome-icon icon="jar" /> 软水盐: {{ dishwasher.salt }}%
              </div>
              <div class="supply-item" :class="{ low: dishwasher.detergent < 10 }">
                <font-awesome-icon icon="flask" /> 洗碗剂: {{ dishwasher.detergent }}%
              </div>
            </div>
            <button 
              class="start-btn"
              @click="toggleDishwasher"
              :disabled="dishwasher.salt < 10 || dishwasher.detergent < 5"
            >
              {{ dishwasher.status ? '暂停清洗' : '开始清洗' }}
            </button>
          </div>
        </div>
      </div>

      <!-- 抽油烟机 -->
      <div class="device-card expanded">
        <div class="device-header">
          <font-awesome-icon icon="fan" class="device-icon" />
          <h3>智能抽油烟机</h3>
          <div class="status-badge" :class="speedClass">
            {{ rangeFan.speedLevels[rangeFan.currentSpeed] }}
          </div>
        </div>
        <div class="device-details">
          <div class="fan-controls">
            <!-- 风速控制 -->
            <div class="speed-control">
              <h4 class="section-title">风速调节</h4>
              <div class="speed-grid">
                <button 
                  v-for="(speed, index) in rangeFan.speedLevels" 
                  :key="index"
                  :class="{ active: rangeFan.currentSpeed === index }"
                  @click="rangeFan.currentSpeed = index"
                  class="speed-btn"
                >
                  <font-awesome-icon 
                    :icon="speedIcons[index]" 
                    class="speed-icon" 
                  />
                  <span class="speed-label">{{ speed }}</span>
                  <div 
                    class="speed-indicator" 
                    :style="{ height: (index + 1) * 20 + '%' }"
                  ></div>
                </button>
              </div>
            </div>

            <!-- 智能模式 -->
            <div class="smart-control">
              <div class="control-item">
                <font-awesome-icon icon="robot" class="control-icon" />
                <div class="control-content">
                  <label>智能感应模式</label>
                  <div class="switch-wrapper">
                    <label class="switch">
                      <input type="checkbox" v-model="rangeFan.autoMode">
                      <span class="slider"></span>
                    </label>
                    <span class="mode-status">
                      {{ rangeFan.autoMode ? '已开启' : '已关闭' }}
                    </span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 滤网状态 -->
            <div class="filter-status">
              <div class="status-card">
                <div class="status-header">
                  <font-awesome-icon icon="filter" class="status-icon" />
                  <h5>滤网寿命</h5>
                </div>
                <div class="progress-ring">
                  <svg width="80" height="80">
                    <circle 
                      class="progress-bg" 
                      cx="40" 
                      cy="40" 
                      r="35"
                    />
                    <circle 
                      class="progress-bar" 
                      cx="40" 
                      cy="40" 
                      r="35"
                      :stroke-dasharray="`${circumference} ${circumference}`"
                      :stroke-dashoffset="strokeOffset"
                    />
                  </svg>
                  <div class="progress-text">
                    {{ rangeFan.filterLife }}%
                  </div>
                </div>
                <button 
                  class="maintenance-btn"
                  @click="resetFilterLife"
                >
                  <font-awesome-icon icon="sync-alt" />
                  重置滤网
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'

// 确保已导入trash图标（在图标导入部分补充）
import { 
  // 其他图标...
  faTrash 
} from '@fortawesome/free-solid-svg-icons'

// 智能冰箱
const fridge = reactive({
  status: true,
  tempFresh: 4,
  tempFrozen: -18,
  energySaving: false,
  inventory: [
    { name: '鲜牛奶', expiryDays: 3 },
    { name: '牛肉', expiryDays: 5 },
    { name: '鸡蛋', expiryDays: 7 }
  ]
})

// 智能烤箱
const oven = reactive({
  status: false,
  mode: '上下火',
  temperature: 180,
  timer: 30,
  remainingTime: 0
})

// 洗碗机
// 修改script中的洗碗机相关代码
// 洗碗机
const dishwasher = reactive({
  status: false,
  mode: '快速洗',  // 设置默认模式
  progress: 0,
  salt: 45,
  detergent: 15
})

// 洗碗模式（修改为三种模式）
const washModes = [
  { name: '快速洗', duration: 30, water: 8 },
  { name: '标准洗', duration: 60, water: 12 },
  { name: '节能洗', duration: 90, water: 6 }
]

// 新增模式选择方法
const selectWashMode = (modeName) => {
  dishwasher.mode = modeName
}

// 修改洗碗机开关逻辑
const toggleDishwasher = () => {
  if (dishwasher.salt < 10 || dishwasher.detergent < 5) {
    alert('耗材不足，请补充后操作！')
    return
  }
  
  dishwasher.status = !dishwasher.status
  if (dishwasher.status) {
    const selectedMode = washModes.find(m => m.name === dishwasher.mode)
    const totalSeconds = selectedMode.duration * 60
    
    const interval = setInterval(() => {
      if (dishwasher.progress < 100) {
        dishwasher.progress += 100 / totalSeconds
        // 实时消耗耗材
        dishwasher.salt = Math.max(0, dishwasher.salt - 0.02)
        dishwasher.detergent = Math.max(0, dishwasher.detergent - 0.05)
      } else {
        clearInterval(interval)
        dishwasher.status = false
        dishwasher.progress = 0
      }
    }, 1000)
  }
}

// 预设菜谱
const recipes = {
  '烤鸡翅': { temp: 200, time: 25 },
  '披萨': { temp: 220, time: 15 },
  '蛋糕': { temp: 170, time: 40 }
}


const showAddModal = ref(false)
const newItem = ref({ name: '', expiryDays: 7 })

const foodIcons = {
  '牛奶': 'mug-hot',
  '鲜牛奶': 'mug-hot',
  '牛肉': 'drumstick-bite',
  '鸡蛋': 'egg',
  '蔬菜': 'leaf',
  '水果': 'apple-alt',
  '鱼类': 'fish',
  '面包': 'bread-slice'
}


// 在script部分新增/修改代码
// 烤箱模式图标映射
const ovenIcons = {
  '上下火': 'arrows-up-down',
  '上火热风': 'arrow-up',
  '下火': 'arrow-down',
  '烘焙': 'cookie'
}

// 获取烤箱模式图标
const getOvenIcon = (mode) => {
  return ovenIcons[mode] || 'fire'
}

// 应用菜谱方法修改
const applyRecipe = (name) => {
  if (recipes[name]) {
    selectedRecipe.value = name
    oven.temperature = recipes[name].temp
    oven.timer = recipes[name].time
  }
}

// 预设菜谱列表
const ovenModes = ref(['上下火', '上火热风', '下火', '烘焙'])
const selectedRecipe = ref('')

const getFoodIcon = (name) => {
  const match = Object.entries(foodIcons).find(([key]) => name.includes(key))
  return match ? match[1] : 'shopping-basket'
}

const getExpiryClass = (days) => {
  if (days <= 1) return 'expired'
  if (days <= 3) return 'urgent'
  return 'normal'
}

const updateFridgeTemp = (type) => {
  if (type === 'fresh') {
    fridge.tempFrozen = Math.min(fridge.tempFrozen, fridge.tempFresh - 14)
  }
}

const addItem = () => {
  if (newItem.value.name && newItem.value.expiryDays > 0) {
    fridge.inventory.push({ ...newItem.value })
    newItem.value = { name: '', expiryDays: 7 }
    showAddModal.value = false
  }
}

const cancelAdd = () => {
  newItem.value = { name: '', expiryDays: 7 }
  showAddModal.value = false
}

// 在现有方法后添加删除方法
const removeItem = (index) => {
  if (confirm('确定要删除这个食材吗？')) {
    fridge.inventory.splice(index, 1)
  }
}

const startOven = () => {
  oven.status = !oven.status
  if (oven.status) {
    oven.remainingTime = oven.timer * 60
    const interval = setInterval(() => {
      if (oven.remainingTime > 0) {
        oven.remainingTime--
      } else {
        clearInterval(interval)
        oven.status = false
      }
    }, 1000)
  }
}

// 抽油烟机数据
const rangeFan = reactive({
  currentSpeed: 0,
  speedLevels: ['关闭', '一档', '二档', '三档'],
  autoMode: true,
  filterLife: 65
})

// 风速图标
const speedIcons = [
  'power-off',    // 关闭
  'wind',         // 一档
  'tachometer-alt', // 二档
  'hurricane'     // 三档
]

// 滤网进度计算
const circumference = computed(() => 2 * Math.PI * 35)
const strokeOffset = computed(() => {
  return circumference.value * (1 - rangeFan.filterLife / 100)
})

// 当前风速样式类
const speedClass = computed(() => `speed-${rangeFan.currentSpeed}`)

// 重置滤网寿命
const resetFilterLife = () => {
  rangeFan.filterLife = 100
}

const formatTime = (seconds) => {
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins}:${secs.toString().padStart(2, '0')}`
}
</script>

<style scoped>

/* 修改外层容器样式 */
.kitchen-panel {
  width: 100vw; /* 改为视口宽度 */
  max-width: 600px; /* 增加最大宽度限制 */
  height: 100vh; /* 使用视口高度 */
  padding: 1.2rem;
  background-color: #f9f9f9;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  overflow-y: auto; /* 启用垂直滚动 */
  display: flex;
  flex-direction: column; /* 垂直布局 */
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.2rem;
  padding-bottom: 0.8rem;
  border-bottom: 1px solid #e0e0e0;
}

.panel-header h2 {
  font-size: 1.3rem;
  margin: 0;
  color: #37474f;
}

.close-btn {
  font-size: 2.5rem;
  line-height: 1;
  padding: 0 0.6rem;
  background: none;
  border: none;
  cursor: pointer;
  color: #666;
  transition: all 0.2s;
  border-radius: 50%;
}

.close-btn:hover {
  color: #ce3c31;
  background: #f0f0f0;
}

/* 调整控制区域 */
.control-section {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  min-width: 0; /* 修复弹性容器溢出问题 */
}

/* 修改所有设备卡片 */
.device-card.expanded {
  width: 100%;
  min-width: 0; /* 重要：修复内容溢出 */
  margin-bottom: 1rem;
}

.device-grid,
.sensor-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 1rem;
}

.device-card,
.sensor-card {
  background-color: #fff;
  padding: 1rem;
  border-radius: 12px;
  text-align: center;
  border: 1px solid #e0e0e0;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  transition: all 0.2s;
}

.device-card:hover,
.sensor-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.device-icon,
.sensor-icon {
  font-size: 1.6rem;
  color: #607d8b;
  margin-bottom: 0.6rem;
}

.device-name,
.sensor-name {
  font-size: 0.9rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
}

.toggle-btn {
  margin-top: 0.6rem;
  background-color: #b0bec5;
  color: white;
  border: none;
  padding: 0.4rem 0.8rem;
  font-size: 0.8rem;
  border-radius: 15px;
  cursor: pointer;
  transition: all 0.3s;
  width: 80%;
}

.toggle-btn.active {
  background-color: #4caf50;
}

.toggle-btn:hover {
  opacity: 0.9;
}

.stock-status {
  font-size: 0.8rem;
  margin: 0.5rem 0;
  color: #444;
}

.low-stock {
  color: #d32f2f;
  font-weight: bold;
}

.recipe-select button {
  margin: 0.5rem 0;
  background-color: #fffde7;
  border: 1px solid #ffb300;
  padding: 0.4rem 0.8rem;
  font-size: 0.8rem;
  border-radius: 12px;
  color: #f57f17;
  cursor: pointer;
  transition: all 0.2s;
}

.recipe-select button:hover {
  background-color: #fff8e1;
}

.sensor-card.alert {
  background-color: #ffebee;
  border: 1px solid #e53935;
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.02);
  }
  100% {
    transform: scale(1);
  }
}

.sensor-status {
  font-size: 0.8rem;
}

.alert-text {
  color: #e53935;
  font-weight: bold;
}

.device-card.expanded {
  margin-bottom: 1.5rem;
  padding: 1.2rem;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.device-header {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  margin-bottom: 1rem;
}

.device-details {
  padding: 0.8rem;
  border-top: 1px solid #eee;
}

/* 温度控制 */
.temp-control {
  display: grid;
  gap: 1.5rem;
  padding: 1rem;
  background: #f5f9ff;
  border-radius: 12px;
  margin-bottom: 1.5rem;
}

.temp-setter {
  position: relative;
}

.temp-header {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  margin-bottom: 1rem;
}

.temp-icon {
  font-size: 1.2rem;
  color: #4a90e2;
}

.slider-container {
  position: relative;
  display: grid;
  align-items: center;
}

.temp-slider {
  width: 100%;
  height: 6px;
  border-radius: 3px;
  background: #ddd;
  outline: none;
  -webkit-appearance: none;
}

.temp-slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  width: 20px;
  height: 20px;
  background: #4a90e2;
  border-radius: 50%;
  cursor: pointer;
}

.temp-value {
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  background: #fff;
  padding: 0.2rem 0.8rem;
  border-radius: 15px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  font-weight: 600;
  color: #4a90e2;
}

/* 库存管理 */
.inventory-management {
  border: 1px solid #e8f4ff;
  border-radius: 12px;
  padding: 1rem;
  margin-bottom: 1.5rem;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  color: #37474f;
  margin: 0 0 1rem;
  font-size: 1.1rem;
}

.section-icon {
  color: #7f8c8d;
}

.inventory-list {
  margin-bottom: 1rem;
}

.inventory-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.8rem;
  background: #f8faff;
  border-radius: 8px;
  margin-bottom: 0.5rem;
}

.item-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.food-icon {
  color: #7f8c8d;
  width: 20px;
}

.item-name {
  font-weight: 500;
}

.expiry-indicator {
  padding: 0.3rem 0.8rem;
  border-radius: 15px;
  font-size: 0.9rem;
  font-weight: 500;
  min-width: 70px;
  text-align: center;
}

.expiry-indicator.normal {
  background: #e8f5e9;
  color: #2e7d32;
}

.expiry-indicator.urgent {
  background: #fff3e0;
  color: #ef6c00;
}

.expiry-indicator.expired {
  background: #ffebee;
  color: #c62828;
}

/* 添加食材模态框 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.inventory-modal {
  background: white;
  padding: 2rem;
  border-radius: 12px;
  width: 400px;
  max-width: 90%;
}

.modal-form {
  display: grid;
  gap: 1.5rem;
}

.form-group {
  display: grid;
  gap: 0.5rem;
}

.form-group input {
  padding: 0.8rem;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 1rem;
}

.modal-buttons {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 1rem;
}

.cancel-btn, .confirm-btn {
  padding: 0.8rem 1.5rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

.cancel-btn {
  background: #f0f0f0;
  color: #666;
}

.confirm-btn {
  background: #4a90e2;
  color: white;
}

/* 节能模式 */
.energy-saving {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: #f5f9ff;
  border-radius: 8px;
}

.energy-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 500;
  color: #4a90e2;
}

.oven-controls {
  display: grid;
  gap: 1rem;
}

.mode-selector {
  display: flex;
  gap: 0.5rem;
  button {
    flex: 1;
    padding: 0.5rem;
    border: 1px solid #ddd;
    border-radius: 8px;
    &.active {
      background: #fff3e0;
      border-color: #ffb300;
    }
  }
}

.progress-circle {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: conic-gradient(#4caf50 var(--progress), #eee 0);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}

.speed-selector {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 0.5rem;
  button {
    padding: 0.6rem;
    border: 1px solid #ddd;
    border-radius: 8px;
    &.active {
      background: #e3f2fd;
      border-color: #2196f3;
    }
  }
}

.life-bar {
  height: 4px;
  background: #4caf50;
  margin-top: 0.3rem;
  border-radius: 2px;
}

.switch {
  position: relative;
  display: inline-block;
  width: 40px;
  height: 24px;
  input {
    opacity: 0;
    width: 0;
    height: 0;
    &:checked + .slider {
      background-color: #4caf50;
    }
    &:checked + .slider:before {
      transform: translateX(16px);
    }
  }
  .slider {
    position: absolute;
    cursor: pointer;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: #ccc;
    transition: .4s;
    border-radius: 12px;
    &:before {
      position: absolute;
      content: "";
      height: 16px;
      width: 16px;
      left: 4px;
      bottom: 4px;
      background-color: white;
      transition: .4s;
      border-radius: 50%;
    }
  }
}

.start-btn {
  background: #4caf50;
  color: white;
  padding: 0.8rem 1.5rem;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  &:disabled {
    background: #bdbdbd;
    cursor: not-allowed;
  }
}

/* 新增删除按钮样式 */
.item-controls {
  display: flex;
  align-items: center;
  gap: 12px;
}

.delete-btn {
  background: none;
  border: none;
  color: #e74c3c;
  cursor: pointer;
  padding: 6px;
  border-radius: 50%;
  transition: all 0.2s;
}

.delete-btn:hover {
  background: #ffeeed;
  transform: scale(1.1);
}

.delete-btn svg {
  width: 16px;
  height: 16px;
}

/* 调整现有样式保持布局 */
.inventory-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.8rem;
  background: #f8faff;
  border-radius: 8px;
  margin-bottom: 0.5rem;
  transition: all 0.2s;
}

.inventory-item:hover {
  transform: translateX(5px);
  box-shadow: 0 2px 6px rgba(0,0,0,0.1);
}

/* 在style部分新增烤箱样式 */
/* 烤箱主容器 */
.oven-controls {
  display: grid;
  gap: 1.5rem;
  padding: 1rem;
}

/* 模式选择网格 */
.mode-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1rem;
}

.mode-card {
  padding: 1rem;
  border: 1px solid #eee;
  border-radius: 12px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
  
  svg {
    font-size: 1.8rem;
    color: #666;
    margin-bottom: 0.5rem;
  }

  &.active {
    border-color: #ff6b6b;
    background: #fff0f0;
    svg {
      color: #ff6b6b;
    }
  }

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  }
}

/* 温度时间控制卡片 */
.temp-time-card {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 1.5rem;
}

.control-group {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
  margin-bottom: 1.5rem;
}

.input-field {
  label {
    display: block;
    margin-bottom: 0.8rem;
    color: #444;
    font-weight: 500;
  }
}

.number-input {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  
  button {
    width: 36px;
    height: 36px;
    border: 1px solid #ddd;
    border-radius: 8px;
    background: white;
    cursor: pointer;
    transition: all 0.2s;
    
    &:hover {
      background: #f8f9fa;
      border-color: #ccc;
    }
  }
  
  input {
    width: 80px;
    padding: 0.5rem;
    text-align: center;
    border: 1px solid #ddd;
    border-radius: 8px;
  }
  
  span {
    color: #666;
  }
}

/* 预设菜谱 */
.recipe-preset {
  h4 {
    margin-bottom: 1rem;
    color: #444;
  }
}

.recipe-buttons {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 1rem;
  
  button {
    position: relative;
    padding: 1rem;
    border: 1px solid #eee;
    border-radius: 8px;
    text-align: left;
    background: white;
    cursor: pointer;
    transition: all 0.2s;
    
    &:hover {
      border-color: #ff6b6b;
    }
    
    &.active {
      border-color: #ff6b6b;
      background: #fff0f0;
    }
  }
}

.recipe-info {
  display: block;
  font-size: 0.8rem;
  color: #666;
  margin-top: 0.3rem;
}

/* 开始按钮 */
.start-btn {
  width: 100%;
  padding: 1rem;
  background: linear-gradient(135deg, #ff6b6b, #ff8787);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 1.1rem;
  transition: all 0.2s;
  
  &:disabled {
    background: #ddd;
    cursor: not-allowed;
  }
  
  &:not(:disabled):hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(255,107,107,0.3);
  }
  
  svg {
    margin-right: 0.5rem;
  }
}

/* 修改style中的洗碗机样式 */
/* 洗碗机主容器 */
.wash-controls {
  padding: 1rem;
  display: grid;
  gap: 1.5rem;
}

/* 模式网格布局 */
.mode-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1rem;
}

.mode-card {
  padding: 1rem;
  border: 2px solid #e0f2fe;
  border-radius: 12px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  background: white;
  
  h4 {
    color: #0891b2;
    margin-bottom: 0.5rem;
  }
  
  p {
    color: #666;
    font-size: 0.9rem;
    margin: 0.3rem 0;
  }
  
  &.active {
    border-color: #0891b2;
    background: #f0f9ff;
    box-shadow: 0 2px 8px rgba(8,145,178,0.1);
  }
  
  &:hover {
    transform: translateY(-3px);
  }
}

/* 耗材状态 */
.supply-status {
  display: grid;
  gap: 1rem;
  padding: 1rem;
  background: #f8fafc;
  border-radius: 12px;
}

.supply-item {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  padding: 0.8rem;
  border-radius: 8px;
  background: white;
  transition: all 0.2s;
  
  &.low {
    background: #fff1f2;
    color: #e11d48;
  }
  
  svg {
    color: #64748b;
  }
}

/* 进度环优化 */
.progress-circle {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: conic-gradient(#06b6d4 var(--progress), #e0f2fe 0);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  color: #0891b2;
}

/* 开始按钮优化 */
.start-btn {
  background: linear-gradient(135deg, #06b6d4, #0891b2);
  color: white;
  padding: 1rem;
  border: none;
  border-radius: 12px;
  font-size: 1rem;
  transition: all 0.3s;
  
  &:disabled {
    background: #cbd5e1;
    cursor: not-allowed;
  }
  
  &:not(:disabled):hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(6,182,212,0.3);
  }
}

/* 调整网格布局为垂直排列 */
.mode-grid {
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); /* 自适应列数 */
}

/* 响应式调整 */
@media (max-width: 480px) {
  .mode-grid {
    grid-template-columns: 1fr; /* 小屏幕下单列显示 */
  }
  
  .temp-time-card .control-group {
    grid-template-columns: 1fr; /* 温度时间控制改为单列 */
  }
}

/* 修改所有固定宽度为百分比 */
.temp-slider {
  width: 90%; /* 滑动条自适应 */
}

.number-input input {
  width: 60px; /* 固定数字输入框宽度 */
}

/* 添加内容换行处理 */
.item-name {
  white-space: normal; /* 允许食材名称换行 */
}

/* 调整模态框宽度 */
.inventory-modal {
  width: 90vw; /* 适应屏幕宽度 */
  max-width: 400px;
}

/* 抽油烟机专用样式 */
.fan-controls {
  display: grid;
  gap: 1.5rem;
}

/* 风速控制 */
.speed-control {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 1.5rem;
}

.section-title {
  color: #2c3e50;
  margin: 0 0 1.5rem;
  font-size: 1.1rem;
  display: flex;
  align-items: center;
  gap: 0.8rem;
}

.speed-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1rem;
}

.speed-btn {
  position: relative;
  height: 100px;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  background: white;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  
  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  }
  
  &.active {
    border-color: #3498db;
    background: #f0f9ff;
    
    .speed-icon {
      color: #3498db;
    }
    
    .speed-indicator {
      opacity: 0.15;
    }
  }
}

.speed-icon {
  font-size: 1.8rem;
  color: #7f8c8d;
  margin-bottom: 0.5rem;
  z-index: 1;
}

.speed-label {
  font-size: 0.9rem;
  color: #2c3e50;
  font-weight: 500;
  z-index: 1;
}

.speed-indicator {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: #3498db;
  opacity: 0;
  transition: opacity 0.3s;
}

/* 智能控制 */
.smart-control {
  background: #fff;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.control-item {
  display: flex;
  align-items: center;
  gap: 1.2rem;
}

.control-icon {
  font-size: 1.8rem;
  color: #27ae60;
  background: #e8f6f0;
  padding: 1rem;
  border-radius: 50%;
}

.control-content {
  flex: 1;
  
  label {
    display: block;
    color: #2c3e50;
    margin-bottom: 0.5rem;
    font-weight: 500;
  }
}

.switch-wrapper {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.mode-status {
  color: #7f8c8d;
  font-size: 0.9rem;
}

/* 滤网状态 */
.filter-status {
  background: #fff;
  border-radius: 12px;
  padding: 1.5rem;
  text-align: center;
}

.status-card {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 2rem;
}

.status-header {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  justify-content: center;
  margin-bottom: 1.5rem;
  
  h5 {
    margin: 0;
    color: #2c3e50;
    font-size: 1.1rem;
  }
}

.status-icon {
  font-size: 1.5rem;
  color: #e67e22;
}

.progress-ring {
  position: relative;
  width: 80px;
  height: 80px;
  margin: 0 auto 1.5rem;
}

.progress-bg {
  fill: none;
  stroke: #f0f0f0;
  stroke-width: 6;
}

.progress-bar {
  fill: none;
  stroke: #e67e22;
  stroke-width: 6;
  stroke-linecap: round;
  transform: rotate(-90deg);
  transform-origin: 50% 50%;
  transition: stroke-dashoffset 0.5s ease;
}

.progress-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 1.2rem;
  font-weight: bold;
  color: #e67e22;
}

.maintenance-btn {
  background: #3498db;
  color: white;
  border: none;
  padding: 0.8rem 1.5rem;
  border-radius: 25px;
  cursor: pointer;
  transition: all 0.3s;
  display: inline-flex;
  align-items: center;
  gap: 0.6rem;
  
  &:hover {
    background: #2980b9;
    transform: translateY(-2px);
  }
  
  svg {
    font-size: 0.9rem;
  }
}

/* 风速状态指示 */
.status-badge {
  padding: 0.3rem 0.8rem;
  border-radius: 15px;
  font-size: 0.9rem;
  background: #f0f0f0;
  color: #7f8c8d;
  
  &.speed-1 { background: #d4e6f1; color: #2980b9; }
  &.speed-2 { background: #d5f5e3; color: #27ae60; }
  &.speed-3 { background: #f9e79f; color: #f1c40f; }
}

</style>