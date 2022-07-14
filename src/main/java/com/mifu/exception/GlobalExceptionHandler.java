package com.mifu.exception;
/**
 * 全局异常处理器(全局响应处理器)
 * 主要作用让前端得到详细的返回信息
 * 后面优化前端的时候发现   这里其实没有很多地方用到这个 有点刻意了  主要是用来学习吧
 *
 * 但是后端做到这一步   也只能在f12中查看返回的信息  不能直接在前端页面返回提示
 * 那么现在先去优化前端代码 一步一步来
 * todo 完成全局异常处理器后 后期补充上全局请求日志和登录校验
 *
 * @author mifuRD
 */

import com.mifu.common.BaseResponse;
import com.mifu.common.ErrorCode;
import com.mifu.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice //这个是一个Spring的切面功能(面向切面编程)  Spring AOP:在调用方法前后进行额外的处理
@Slf4j //提供log对象
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class) //这个注解接收的参数  必须是一个继承异常接口(Throwable)的参数
    public BaseResponse businessExceptionHandler(BusinessException e) { //这个就是这个方法只捕获BusinessException中的异常
        log.error("BusinessException: " + e.getMessage(), e);
        return ResultUtils.error(e.getCode(), e.getMessage(), e.getDescription());
    }


    public BaseResponse runtimeExceptionHandler(RuntimeException e) { //那这个方法只捕获RuntimeException中的异常
        log.error("runtimeException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, e.getMessage(), "");
    }
}
