package com.liumingyao.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户退出队伍请求体
 *
 */
@Data
public class TeamQuitRequest implements Serializable {
    private static final long serialVersionUID = 3191241716373120793L;

    private Long teamId;


}
