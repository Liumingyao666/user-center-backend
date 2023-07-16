package com.liumingyao.usercenter.service;

import com.liumingyao.usercenter.model.entity.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liumingyao.usercenter.model.entity.User;

/**
* @author LiuMingyao
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2023-07-12 14:51:44
*/
public interface TeamService extends IService<Team> {

    /**
     * 创建队伍
     *
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, User loginUser);

}
