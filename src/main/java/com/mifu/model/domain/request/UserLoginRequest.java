package com.mifu.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体
 * 接收前端发送过来的登录数据
 *
 * @author mifuRD
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = -3196081511715116918L;

    private String userAccount;
    private String userPassword;


}
