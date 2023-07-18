package com.liumingyao.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liumingyao.usercenter.common.ErrorCode;
import com.liumingyao.usercenter.enums.TeamStatusEnum;
import com.liumingyao.usercenter.exception.BusinessException;
import com.liumingyao.usercenter.model.dto.TeamQuery;
import com.liumingyao.usercenter.model.entity.Team;
import com.liumingyao.usercenter.model.entity.User;
import com.liumingyao.usercenter.model.entity.UserTeam;
import com.liumingyao.usercenter.model.request.TeamJoinRequest;
import com.liumingyao.usercenter.model.request.TeamUpdateRequest;
import com.liumingyao.usercenter.model.vo.TeamUserVO;
import com.liumingyao.usercenter.model.vo.UserVO;
import com.liumingyao.usercenter.service.TeamService;
import com.liumingyao.usercenter.mapper.TeamMapper;
import com.liumingyao.usercenter.service.UserService;
import com.liumingyao.usercenter.service.UserTeamService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
* @author LiuMingyao
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2023-07-12 14:51:44
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

    @Resource
    private UserTeamService userTeamService;

    @Resource
    private UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //是否登录
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        final long userId = loginUser.getId();
        //队伍人数> 1且<= 20
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum < 1 || maxNum > 20 ) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍人数不满足需求");
        }

        //队伍标题<=20
        String name = team.getName();
        if (StringUtils.isBlank(name) || name.length() > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍标题过长");
        }

        //描述<=512
        String description = team.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "描述过长");
        }

        //status是否公开，不传默认为0(公开)
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (statusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态错误");
        }

        //如果status是加密状态，一定要有密码，且密码 <= 32
        String password = team.getPassword();
        if (TeamStatusEnum.SECRET.equals(statusEnum)) {
            if (StringUtils.isBlank(password) || password.length() > 32) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码设置不正确");
            }
        }

        //超时时长>当前时间
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "超时时长>当前时间");
        }

        //校验用户最多创建5个队伍
        //todo 有bug，可能同时创建100个队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        long count = this.count(queryWrapper);
        if (count >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户最多创建5个队伍");
        }

        //插入队伍信息
        team.setId(null);
        team.setUserId(userId);
        boolean save = this.save(team);
        Long teamId = team.getId();
        if (!save || teamId == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建队伍失败");
        }
        //插入用户队伍关系表
        UserTeam userTeam = UserTeam.builder().userId(userId).teamId(teamId).joinTime(new Date()).build();
        boolean result = userTeamService.save(userTeam);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建队伍失败");
        }
        return team.getId();
    }

    @Override
    public List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();

        if (teamQuery != null) {
            Long id = teamQuery.getId();
            if (id != null && id > 0){
                queryWrapper.eq("id", id);
            }
            String searchText = teamQuery.getSearchText();
            if (StringUtils.isNotBlank(searchText)) {
                queryWrapper.and(qw -> qw.like("name", searchText)).or().like("description", searchText);
            }
            String name = teamQuery.getName();
            if (StringUtils.isNotBlank(name)) {
                queryWrapper.like("name", name);
            }
            String description = teamQuery.getDescription();
            if (StringUtils.isNotBlank(description)) {
                queryWrapper.like("description", description);
            }
            Integer maxNum = teamQuery.getMaxNum();
            //查询最大人数相等的
            if (maxNum != null && maxNum > 0) {
                queryWrapper.eq("maxNum", maxNum);
            }
            Long userId = teamQuery.getUserId();
            //根据创建人来查询
            if (userId != null && userId > 0){
                queryWrapper.eq("userId", userId);
            }
            //根据状态查询,只有管理员才能查看加密还有非公开的房间
            Integer status = teamQuery.getStatus();
            TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
            if (teamStatusEnum == null){
                teamStatusEnum = TeamStatusEnum.PUBLIC;
            }
            if (!isAdmin && !teamStatusEnum.equals(TeamStatusEnum.PUBLIC)){
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
            queryWrapper.eq("status", teamStatusEnum.getValue());

        }
        //不展示已过期的队伍


        List<Team> teamList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(teamList)){
            return new ArrayList<>();
        }
        //关联查询创建人用户信息
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        teamList.forEach(team -> {
            Long userId = team.getUserId();
            if (userId == null) {
                return;
            }
            User user = userService.getById(userId);
            User safetyUser = userService.safetyUser(user);
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            teamUserVO.setCreateUser(userVO);
            teamUserVOList.add(teamUserVO);
        });

        return teamUserVOList;
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        if(teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long id = teamUpdateRequest.getId();
        if (id == null && id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Team oldTeam = getById(id);
        if (oldTeam == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }

        //只有队伍的创建者或管理员可以修改
        if (oldTeam.getUserId() != loginUser.getId() && !userService.isAdmin(loginUser)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }

        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamUpdateRequest.getStatus());
        if (statusEnum.equals(TeamStatusEnum.SECRET)){
            if (StringUtils.isBlank(teamUpdateRequest.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "加密队伍必须设置密码");
            }
        }
        Team updateTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest, updateTeam);

        return this.updateById(updateTeam);
    }

    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {

        if(teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long teamId = teamJoinRequest.getTeamId();
        if (teamId == null || teamId < 1){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍不存在");
        }
        Date expireTime = team.getExpireTime();
        if (expireTime == null || expireTime.after(new Date())){
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍已过期");
        }
        Integer status = team.getStatus();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
        if (TeamStatusEnum.PRIVATE.equals(teamStatusEnum)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "禁止加入私有队伍");
        }
        String password = teamJoinRequest.getPassword();
        if (TeamStatusEnum.SECRET.equals(teamStatusEnum)) {
            if (StringUtils.isBlank(password) || !password.equals(team.getPassword())){
                throw new BusinessException(ErrorCode.NULL_ERROR, "密码错误");
            }
        }
        //该用户已加入的队伍数量
        Long userId = loginUser.getId();
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        long hasJoinNums = userTeamService.count(queryWrapper);
        if (hasJoinNums > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "最多创建5个人的队伍");
        }
        //不能重复加入已加入的队伍
        QueryWrapper<UserTeam> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("userId", userId);
        queryWrapper2.eq("teamId", teamId);
        long hasUserJoinTeam = userTeamService.count(queryWrapper2);
        if (hasUserJoinTeam > 0){
            throw new BusinessException(ErrorCode.NULL_ERROR, "用户已加入该队伍");
        }
        //已加入队伍的人数
        QueryWrapper<UserTeam> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("teamId", teamId);
        long teamHasJoinNum = userTeamService.count(queryWrapper1);
        if (teamHasJoinNum >= team.getMaxNum()){
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍已满");
        }

        //修改队伍信息
        UserTeam userTeam = UserTeam.builder().teamId(teamId).userId(userId).joinTime(new Date()).build();
        return userTeamService.save(userTeam);
    }
}




