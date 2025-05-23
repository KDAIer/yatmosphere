# Yatmosphere 智能家居控制系统架构设计——设备接口端(Gateway)

## 一、整体架构

```mermaid
graph TD
    A[后端系统] <-- MQTT协议 --> B[MQTT Broker 消息服务器]
    B <-->|MQTT协议| C[设备接口端]
    C -->|设备专用协议/第三方协议| D[物理设备]
```

## 二、技术栈

- 语言: Python 3.11+  
- 库:
  - paho-mqtt    # MQTT客户端库
  - PyYAML       # 配置文件解析

## 三、设备接口端(Gateway)详细设计

### 1. 模块组成

```mermaid
graph TD
    A[设备接口端] --> B[MQTT客户端模块]
    B --> B1[连接MQTT Broker]
    B --> B2[订阅/发布主题]

    A --> C[设备管理模块]
    C --> C1[设备发现]
    C --> C2[设备状态维护]

    A --> D[协议转换模块]
    D --> D1[JSON ↔ 设备协议]

    A --> E[状态同步模块]
    E --> E1[状态变化检测]
    E --> E2[主动上报]

    A --> F[命令执行模块]
    F --> F1[接收后端命令]
    F --> F2[执行设备控制]
    F --> F3[返回response]

```

### 2. 通信流程

```mermaid
sequenceDiagram
    participant Backend
    participant Broker
    participant Device

    Note over Backend,Device: 场景1：后端主动查询
    Backend->>Broker: PUBLISH yatmosphere/device/thermo001/request (带request_id)
    Broker->>Device: 转发请求
    Device->>Broker: PUBLISH yatmosphere/device/thermo001/response
    Broker->>Backend: 转发响应

    Note over Backend,Device: 场景2：设备主动上报
    Device->>Broker: PUBLISH yatmosphere/device/light001/sstatus (状态变更)
    Broker->>Backend: 转发状态

    Note over Backend,Device: 场景3：后端控制设备
    Backend->>Broker: PUBLISH yatmosphere/device/light001/command
    Broker->>Device: 转发指令
    Device->>Broker: PUBLISH yatmosphere/device/light001/ack
    Broker->>Backend: 转发ACK
```

**2.1 后端获取设备信息**
```
后端 → MQTT Broker → 设备接口端 → 物理设备
      (发布/get_device_info)      (获取设备状态)
      
物理设备 → 设备接口端 → MQTT Broker → 后端
                        (发布/device_info)
```

**2.2 后端设置设备参数**
```
后端 → MQTT Broker → 设备接口端 → 物理设备
      (发布/set_device_params)    (应用设置)
      
物理设备 → 设备接口端 → MQTT Broker → 后端
                        (发布/response)
```

**2.3 设备状态主动上报**
```
物理设备 → 设备接口端 → MQTT Broker → 后端
(状态变化)             (发布/device_status_update)
```

## 四、通信协议设计规范

### 1. MQTT主题设计

| **主题类型**               | **格式**        | 
|---------------------------|-----------------|
| 设备状态上报               | `device/<device_id>/status` |
| 后端下发指令               | `device/<device_id>/command` |
| 设备响应指令               | `device/<device_id>/response` | 

### 2. 消息格式规范（JSON）

#### 2.1 设备状态主动上报（status）

```json
{
  "msg_id": "uuidv4",
  "timestamp": 1630000000,
  "device_type": "light",
  "device_id": "light_001",
  "msg_type": "status",
  "power": "on",
  "brightness": 80,
  "color": "#ffffff"
}
```

#### 2.2 后端下发指令（command）

- 设置参数

```json
{
  "msg_id": "uuidv4",       // 消息唯一标识
  "timestamp": 1630000000,  // UNIX时间戳
  "device_type": "light",
  "device_id": "light_001",
  "msg_type": "command",
  "action": "set_power", // 设置开/关
  "value": "on"
}
```

- 状态查询 (响应用 **msg_type: status**)

```json
{
  "msg_id": "uuidv4",
  "timestamp": 1630000000,
  "device_type": "light",
  "device_id": "light_001",
  "msg_type": "command",
  "action": "get_status" // 状态查询
}
```

#### 2.3 设备响应指令（response）
```json
{
  "msg_id": "uuidv4",       // 消息唯一标识
  "timestamp": 1630000000,  // UNIX时间戳
  "msg_type": "response",
  "status": "success",      // success/failed
  "related_msg_id": "uuidv4", // 关联的command消息ID
  "error_code": 0,           // 错误码（0表示成功）
  "error_msg": ""            // 错误描述
}
```

### 3. 错误码规范
| 错误码 | 说明                  |
|--------|----------------------|
| 0      | 成功                  |
| 400    | 参数错误              |
| 403    | 设备忙（无法立即执行） |
| 404    | 设备离线              |
| 500    | 设备内部错误          |

### 4. 支持的 command 指令 (`device/<device_id>/command`时)

#### 4.1 智能灯 light

| action           | 说明         | value类型 | 示例值      |
|------------------|--------------|----------|-------------|
| set_power        | 设置开关      | string   | "on"/"off"  |
| set_brightness   | 设置亮度      | int      | 0~100       |
| set_color        | 设置颜色      | object   | {"r":255,"g":255,"b":255}   |
| get_status       | 查询当前状态  | 无       |             |

## 五、项目结构（暂定）

```
yatmosphere/
├── backend/                   # 后端系统
│   ├── src/
│   │   └── app.js             # 主入口
├── gateway/                   # 设备接口端
│   ├── src/
│   │   ├── main.py            # 主程序，初始化、事件循环
│   │   ├── mqtt_client.py     # MQTT客户端，负责与MQTT Broker的通信
│   │   ├── device_manager.py  # 设备管理
│   │   └── protocols/         # 协议转换    
│   │       └── ...
│   └── config/
│       └── devices.yaml       # 设备配置文件
├── virtual_devices/           # 模拟设备
│   ├── light.py               # 智能灯
│   └── ...                
└── frontend/                  # 前端
    └── ...
```

## 六、运行测试

- MQTT Broker版本：EMQX，开源版功能完善，支持百万级连接
- 部署方式：Docker容器

```bash
# 终端1 - 启动MQTT Broker (EMQX)
docker run -d --name emqx -p 1883:1883 -p 8083:8083 emqx/emqx:latest

# 终端2 - 启动网关
cd yatmosphere/gateway
pip install -r requirements.txt
python src/main.py

# 终端3 - 启动虚拟设备
cd yatmosphere
python virtual_devices/light.py --device_id light_001
```

- 基础响应验证

测试场景：发送开灯指令 → 验证设备返回ACK

```bash
# 终端1：监听设备响应
mosquitto_sub -h localhost -t "device/light_001/response" -v

# 终端2：发送开灯指令
mosquitto_pub -h localhost -t "device/light_001/command" -m '{
  "msg_id": 1001,
  "timestamp": 1630000000,
  "device_type": "light",
  "device_id": "light_001",
  "msg_type": "command",
  "action": "set_power",
  "value": "on"
}'
```

- 预期响应：
```json
device/light_001/response {
  "msg_id": 1001,
  "msg_type": "response",
  "status": "success",
  "related_msg_id": 1001,
  "timestamp": 1630000001
}
```

## 七、参考资料

- https://github.com/kerwincui/wumei-iot
- https://fastbee.cn/doc/link/mqtt.html#%E4%B8%89%E3%80%81%E8%AE%BE%E5%A4%87%E4%BA%A4%E4%BA%92

