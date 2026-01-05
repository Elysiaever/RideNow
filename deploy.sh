#!/bin/bash

# RideNow 项目部署脚本

echo "开始部署 RideNow 项目..."

# 检查是否安装了必要的工具
if ! [ -x "$(command -v docker)" ]; then
  echo "错误: Docker 未安装或未在PATH中" >&2
  exit 1
fi

if ! [ -x "$(command -v docker-compose)" ]; then
  echo "错误: Docker Compose 未安装或未在PATH中" >&2
  exit 1
fi

# 构建所有微服务
echo "构建后端服务..."

# 构建公共模块
echo "构建 common 模块..."
cd ./common
mvn clean install -DskipTests
cd ..

# 构建各个服务
SERVICES=("gateway" "services/service-driver" "services/service-matching" "services/service-notification" "services/service-payment" "services/service-ride" "services/service-user")

for service in "${SERVICES[@]}"; do
  echo "构建 $service 服务..."
  cd ./$service
  mvn clean package -DskipTests
  cd ../..
done

# 构建前端
echo "构建前端应用..."
cd ./ui
npm install
npm run build
cd ..

# 启动所有服务
echo "启动所有服务..."
docker-compose up -d

echo "等待服务启动..."
sleep 30

# 检查服务状态
echo "检查服务状态..."
docker-compose ps

echo "RideNow 项目部署完成！"
echo "访问地址: http://localhost:8080"
echo "Nacos 控制台: http://localhost:8848/nacos (用户名: nacos, 密码: Cf1590753)"