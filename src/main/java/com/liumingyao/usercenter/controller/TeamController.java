package com.liumingyao.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liumingyao.usercenter.common.BaseResponse;
import com.liumingyao.usercenter.common.ErrorCode;
import com.liumingyao.usercenter.common.ResultUtils;
import com.liumingyao.usercenter.exception.BusinessException;
import com.liumingyao.usercenter.model.dto.TeamQuery;
import com.liumingyao.usercenter.model.entity.Team;
import com.liumingyao.usercenter.model.entity.User;
import com.liumingyao.usercenter.model.entity.UserTeam;
import com.liumingyao.usercenter.model.request.TeamAddRequest;
import com.liumingyao.usercenter.model.request.TeamJoinRequest;
import com.liumingyao.usercenter.model.request.TeamQuitRequest;
import com.liumingyao.usercenter.model.request.TeamUpdateRequest;
import com.liumingyao.usercenter.model.vo.TeamUserVO;
import com.liumingyao.usercenter.service.TeamService;
import com.liumingyao.usercenter.service.UserService;
import com.liumingyao.usercenter.service.UserTeamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/team")
@CrossOrigin(origins = {"http://localhost:3000"})
@Slf4j
public class TeamController {

    @Resource
    private UserService userService;

    @Resource
    private TeamService teamService;

    @Resource
    private UserTeamService userTeamService;

    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request){
        if (teamAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest, team);
        Long teamId = teamService.addTeam(team, loginUser);
        return ResultUtils.success(teamId);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody long teamId, HttpServletRequest request){
        if (teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.deleteTeam(teamId, loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        return ResultUtils.success(true);
    }


    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request){
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.updateTeam(teamUpdateRequest, loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "修改失败");
        }
        return ResultUtils.success(true);
    }

    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(long id){
        if (id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        if (team == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return ResultUtils.success(team);
    }

    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> listTeams(TeamQuery teamQuery, HttpServletRequest request){
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        boolean admin = userService.isAdmin(request);
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery, admin);
        //查询队伍列表
        final List<Long> teamIdList = teamList.stream().map(TeamUserVO::getId).collect(Collectors.toList());
        //判断当前用户是否已加入队伍
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        try{
            User loginUser = userService.getLoginUser(request);
            queryWrapper.eq("userId", loginUser.getId());
            queryWrapper.in("teamId", teamIdList);
            List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
            Set<Long> hasJoinTeamList = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
            teamList.forEach(team -> {
                boolean hasJoin = hasJoinTeamList.contains(team.getId());
                team.setHasJoin(hasJoin);
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        // 查询加入队伍的用户信息（人数）
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        queryWrapper.in("teamId", teamIdList);
        List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
        //队伍id =》加入这个队伍的用户列表
        Map<Long, List<UserTeam>> teamUserList = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        teamList.forEach(team -> {
            team.setHasJoinNum(teamUserList.getOrDefault(team.getId(), new ArrayList<>()).size());
        });

        return ResultUtils.success(teamList);
    }

    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> listTeamsByPage(TeamQuery teamQuery){
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery, team);

        Page page = new Page<Team>(teamQuery.getPageNum(), teamQuery.getPageSize());

        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        Page<Team> teamPage = teamService.page(page, queryWrapper);
        return ResultUtils.success(teamPage);
    }

    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request){
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.joinTeam(teamJoinRequest, loginUser);
        return ResultUtils.success(result);
    }

    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request){
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.quitTeam(teamQuitRequest, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 获取我的队伍
     *
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list/my/create")
    public BaseResponse<List<TeamUserVO>> listMyTeams(TeamQuery teamQuery, HttpServletRequest request){
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean isAdmin = userService.isAdmin(loginUser);
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getId());
        List<Team> teamList = teamService.list(queryWrapper);
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        teamList.forEach(team -> {
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            teamUserVOList.add(teamUserVO);
        });
        return ResultUtils.success(teamUserVOList);
    }

    /**
     * 获取我加入的队伍
     *
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list/my/join")
    public BaseResponse<List<TeamUserVO>> listMyJoinTeams(TeamQuery teamQuery, HttpServletRequest request){
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getId());
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
        //取出不重复的队伍id
        //teamId userId
        //1,2
        //1,3
        //2,3
        //result
        //1 =》 2,3
        //2 =》 3
        if (CollectionUtils.isEmpty(userTeamList)){
            return null;
        }

        List<Long> teamIdList = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toList());
        List<Team> teamList = teamService.listByIds(teamIdList);
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        teamList.forEach(team -> {
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            teamUserVOList.add(teamUserVO);
        });
        return ResultUtils.success(teamUserVOList);
    }

}
