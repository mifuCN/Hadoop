package com.mifu.usercenter.exception;

import com.mifu.usercenter.common.ErrorCode;

/**
 * 自定义异常类
 * 其实就是为了更方便的抛异常
 *
 * @author mifuRD
 */
//继承运行时异常 就可以不在java的代码中显示的去捕获它 就不需要try cache 或者 throw
public class BusinessException extends RuntimeException {

    private final int code;
    private final String description;

    public BusinessException(String message, int code, String description) { //直接调用runtime的构造函数
        super(message);
        this.code = code;
        this.description = description;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    public BusinessException(ErrorCode errorCode, String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
