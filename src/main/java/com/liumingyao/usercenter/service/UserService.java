package com.liumingyao.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liumingyao.usercenter.model.User;

import javax.servlet.http.HttpServletRequest;

/**
* @author LiuMingyao
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2023-04-14 19:07:52
*/
public interface UserService extends IService<User> {



    /**
     * 用户注册
     * @param userAccount 账户
     * @param userPassword 密码
     * @param checkUserPassword 校验密码
     * @param planetCode 星球编号
     * @return 用户id
     */
    long userRegister(String userAccount, String userPassword, String checkUserPassword, String planetCode);

    /**
     * 用户登录
     * @param userAccount 账户
     * @param userPassword 密码
     * @return 用户(脱敏)
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     * @param user
     * @return
     */
    User safetyUser(User user);

    /**
     * 用户注销
     * @return
     */
    int userLogout(HttpServletRequest request);
}
