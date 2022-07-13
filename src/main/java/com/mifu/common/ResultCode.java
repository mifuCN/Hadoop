package com.mifu.common;

/**
 * 定义返回的状态的常量
 *
 * @Auther: mifu
 * @Date: 2022/07/03 16:59
 * @Description: 自定义状态码
 */

public interface ResultCode {
    //跟前端约定每个状态码表示不同的含义
    public static Integer SUCCESS = 20000; //成功

    public static Integer ERROR = 20001; //失败
}
