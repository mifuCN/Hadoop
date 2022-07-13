package com.mifu.common;

/**
 * 定义一个枚举
 * 全局错误码
 *
 * @author mifuRD
 */

public enum ErrorCode {
    SUCCESS(0, "OK", ""),
    PARAMS_ERROR(40000, "请求参数错误", ""),
    NULL_ERROR(40001, "请求数据为空", ""),
    NOT_LOGIN(40100, "未登录", ""),
    NO_AUTH(40101, "无权限", ""),
    SYSTEM_ERROR(50000, "系统内部异常", "");
    private final int code;
    /**
     * 状态码信心
     */
    private final String message;
    /**
     * 状态码描述(详情)
     */
    private final String description;//描述

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() { //枚举值是不支持set的 所以这里不能用@Data注解
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
