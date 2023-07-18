package com.liumingyao.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TeamJoinRequest implements Serializable {
    private static final long serialVersionUID = 3191241716373120793L;

    private Long teamId;

    /**
     * 密码
     */
    private String password;

}
