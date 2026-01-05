# 第一阶段：构建前端应用
FROM node:20-alpine AS build

# 设置工作目录
WORKDIR /app

# 复制package文件并安装依赖
COPY package*.json ./
RUN npm install

# 复制源代码并构建
COPY . .
RUN npm run build

# 第二阶段：使用Nginx提供静态文件服务
FROM nginx:alpine

# 复制构建好的前端文件到nginx的html目录
COPY --from=build /app/dist /usr/share/nginx/html

# 复制nginx配置（如果存在）
COPY nginx.conf /etc/nginx/nginx.conf

# 暴露端口
EXPOSE 80

# 启动nginx
CMD ["nginx", "-g", "daemon off;"]