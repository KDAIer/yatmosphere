// src/composables/DashboardLogic.js
import { ref, computed, onMounted, onUnmounted, reactive } from 'vue'
import axios from 'axios'
import AirConditioner from '@/components/AirConditioner.vue'
import LivingRoomControl from '@/components/LivingRoomControl.vue'
import KitchenControl from '@/components/KitchenControl.vue'
import BedroomControl from '@/components/BedroomControl.vue'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import {
  faFan,
  faHouse,
  faUtensils,
  faBed,
  faPowerOff,
  faLock,
  faShieldAlt,
  faExclamationTriangle,
  faWifi,
} from '@fortawesome/free-solid-svg-icons'


// 主题状态
const theme = ref(localStorage.getItem('theme') || 'light')
const toggleTheme = () => {
  theme.value = theme.value === 'light' ? 'dark' : 'light'
  localStorage.setItem('theme', theme.value)
  document.documentElement.setAttribute('data-theme', theme.value)
}

// 读取角色
const role = ref(localStorage.getItem('role') || '')
// 读取用户名
const username = ref(localStorage.getItem('username') || '未知用户')

// 计算角色中文
const roleName = computed(() => {
  if (role.value === 'admin') return '管理员'
  if (role.value === 'member') return '普通用户'
  return '未知身份'
})
// // 用户数据
// const username = ref('管理员')
const activeArea = ref(null)

// 区域配置
const areas = ref([
  { id: 'ac', name: '空调控制', icon: '/src/assets/images/aircondition.png', component: 'AirConditioner' },
  { id: 'living', name: '客厅', icon: '/src/assets/images/livingroom.png', component: 'LivingRoomControl' },
  { id: 'kitchen', name: '厨房', icon: '/src/assets/images/kitchen.png', component: 'KitchenControl' },
  { id: 'bedroom', name: '卧室', icon: '/src/assets/images/bedroom.png', component: 'BedroomControl' },
])


// 时间相关 - 修复版本
const currentTime = ref('')

// 创建一个组合函数来处理时间更新
// 创建组合函数：处理时间更新 + 请求家庭成员数据
export const useTimeUpdater = () => {
  const updateTime = () => {
    const now = new Date()
    currentTime.value = now.toLocaleTimeString('zh-CN', {
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
      hour12: false,
    })
  }

  const getCurrentAccount = () => {
    return localStorage.getItem('account') || 'admin' // 示例：从本地获取当前账号
  }

  const loadFamilyMembers = async () => {
    const account = getCurrentAccount()
    console.log('加载家庭成员，当前账号:', account)
    try {
      const res = await axios.get('/api/family-members', {
        params: { account }
      })
      // 检查响应状态
      if (res.data.status === 200) {
        familyMembers.value = res.data.data.map((member, index) => ({
          ...member,
          id: index // 为 v-for 添加唯一 key
        }))
        console.log('家庭成员加载成功:', familyMembers.value)
      } else {
        console.error('家庭成员请求失败:', res.data.msg)
      }
    } catch (err) {
      console.error('家庭成员请求异常:', err)
    }
  }

  onMounted(() => {
    console.log('Dashboard 组件已挂载，启动时间更新与数据加载')
    updateTime()
    const timer = setInterval(updateTime, 1000)

    // 加载家庭成员
    loadFamilyMembers()

    onUnmounted(() => {
      console.log('清除时间更新定时器')
      clearInterval(timer)
    })
  })

  return { currentTime, familyMembers }
}
// 环境数据
const environmentData = reactive({
  time: { label: '时间', value: currentTime },
  people: { label: '屋内有', value: '3人' },
  temperature: { label: '温度', value: '22°C' },
  humidity: { label: '湿度', value: '57%' },
  pm25: { label: 'PM2.5', value: '32 μg/m³' },
  co2: { label: '二氧化碳', value: '450 ppm' },
})

// 场景模式
const scenes = ref([
  { id: 'home', name: '回家模式', icon: '🏠' },
  { id: 'away', name: '离家模式', icon: '🚪' },
  { id: 'sleep', name: '睡眠模式', icon: '🛌' },
])

// 所有设备
const allDevices = ref([
  { id: 'AC001', name: '客厅空调', state: true, details: '22°C 制冷模式' },
  { id: 'LK002', name: '智能门锁', state: false, details: '已锁定' },
  { id: 'SF003', name: '安防系统', state: true, details: '布防中' },
  { id: 'LT004', name: '客厅灯', state: true, details: '亮度75%' },
  { id: 'KT005', name: '厨房灯', state: false, details: '关闭' },
  { id: 'BD006', name: '卧室灯', state: true, details: '暖光模式' },
])

