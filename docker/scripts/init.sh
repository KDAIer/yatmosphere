#!/bin/bash
sleep 10 # 等待服务启动

# 创建MQTT用户
emqx_ctl users add backend ${EMQX_BACKEND_PASSWORD} 
emqx_ctl users add gateway ${EMQX_GATEWAY_PASSWORD} 