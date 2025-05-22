负责项目环境部署

<!-- ## docker安装

TODO -->
## 一、文件结构

```
docker
├── .env                  # 环境变量
├── docker-compose.yml    # 创建不同服务/容器的配置
├── mqtt                  # mqtt 相关设置
│   ├── acl.conf
│   └── emqx.conf
├── ...                   # 其他服务文件夹
│   └── ...
└── scripts
    └── init.sh           # 在容器启动时执行一些初始化操作。
```

添加新服务时，需新建对应服务文件夹，并修改:
- docker-compose.yml
- .env
- init.sh

## 二、环境部署

### 1、启动所有服务
```sh
cd yatmosphere/docker
docker compose up -d # 可能需要安装docker compose
```

### 2、验证服务:

```bash
# 检查容器状态
docker ps
```

- MQTT

```bash
# 进入工具容器
docker exec -it mqtt-tools sh

# 在容器内使用mosquitto工具，测试MQTT连接
mosquitto_sub -h yatmosphere_emqx -t "test" -u backend -P 123456
```

### 3、服务访问:

EMQX Dashboard: http://localhost:18083 (账号：admin/密码：123456)

### 关闭容器

```sh
docker compose down
```