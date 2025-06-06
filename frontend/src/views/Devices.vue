<!-- src/views/Devices.vue -->
<template>
    <div class="device-page">
        <h1 class="page-title">设备管理</h1>

        <!-- 设备列表卡片 -->
        <section class="device-list card">
            <div class="device-list-header">
                <h2>设备列表</h2>
                <div class="device-actions">
                    <button class="action-btn add-btn" @click="canAddDevice">添加设备</button>
                    <button class="action-btn remove-btn" @click="canRemoveDevice">移除设备</button>
                </div>
            </div>

            <!-- 表格头部 -->
            <div class="device-table-container">
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
                    <input type="text" v-model="newDevice.deviceName" placeholder="例如：客厅空调 / 卧室灯" />
                </div>
                <div class="form-group">
                    <label>设备ID：</label>
                    <input type="text" v-model="newDevice.deviceId" placeholder="例如：AC001 / LT001" />
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

                <!-- 空调专属字段 -->
                <div v-if="newDevice.type === 'airConditioner'">
                    <div class="form-group">
                        <label>温度 (℃)：</label>
                        <input type="number" v-model.number="newDevice.temperature" min="16" max="30" />
                    </div>
                    <div class="form-group">
                        <label>模式：</label>
                        <select v-model="newDevice.mode">
                            <option value="cool">制冷</option>
                            <option value="heat">制热</option>
                            <option value="dry">除湿</option>
                            <option value="fan">送风</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>风速 (档)：</label>
                        <select v-model.number="newDevice.fanLevel">
                            <option v-for="n in 5" :key="n" :value="n">{{ n }} 档</option>
                        </select>
                    </div>
                </div>

                <!-- 灯专属字段 -->
                <div v-if="newDevice.type === 'light'">
                    <div class="form-group">
                        <label>亮度 (%)：</label>
                        <input type="number" v-model.number="newDevice.brightness" min="0" max="100" />
                    </div>
                    <div class="form-group">
                        <label>色温：</label>
                        <select v-model="newDevice.colorTemp">
                            <option value="natural">自然</option>
                            <option value="warm">暖光</option>
                            <option value="cool">冷光</option>
                        </select>
                    </div>
                </div>

                <div class="modal-actions">
                    <button class="cancel-btn" @click="showAddDeviceModal = false">
                        取消
                    </button>
                    <button class="confirm-btn" @click="addDevice" :disabled="!canConfirmAdd">
                        添加
                    </button>
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

        <!-- 权限错误弹窗 -->
        <div class="access-error-modal" v-if="showAccessError" @click.self="showAccessError = false">
            <div class="modal-content error-modal-content">
                <h3 class="error-title">
                    <span style="color: red">⚠</span> 权限错误
                </h3>
                <p class="error-message">
                    您没有权限使用此功能，请联系管理员
                </p>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'

// 设备列表
const devices = ref([])

// 新设备表单数据
const newDevice = ref({
    type: 'airConditioner',
    deviceName: '',
    deviceId: '',
    status: true,
    temperature: 24,
    mode: 'cool',
    fanLevel: 3,
    brightness: 80,
    colorTemp: 'natural'
})

// 控制弹窗显示
const showAddDeviceModal = ref(false)
const showRemoveDeviceModal = ref(false)
const showAccessError = ref(false)

// 粒子特效控制
const activeParticle = ref(null)
function triggerParticleEffect(event, identifier) {
    activeParticle.value = identifier
    setTimeout(() => {
        activeParticle.value = null
    }, 400)
}

// 选中的待移除设备
const selectedDeviceToRemove = ref(null)

// 权限逻辑
const roleAccess = ref(false)
function checkRoleAccess() {
    roleAccess.value = localStorage.getItem('role') === 'admin'
    if (!roleAccess.value) {
        showAccessError.value = true
    }
    return roleAccess.value
}
function canAddDevice() {
    if (!checkRoleAccess()) return
    resetDeviceForm()
    showAddDeviceModal.value = true
}
function canRemoveDevice() {
    if (!checkRoleAccess()) return
    showRemoveDeviceModal.value = true
}

// 重置添加设备表单
function resetDeviceForm() {
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

// 是否可以确认添加
const canConfirmAdd = computed(() => {
    const d = newDevice.value
    return d.deviceName && d.deviceId
})

// 添加设备 (前端模拟)
function addDevice() {
    const d = { ...newDevice.value }
    if (d.type === 'airConditioner') {
        devices.value.push({
            id: d.deviceId,
            name: d.deviceName,
            state: d.status,
            details: `模式: ${{ cool: '制冷', heat: '制热', dry: '除湿', fan: '送风' }[d.mode]
                }，温度: ${d.temperature}℃，风速: ${d.fanLevel}档`
        })
    } else {
        devices.value.push({
            id: d.deviceId,
            name: d.deviceName,
            state: d.status,
            details: `亮度: ${d.brightness}%，色温: ${{ natural: '自然', warm: '暖光', cool: '冷光' }[d.colorTemp]
                }`
        })
    }
    showAddDeviceModal.value = false
    resetDeviceForm()
}

// 移除设备 (调用后端)
async function removeDevice() {
    if (!selectedDeviceToRemove.value) return
    try {
        const token = localStorage.getItem('authToken')
        const res = await axios.post(
            '/device/deleteByDeviceName',
            null,
            {
                params: { deviceName: selectedDeviceToRemove.value.deviceName },
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: token
                }
            }
        )
        if (res.data.status === 200 && res.data.data === true) {
            devices.value = devices.value.filter(
                (d) => d.id !== selectedDeviceToRemove.value.deviceId
            )
            showRemoveDeviceModal.value = false
            selectedDeviceToRemove.value = null
            fetchAllDevices()
        } else {
            console.error('设备删除失败:', res.data.msg)
        }
    } catch (err) {
        console.error('删除设备请求异常:', err)
    }
}

