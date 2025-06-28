#!/usr/bin/env bash
set -e

# 1. 安装 Python 依赖
cd /workspace/gateway
python -m venv venv
source venv/bin/activate
pip install -r requirements.txt
deactivate

# 2. 安装前端依赖
cd /workspace/frontend
npm install

# 3. 构建后端
cd /workspace/backend
mvn clean package -DskipTests

# 返回根目录
cd /workspace
