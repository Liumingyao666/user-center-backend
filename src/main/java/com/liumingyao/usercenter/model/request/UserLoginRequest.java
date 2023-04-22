package com.liumingyao.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体
 *
 * @author
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = -2778288204672206728L;

    private String userAccount;

    private String userPassword;
}