// 从后端拉取设备列表
async function fetchAllDevices() {
    try {
        const token = localStorage.getItem('authToken')
        const res = await axios.get('/device/getall', {
            headers: {
                'Content-Type': 'application/json',
                Authorization: token
            }
        })
        if (res.data && Array.isArray(res.data.data)) {
            devices.value = res.data.data.map((item) => ({
                id: item.deviceId,
                name: item.deviceName,
                state: item.status === '开启',
                details: item.detail || ''
            }))
        }
    } catch (err) {
        console.error('获取设备列表失败', err)
    }
}

// 页面挂载后加载设备
onMounted(() => {
    fetchAllDevices()
})
</script>

<style scoped>
.device-page {
    padding: 24px;
    background-color: #f5f7fa;
}

/* 页面标题 */
.page-title {
    font-size: 24px;
    color: #333;
    margin-bottom: 16px;
}

/* 通用卡片样式 */
.card {
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
    padding: 24px;
    margin-bottom: 24px;
}

/* 设备列表头部 */
.device-list-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
}

.device-actions .action-btn {
    margin-left: 8px;
    padding: 6px 12px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;
}

.add-btn {
    background-color: #409eff;
    color: #fff;
}

.add-btn:hover {
    background-color: #2879c7;
}

.remove-btn {
    background-color: #f56c6c;
    color: #fff;
}

.remove-btn:hover {
    background-color: #d43834;
}

/* 设备表格 */
.device-table-container {
    overflow-x: auto;
}

.device-table {
    width: 100%;
}

.table-header,
.table-row {
    display: grid;
    grid-template-columns: 1fr 2fr 1fr 3fr;
    gap: 16px;
    align-items: center;
    padding: 8px 0;
}

.table-header {
    font-weight: bold;
    border-bottom: 2px solid #e0e0e0;
}

.table-row {
    border-bottom: 1px solid #e0e0e0;
}

.device-on {
    background-color: #f0f9eb;
}

/* 弹窗通用样式 */
.device-modal,
.access-error-modal {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.4);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 100;
}

.modal-content {
    width: 360px;
    max-width: 90%;
    background-color: #fff;
    border-radius: 8px;
    padding: 24px;
    box-shadow: 0 2px 16px rgba(0, 0, 0, 0.2);
}

.modal-content h3 {
    margin-top: 0;
    font-size: 18px;
    color: #333;
}

.form-group {
    margin: 12px 0;
}

.form-group label {
    display: block;
    margin-bottom: 6px;
    font-size: 14px;
    color: #555;
}

.form-group input[type='text'],
.form-group input[type='number'],
.form-group select {
    width: 100%;
    padding: 6px 8px;
    font-size: 14px;
    border: 1px solid #ccc;
    border-radius: 4px;
}

/* 开关样式 */
.switch {
    position: relative;
    display: inline-block;
    width: 50px;
    height: 24px;
    margin-right: 8px;
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
    transition: 0.2s;
    border-radius: 24px;
}

.slider:before {
    position: absolute;
    content: '';
    height: 20px;
    width: 20px;
    left: 2px;
    bottom: 2px;
    background-color: #fff;
    transition: 0.2s;
    border-radius: 50%;
}

input:checked+.slider {
    background-color: #409eff;
}

input:checked+.slider:before {
    transform: translateX(26px);
}

.switch-label {
    font-size: 14px;
    vertical-align: middle;
}

/* 粒子特效 */
.particle-container {
    position: absolute;
    top: -8px;
    left: 50%;
    transform: translateX(-50%);
    width: 0;
    height: 0;
}

.particle-container.active .particle {
    animation: explode 0.4s ease-out forwards;
}

.particle {
    position: absolute;
    width: 6px;
    height: 6px;
    background-color: #409eff;
    border-radius: 50%;
    top: 0;
    left: 0;
    transform-origin: 0 0;
    opacity: 0;
}

@keyframes explode {
    0% {
        transform: scale(0.5);
        opacity: 1;
    }

    100% {
        transform: scale(1.5) translateY(-8px);
        opacity: 0;
    }
}

/* 弹窗按钮组 */
.modal-actions {
    margin-top: 16px;
    text-align: right;
}

.cancel-btn,
.confirm-btn {
    padding: 6px 12px;
    font-size: 14px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
}

.cancel-btn {
    background-color: #f0f0f0;
    color: #333;
    margin-right: 8px;
}

.cancel-btn:hover {
    background-color: #e0e0e0;
}

.confirm-btn {
    background-color: #409eff;
    color: #fff;
}

.confirm-btn:disabled {
    background-color: #a0cfff;
    cursor: not-allowed;
}

.confirm-btn:hover:enabled {
    background-color: #2879c7;
}

/* 权限弹窗样式*/
.access-error-modal {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
}

.access-error-modal .modal-content {
    border: rgb(78, 205, 196) solid 2px;
    border-radius: 15px;
}

.access-error-modal .error-title {
    font-size: 1.5rem;
    font-weight: bold;
    color: var(--color-text);
    background-color: rgb(78, 205, 196);
    margin-bottom: 20px;
    text-align: center;
}

.access-error-modal .error-message {
    font-size: 1rem;
    font-style: italic;
    color: var(--color-text);
    margin-bottom: 20px;
    text-align: center;
}
</style>
