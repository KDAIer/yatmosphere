# 后端项目说明

此文档指导如何在本地环境搭建并运行后端服务。

## 一、项目结构

```
backend/iot
├── pom.xml               # Maven 配置
├── src/main/java/...      # 后端源码
├── src/main/resources
│   ├── application.yml    # 配置文件
│   └── application-dev.yml # 数据库配置文件
├── sql——iot.sql        # 数据库建表及初始化脚本
└── target/               # 打包生成目录（无需关注）
```

## 二、前置条件

1. **Java 17**
2. **MySQL 8.x** 或以上
3. **Maven 3.x**

## 三、数据库初始化

1. 打开命令行，登录 MySQL：

   ```bash
   mysql -u root -p
   ```
2. 在 MySQL 提示符下，执行建库并导入脚本：

   ```sql
   -- 导入 iot.sql 脚本
   mysql -u root -p library < path/iot.sql
   ```

   > 注意：`path/iot.sql` 请替换为实际脚本文件路径，
   > 如 `D:/yatmoshpere/backend/sql/iot.sql`。
   >

## 四、配置环境变量（Maven）

1. 确保已安装 Maven，且 `mvn -v` 能正常输出版本信息。
2. 在 PowerShell 临时添加 Maven 到 `Path`：

   ```powershell
   # 查看当前 Path
   $env:Path
   # 添加 Maven bin 目录
   $env:Path += ";D:\apache-maven-3.9.9\bin"
   ```

> 如果希望永久配置，建议在系统环境变量中添加 Maven 的 `bin` 路径。

## 五、运行后端服务

在./backend目录下执行：

```bash
mvn spring-boot:run
```

启动成功后，控制台会显示类似：

```
Tomcat started on port 81 (http) with context path '/'
Started Application in X.XXX seconds
```

## 六、访问接口

* **示例接口（下载postman软件运行，设置的post，因此浏览器无法访问）**：

  * 登录：`POST http://localhost:81/auth/login`
  * 注册：`POST http://localhost:81/auth/register`
  * 用户详情：`GET http://localhost:81/user/detail?account={account}`
  * 设备管理接口：`/device/**`

## 七、常见问题

* **401 Unauthorized**：请先调用 `/auth/login` 获取 token，并在后续请求头中添加 `Authorization: Bearer <token>`。
* **Lombok 编译错误**：请确认 IDE 已安装 Lombok 插件，\`pom.xml
