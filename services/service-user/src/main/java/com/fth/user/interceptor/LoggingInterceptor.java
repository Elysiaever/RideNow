package com.fth.user.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoggingInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    // 在请求处理前调用
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.info("请求URL: {}", request.getRequestURI());
        logger.info("请求方法: {}", request.getMethod());
        logger.info("客户端IP: {}", request.getRemoteAddr());
        // 返回true表示继续处理请求，false表示中断
        return true;
    }

    // 在请求处理后调用（视图渲染前）
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           org.springframework.web.servlet.ModelAndView modelAndView) throws Exception {
        logger.info("响应状态码: {}", response.getStatus());
    }

    // 在整个请求完成后调用（视图渲染后）
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (ex != null) {
            logger.error("请求处理异常", ex);
        }
        logger.info("请求处理完成: {}", request.getRequestURI());
    }
}