package com.mifu.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: mifu
 * @Date: 2022/07/03 17:02
 * @Description: 统一返回结果类 R
 */
@Data
public class R {

    @ApiModelProperty(value = "是否成功")
    private Boolean success;
    @ApiModelProperty(value = "状态码")
    private Integer code;
    @ApiModelProperty(value = "返回消息")
    private String message;
    @ApiModelProperty(value = "返回数据")
    private Map<String, Object> data = new HashMap<String, Object>();

    //将构造方法私有化，防止别人实例化该对象。不能new对象，只能调用方法
    private R() {
    }

    //成功的静态方法
    public static R ok() {
        R r = new R();  //new一个对象用来存储成功的信息
        r.setSuccess(true); //成功
        r.setCode(ResultCode.SUCCESS);  //返回成功的状态码 20000
        r.setMessage("成功"); //返回成功的信息
        return r;
    }

    //失败的静态方法
    public static R error() {
        R r = new R();  //new一个对象用来存储失败的信息
        r.setSuccess(false);    //失败
        r.setCode(ResultCode.ERROR);    //返回失败的状态码 20001
        r.setMessage("失败"); //返回失败的信息
        return r;
    }

    //失败的静态方法
    public static R error(String message) {
        R r = new R();  //new一个对象用来存储失败的信息
        r.setSuccess(false);    //失败
        r.setCode(ResultCode.ERROR);    //返回失败的状态码 20001
        r.setMessage(message); //返回失败的信息
        return r;
    }

    public R success(Boolean success) {
        this.setSuccess(success);
        return this;    //当前的类对象
    }

    public R message(String message) {
        this.setMessage(message);
        return this;
    }

    public R code(Integer code) {
        this.setCode(code);
        return this;
    }

    public R data(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    public R data(Map<String, Object> map) {
        this.setData(map);
        return this;
    }


}
