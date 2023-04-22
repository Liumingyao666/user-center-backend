package com.liumingyao.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author liumingyao
 */

@Data
public class UserRegisterRequest implements Serializable {


    private static final long serialVersionUID = 640293378943611614L;

    private String userAccount;

    private String userPassword;

    private String checkUserPassword;

    private String planetCode;


}
