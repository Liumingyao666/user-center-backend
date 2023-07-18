package com.liumingyao.usercenter.service;

import com.liumingyao.usercenter.model.dto.TeamQuery;
import com.liumingyao.usercenter.model.entity.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liumingyao.usercenter.model.entity.User;
import com.liumingyao.usercenter.model.request.TeamJoinRequest;
import com.liumingyao.usercenter.model.request.TeamUpdateRequest;
import com.liumingyao.usercenter.model.vo.TeamUserVO;

import java.util.List;

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

    /**
     * 查询队伍列表
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery,  boolean isAdmin);

    /**
     * 更新队伍
     *
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    /**
     * 加入队伍
     *
     * @param teamJoinRequest
     * @param loginUser
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);
}
