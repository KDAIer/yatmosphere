# 智能家居控制系统 - 前端项目文档

## 项目概述
这是一个基于 Vue 3 和 Vite 构建的智能家居控制系统的前端项目，提供了房间设备控制、仪表盘展示和用户管理等功能。

## 项目结构说明

### 源码目录 (src)
```
src/
│ # 核心文件
│ App.vue # 应用根组件，包含整体布局结构
│ main.js # 应用入口文件，初始化Vue实例和全局配置
│ tree.txt # 项目目录结构说明文件
│
├─assets/ # 静态资源目录
│ │ base.css # 基础样式表
│ │ global.css # 全局样式表
│ │ main.css # 主样式表
│ │ logo.svg # 应用logo(SVG格式)
│ │
│ ├─audio/ # 音频资源
│ │ Ready.mp3 # 准备提示音
│ │ 圣城日常-塞壬唱片-MSR.mp3 # 背景音乐1
│ │ 海愿 - 塞壬唱片-MSR、Eagle Wei.mp3 # 背景音乐2
│ │ 生命流.mp3 # 背景音乐3
│ │
│ ├─images/ # 图片资源
│ │ aircondition.png # 空调图标
│ │ background.jpg # 亮色背景图
│ │ background_dark.jpg # 暗色背景图
│ │ background_login.jpg # 登录页背景
│ │ bedroom.png # 卧室图标
│ │ doorlock.png # 门锁图标(开)
│ │ doorlock1.png # 门锁图标(关)
│ │ kitchen.png # 厨房图标
│ │ livingroom.png # 客厅图标
│ │ security.png # 安防图标(正常)
│ │ security1.png # 安防图标(警报)
│ │ switch.png # 开关图标(开)
│ │ switch1.png # 开关图标(关)
│ │ wifi.png # WiFi图标(连接)
│ │ wifi1.png # WiFi图标(断开)
│ │ user.png # 用户默认头像
│ │
│ └─videos/ # 视频资源
│ background.mp4 # 背景视频
│ background1.mp4 # 备用背景视频
│
├─components/ # 可复用组件
│ │ AirConditioner.vue # 空调控制组件
│ │ BedroomControl.vue # 卧室控制面板
│ │ KitchenControl.vue # 厨房控制面板
│ │ LivingRoomControl.vue # 客厅控制面板
│ │ RoomControl.vue # 通用房间控制组件
│ │ Sidebar.vue # 侧边导航栏组件
│ │
│ ├─icons/ # SVG图标组件
│ │ IconCommunity.vue # 社区图标
│ │ IconDocumentation.vue # 文档图标
│ │ IconEcosystem.vue # 生态系统图标
│ │ IconSupport.vue # 支持图标
│ │ IconTooling.vue # 工具图标
│ │
│ └─tests/ # 组件测试
│ HelloWorld.spec.js # 示例测试文件
│
├─router/ # 路由配置
│ index.js # 路由定义和配置
│
├─stores/ # 状态管理(Pinia)
│ counter.js # 示例计数器store
│
└─views/ # 页面级组件
Dashboard.vue # 仪表盘页面
DashboardLogic.js # 仪表盘业务逻辑
DashboardStyles.css # 仪表盘样式
Devices.css # 设备页面样式
Devices.vue # 设备管理页面
Login.vue # 登录页面
Profile.css # 个人资料页面样式
Profile.js # 个人资料业务逻辑
Profile.vue # 个人资料页面
```

## 开发指南

### 🛠️ 开发环境配置

#### 推荐工具
- **IDE**: [Visual Studio Code](https://code.visualstudio.com/)
- **必备插件**:
  - [Volar](https://marketplace.visualstudio.com/items?itemName=Vue.volar) (Vue 3官方推荐插件)
  - [ESLint](https://marketplace.visualstudio.com/items?itemName=dbaeumer.vscode-eslint) (代码规范检查)
  - [Prettier](https://marketplace.visualstudio.com/items?itemName=esbenp.prettier-vscode) (代码格式化)

> ⚠️ 注意：请确保禁用Vetur插件以避免冲突

### ⚙️ 项目配置

#### 环境变量
项目支持以下环境变量配置：
```env
VITE_API_BASE_URL=  # 接口基础地址
VITE_APP_TITLE=     # 应用标题
VITE_THEME=light    # 默认主题(light/dark)
```
### 自定义Vite配置
修改 vite.config.js 可调整：
开发服务器配置
构建选项
插件配置
参考文档：Vite配置参考

🚀 项目脚本
### 安装依赖
```
npm install
# 或
yarn install
```

### 开发模式
```
npm run dev
# 访问 http://localhost:5173
```

### 生产构建
```
npm run build
# 生成文件在 /dist 目录
```

### 测试相关
```
# 运行单元测试
npm run test:unit

# 生成测试覆盖率报告
npm run test:coverage

# 运行E2E测试
npm run test:e2e
```
### 代码质量
```
# 代码格式检查
npm run lint

# 自动修复可修复的lint错误
npm run lint:fix
```
### 📁 目录规范
组件开发：

- 公共组件放在 /components

- 页面级组件放在 /views

- 组件命名使用PascalCase

### 样式管理：

- 全局样式：/assets/*.css

- 组件样式：与组件同名的CSS文件

- 使用BEM命名规范

### 状态管理：

- Pinia store模块放在 /stores

- 按功能模块拆分store

### 🔌 API集成
项目已配置Axios实例，可在以下位置修改：

```javascript
// 通常位于 /src/api/request.js
import axios from 'axios'

const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000
})
```
🎨 主题定制
- 通过修改 /assets/main.css 中的CSS变量

- 或使用预处理器变量覆盖

🐛 调试技巧
- 使用Vue Devtools调试组件

- 开发模式下支持热重载

- 测试组件时可使用Storybook模式

📦 第三方依赖
- 主要依赖：

- vue-router@4 - 路由管理

- pinia - 状态管理

- axios - HTTP客户端

- sass - CSS预处理器

完整依赖请查看 package.json
