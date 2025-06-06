<!-- src/views/Devices.vue -->
<template>
    <div class="device-page">
        <h1 class="page-title">设备管理</h1>

        <!-- 设备列表 -->
        <section class="device-list card">
            <div class="device-list-header">
                <h2>设备列表</h2>
                <div class="device-actions">
                    <button class="action-btn add-btn" @click="canAddDevice">添加设备</button>
                    <button class="action-btn remove-btn" @click="canRemoveDevice">移除设备</button>
                </div>
            </div>

            <!-- 表格头部 -->
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
        </section>

        <!-- 添加设备弹窗 -->
        <div class="device-modal" v-if="showAddDeviceModal" @click.self="showAddDeviceModal = false">
            <div class="modal-content">
                <h3>添加新设备（请在弹窗最下方确认添加）</h3>

                <!-- 设备类型 -->
                <div class="form-group">
                    <label>设备类型：</label>
                    <select v-model="newDevice.type" @change="resetDeviceForm">
                        <option value="airConditioner">空调</option>
                        <option value="light">灯</option>
                    </select>
                </div>

                <!-- 通用字段 -->
                <div class="form-group">
                    <label>设备名称：</label>
                    <input type="text" v-model="newDevice.deviceName" placeholder="例如: 客厅空调 / 卧室灯" />
                </div>
                <div class="form-group">
                    <label>设备ID：</label>
                    <input type="text" v-model="newDevice.deviceId" placeholder="例如: AC001 / LT001" />
                </div>

                <!-- 初始状态开关 -->
                <div class="form-group">
                    <label>初始状态：</label>
                    <label class="switch">
                        <input type="checkbox" v-model="newDevice.status"
                            @click="triggerParticleEffect($event, 'add-device-switch')" />
                        <span class="slider round"></span>
                        <span class="switch-label">
                            {{ newDevice.status ? '开启' : '关闭' }}
                        </span>
                        <div class="particle-container" :class="{ active: activeParticle === 'add-device-switch' }">
                            <span class="particle" v-for="n in 8" :key="n"
                                :style="{ '--angle': `${(n - 1) * 45}deg` }"></span>
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
                    <label>选择要移除的设备：</label>
                    <select v-model="selectedDeviceToRemove">
                        <option v-for="device in devices" :key="device.id"
                            :value="{ deviceId: device.id, deviceName: device.name }">
                            {{ device.name }} ({{ device.id }})
                        </option>
                    </select>
                </div>
                <div class="modal-actions">
                    <button class="cancel-btn" @click="showRemoveDeviceModal = false">
                        取消
                    </button>
                    <button class="confirm-btn" @click="removeDevice" :disabled="!selectedDeviceToRemove">
                        确认移除
                    </button>
                </div>
            </div>
        </div>

      <!--权限错误弹窗-->
      <div class="access-error-modal" v-if="showAccessError" @click.self="showAccessError = false">
        <div class="modal-content">
          <h3 class="error-title"><span style="color: red">⚠</span>权限错误</h3>
          <p class="error-message">&nbsp;您没有权限使用此功能，请联系管理员&nbsp;</p>
        </div>
      </div>
    </div>
</template>


<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import {
  allDevices,
  activeParticle,
  triggerParticleEffect,
  useTimeUpdater, // 导入时间更新组合函数
} from './DashboardLogic.js'

// 调用时间更新组合函数
useTimeUpdater()

// 从后端拉取设备列表
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
onMounted(fetchAllDevices)
// 设备管理相关状态
const showAddDeviceModal = ref(false)
const showRemoveDeviceModal = ref(false)
const selectedDeviceToRemove = ref('')
const devices = ref([]) // 存储设备列表
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
      if(device.type === 'airConditioner'){
        console.error('设备添加失败:', res.data.msg);
      }else{
        console.error('设备添加失败:', res.data.data.message);
      }
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
      resetDeviceForm();
    } else {
      console.error('设备删除失败:', res.data.msg);
    }
  } catch (err) {
    console.error('删除设备请求异常:', err);
  }
};

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


// 页面挂载后加载设备
onMounted(() => {
    fetchAllDevices()
})
</script>

<style scoped>
@import './Devices.css';
</style>