package com.fth.common.api;

import lombok.Data;

@Data
public class Response<T> {

    private int code;       // 状态码
    private String message; // 错误信息（成功为空）
    private T data;         // 返回数据（失败为空）

    // 成功返回
    public static <T> Response<T> success(T data) {
        Response<T> r = new Response<>();
        r.setCode(200);
        r.setMessage(null);
        r.setData(data);
        return r;
    }

    // 失败返回
    public static <T> Response<T> error(int code, String msg) {
        Response<T> r = new Response<>();
        r.setCode(code);
        r.setMessage(msg);
        r.setData(null);
        return r;
    }
}
