# 使用官方OpenJDK 17作为基础镜像
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# 复制打包好的JAR文件到容器中
COPY target/service-user-*.jar app.jar

# 暴露端口
EXPOSE 9005

# 运行应用
ENTRYPOINT ["java", "-jar", "app.jar"]