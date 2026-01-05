# RideNow 项目部署指南

## 部署架构

RideNow 项目是一个基于Spring Cloud的微服务架构应用，包含以下组件：

- **Nacos**: 服务发现和配置中心
- **PostgreSQL**: 主数据库
- **Redis**: 缓存服务
- **RabbitMQ**: 消息队列
- **Gateway**: API网关
- **Microservices**: 各个业务微服务
- **UI**: 前端应用

## 部署要求

- Docker 20.10+
- Docker Compose v2+
- Maven 3.8+
- Node.js 20+

## 部署步骤

### 1. 构建项目

```bash
# 构建后端服务
cd common && mvn clean install -DskipTests && cd ..
cd gateway && mvn clean package -DskipTests && cd ..
cd services/service-driver && mvn clean package -DskipTests && cd ../..
cd services/service-matching && mvn clean package -DskipTests && cd ../..
cd services/service-notification && mvn clean package -DskipTests && cd ../..
cd services/service-payment && mvn clean package -DskipTests && cd ../..
cd services/service-ride && mvn clean package -DskipTests && cd ../..
cd services/service-user && mvn clean package -DskipTests && cd ../..

# 构建前端
cd ui && npm install && npm run build && cd ..
```

### 2. 启动服务

```bash
# 使用Docker Compose启动所有服务
docker-compose up -d
```

### 3. 检查服务状态

```bash
# 查看所有服务状态
docker-compose ps
```

## 访问地址

- **前端应用**: http://localhost:8080
- **Nacos 控制台**: http://localhost:8848/nacos (用户名: nacos, 密码: Cf1590753)
- **RabbitMQ 管理界面**: http://localhost:15672 (用户名: guest, 密码: guest)
- **PostgreSQL**: localhost:5432
- **Redis**: localhost:6379

## 部署脚本

项目提供了自动化部署脚本：

- **Linux/Mac**: `deploy.sh`
- **Windows**: `deploy.ps1`

运行部署脚本：

```bash
# Linux/Mac
chmod +x deploy.sh
./deploy.sh

# Windows (PowerShell)
.\deploy.ps1
```

## 环境变量

项目使用 `.env` 文件管理环境变量，主要配置包括：

- 数据库连接信息
- Nacos配置
- Redis配置
- RabbitMQ配置
- JWT密钥

## 故障排除

### 服务启动失败

1. 检查Docker和Docker Compose是否正确安装
2. 确保没有其他服务占用相关端口
3. 检查防火墙设置

### 数据库连接问题

1. 确认PostgreSQL服务正常运行
2. 检查数据库连接参数是否正确

### 前端无法访问API

1. 确认网关服务正常运行
2. 检查Nginx配置是否正确

## 安全建议

1. 生产环境中应使用强密码替换默认密码
2. JWT密钥应定期更换
3. 使用HTTPS加密通信
4. 限制数据库访问权限
5. 定期更新依赖包版本