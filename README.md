# Yatmosphere 智能家居控制系统

> 打造“全屋环境氛围”——基于多传感器、MQTT 和云平台的智能家居管控方案
> 访问网址：[Yatmosphere](https://yatmosphere.xyz)

---

## 🌟 项目简介

Yatmosphere（融合“Yat”与 “Atmosphere”）是一款面向家庭全屋环境感知与自动化的智能家居控制系统。

- **环境监测**：温湿度、光照、PM2.5/CO₂ 等多维传感器数据采集
- **自动联动**：基于预设规则或 AI 算法，自动调节空调、照明、窗帘、净化器等
- **远程操控**：Web 与移动端双客户端实时监控·远程控制
- **高可扩展**：模块化架构，支持多品牌

---

## ⚙️ 核心特性

- 设备管理：一键添加/删除/分组·在线/离线状态监测
- 场景模式：自定义规则·定时/事件触发·一键切换
- 用户权限：多角色授权·安全可信
- 数据可视化：实时仪表盘·历史曲线·告警通知
- 边缘网关：本地缓存·断网续传·低延迟

---

## 🏗️ 技术栈

| 层级       | 技术/工具                        |
| ---------- | -------------------------------- |
| 前端 UI    | Vue 3          |
| 后端服务   | Spring Boot |
| 通信协议   | MQTT             |
| 数据库     | MySQL                  |
| 容器化部署 | Docker           |
| 文档与协作 | GitHub / WPS 协作文档            |

---

## 🚀 快速上手

1. 克隆仓库

   ```bash
   git clone https://github.com/sysu-orz/yatmosphere.git
   cd yatmosphere
   ```
2. 安装依赖

   ```bash
   # 前端
   cd frontend && npm install
   ```
3. 配置环境

   ```bash
   cp .env.example .env
   # 编辑 .env，填写数据库与 MQTT 信息
   # 创建python环境
   cd gateway
   conda create -n yatmosphere python=3.11.5
   conda activate yatmosphere
   pip install -r requirements.txt
   ```
4. 启动服务

   ```bash
   # 启动MQTT Broker
   docker run -d --name emqx -p 1883:1883 -p 8083:8083 emqx/emqx:latest
   # 后端
   cd backend && mvn spring-boot:run
   # 前端
   cd frontend && npm run dev
   ```

---

## 📚 文档与协作

* **项目计划书 & 设计文档**：详见 WPS 协作文档
* **完整文档清单**：需求规格 · 系统设计 · 接口说明 · 部署手册 · 用户手册 · 测试报告

---

## 🤝 贡献指南

- **分支流程**：采用 Git 主干（main/master）+ 功能分支（feature/*）的模式。主干分支仅用于稳定版本发布，所有新功能开发在 feature 分支进行，并通过 Pull Request 合并到主干。
- **提交规范**：提交信息应简洁明了，描述所做更改。可参考 [Conventional Commits](https://www.conventionalcommits.org) 规范，如 `feat:`、`fix:` 等前缀。
- **拉取请求 (PR)**：开发完成后，打开 Pull Request，请团队成员进行代码评审。确保所有检查通过（包括自动化测试）后再合并分支。
- **代码审查**：在 PR 中附上改动说明和必要的截图或示例。审查过程中发现问题及时讨论，确保代码质量。
- **Issue 管理**：使用 Issue 跟踪功能任务和缺陷。新建 Issue 时标明标题、详细说明和重现步骤，并使用标签分类和指派负责人。
- **编码规范**：遵守项目的代码风格和文档规范。使用代码格式化和静态检查工具（如 ESLint）保持代码整洁。

---

## 📄 许可证

本项目遵循 MIT 许可证。详情见 [LICENSE](LICENSE)。

> 欢迎在 Issues 中反馈问题或建议，一起打造更智能的家居体验！
