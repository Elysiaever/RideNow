package com.fth.common.core.constant;

/**
 * 异常消息常量类：统一管理系统中所有业务异常的提示信息
 */
public class ExceptionConstant {

    // ====================== 认证与授权相关 ======================
    public static final String TOKEN_INVALID = "Token无效或已过期";
    public static final String TOKEN_PARSE_ERROR = "Token解析失败";
    public static final String TOKEN_EMPTY = "Token不能为空";
    public static final String AUTHENTICATION_FAILED = "认证失败，请重新登录";

    // ====================== 用户相关 ======================
    public static final String USER_NOT_FOUND = "用户名不存在";
    public static final String PASSWORD_ERROR = "密码错误";
    public static final String USERNAME_EXIST = "用户名已存在";
    public static final String USER_DISABLED = "用户已被禁用";

    // ====================== 通用业务相关 ======================
    public static final String PARAM_VALID_ERROR = "参数校验失败";
    public static final String RESOURCE_NOT_FOUND = "资源不存在";
    public static final String OPERATION_FAILED = "操作失败";
    public static final String SYSTEM_ERROR = "系统内部异常，请联系管理员";

    // ====================== 扩展场景（根据业务补充） ======================
    public static final String DATA_ACCESS_DENIED = "数据访问权限不足";
    public static final String REQUEST_FREQUENCY_LIMIT = "请求过于频繁，请稍后再试";

}