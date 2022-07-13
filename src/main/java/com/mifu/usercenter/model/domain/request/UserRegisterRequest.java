package com.mifu.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author mifuRD
 */
@Data //引用lombok的Data注解 会自动生成get set方法
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = -3196081511715116918L;

    private String userAccount;
    private String userPassword;
    private String checkPassword;
    private String planetCode;


}