// 家庭成员

export const familyMembers = ref([])
export const toggleHomeStatus = (member) => {
  member.isHome = !member.isHome
}
// 示例：从本地存储或状态管理中获取当前账号
const getCurrentAccount = () => {
  // 假设你登录后保存了账号
  return localStorage.getItem('account') || 'admin'
}



// const familyMembers = ref([
//   { id: 1, name: '黄集瑞', isAdmin: true, isHome: true, todos: ['玩火影忍者', '和学妹贴贴'] },
//   { id: 2, name: '黄集瑞的学妹', isAdmin: false, isHome: true, todos: ['和黄集瑞贴贴', '和黄集瑞打游戏'] },
//   { id: 3, name: '张小明', isAdmin: false, isHome: false, todos: [] },
// ])

// const toggleHomeStatus = (member) => {
//   member.isHome = !member.isHome
// }

// 区域切换
const toggleArea = (areaId) => {
  activeArea.value = activeArea.value === areaId ? null : areaId
}

// 获取区域组件
const getAreaComponent = (areaId) => {
  const componentMap = {
    ac: AirConditioner,
    living: LivingRoomControl,
    kitchen: KitchenControl,
    bedroom: BedroomControl,
  }
  return componentMap[areaId]
}

// 快捷设备
const quickDevices = ref([
  { id: 1, name: '总控开关', state: true, status: '系统在线', icon: '/src/assets/images/switch.png', type: 'master' },
  { id: 2, name: '智能门锁', state: false, status: '已锁定', icon: '/src/assets/images/doorlock.png', type: 'lock' },
  { id: 3, name: '安防系统', state: true, status: '布防中', icon: '/src/assets/images/security.png', type: 'security' },
  { id: 5, name: '网络状态', state: true, status: '5G在线', icon: '/src/assets/images/wifi.png', type: 'network', signalStrength: 80 },
])

const handleDeviceAction = (device) => {
  if (!device || !device.id) {
    console.error('Invalid device:', device)
    return
  }
  if (!quickDevices.value) {
    console.error('quickDevices is undefined')
    return
  }
  const index = quickDevices.value.findIndex(d => d.id === device.id)
  if (index === -1) {
    console.error('Device not found:', device.id)
    return
  }

  quickDevices.value[index] = {
    ...quickDevices.value[index],
    state: !quickDevices.value[index].state,
  }
  const newState = quickDevices.value[index].state

  switch (device.type) {
    case 'master':
      quickDevices.value[index].status = newState ? '系统在线' : '系统离线'
      break
    case 'lock':
      quickDevices.value[index].status = newState
        ? `已在 ${new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false })} 锁定`
        : '已解锁'
      break
    case 'security':
      quickDevices.value[index].status = newState ? '布防中' : '撤防中'
      console.log(newState ? '安防系统已激活' : '安防系统已撤防')
      break
    case 'network':
      quickDevices.value[index].status = newState ? '5G在线' : '网络断开'
      quickDevices.value[index].signalStrength = newState ? 80 : 0 // 动态更新强度
      if (newState) showNetworkModal()
      break
    default:
      console.warn('Unknown device type:', device.type)
      break
  }
}

// 网络弹窗
const networkModalVisible = ref(false)
const showNetworkModal = () => {
  networkModalVisible.value = true
}

// 退出登录
const logout = () => {
  localStorage.removeItem('authToken')
  window.location.href = '/login'
}

// 粒子效果
const activeParticle = ref(null)
const triggerParticleEffect = (event, deviceId) => {
  activeParticle.value = deviceId
  setTimeout(() => {
    activeParticle.value = null
  }, 800)
}

// 导出所有逻辑
export {
  theme,
  toggleTheme,
  username,
  roleName,
  activeArea,
  areas,
  quickDevices,
  currentTime,
  environmentData,
  scenes,
  allDevices,
  // familyMembers,
  // toggleHomeStatus,
  toggleArea,
  getAreaComponent,
  handleDeviceAction,
  networkModalVisible,
  showNetworkModal,
  logout,
  activeParticle,
  triggerParticleEffect,
  // useTimeUpdater, // 导出时间更新组合函数
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
}