package com.liumingyao.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liumingyao.usercenter.model.User;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.liumingyao.usercenter.contant.UserConstant.ADMIN_ROLE;
import static com.liumingyao.usercenter.contant.UserConstant.USER_LOGIN_STATE;

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

    /**
     * 根据标签来搜索用户
     * @param tagNameList 用户要拥有的标签
     * @return
     */
    List<User> searchUsersByTags(List<String> tagNameList);

    /**
     * 更改用户信息
     * @param user
     * @return
     */
    int updateUser(User user, User loginUser);

    /**
     * 获取当前用户登录信息
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);


    /**
     * 是否为管理员
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    boolean isAdmin(User loginUser);
}
