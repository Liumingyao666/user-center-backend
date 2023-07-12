package com.liumingyao.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liumingyao.usercenter.model.entity.Team;
import com.liumingyao.usercenter.service.TeamService;
import com.liumingyao.usercenter.mapper.TeamMapper;
import org.springframework.stereotype.Service;

/**
* @author LiuMingyao
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2023-07-12 14:51:44
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

}




