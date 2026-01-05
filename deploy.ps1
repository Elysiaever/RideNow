# RideNow 项目部署脚本 (PowerShell)

Write-Host "开始部署 RideNow 项目..." -ForegroundColor Green

# 检查是否安装了必要的工具
if (!(Get-Command docker -ErrorAction SilentlyContinue)) {
    Write-Host "错误: Docker 未安装或未在PATH中" -ForegroundColor Red
    exit 1
}

if (!(Get-Command docker-compose -ErrorAction SilentlyContinue)) {
    Write-Host "错误: Docker Compose 未安装或未在PATH中" -ForegroundColor Red
    exit 1
}

# 构建所有微服务
Write-Host "构建后端服务..." -ForegroundColor Yellow

# 构建公共模块
Write-Host "构建 common 模块..." -ForegroundColor Cyan
Set-Location ./common
mvn clean install -DskipTests
Set-Location ../..

# 构建各个服务
$services = @("gateway", "services/service-driver", "services/service-matching", "services/service-notification", "services/service-payment", "services/service-ride", "services/service-user")

foreach ($service in $services) {
    Write-Host "构建 $service 服务..." -ForegroundColor Cyan
    Set-Location ./$service
    mvn clean package -DskipTests
    Set-Location ../..
}

# 构建前端
Write-Host "构建前端应用..." -ForegroundColor Yellow
Set-Location ./ui
npm install
npm run build
Set-Location ..

# 启动所有服务
Write-Host "启动所有服务..." -ForegroundColor Yellow
docker-compose up -d

Write-Host "等待服务启动..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

# 检查服务状态
Write-Host "检查服务状态..." -ForegroundColor Yellow
docker-compose ps

Write-Host "RideNow 项目部署完成！" -ForegroundColor Green
Write-Host "访问地址: http://localhost:8080" -ForegroundColor Green
Write-Host "Nacos 控制台: http://localhost:8848/nacos (用户名: nacos, 密码: Cf1590753)" -ForegroundColor Green