package com.liumingyao.usercenter.service;


import com.liumingyao.usercenter.model.entity.User;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;


/**
 * 用户服务测试
 * @author liumingyao
 */
@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    public void testAddUser(){

        User user = new User();
        user.setUsername("testlmy");
        user.setUserAccount("123");
        user.setAvatarUrl("www.c456.com");
        user.setGender(0);
        user.setUserPassword("xxx");
        user.setPhone("123");
        user.setEmail("123");

        boolean result = userService.save(user);
        System.out.println(user.getId());

        Assertions.assertTrue(result);
    }

    @Test
    void userRegister() {
        String userAccount = "zhangsan";
        String userPassword = "123456789";
        String checkUserPassword = "123456789";
        String planetCode = "100";

        long result = userService.userRegister(userAccount, userPassword, checkUserPassword, planetCode);
        Assertions.assertTrue(result > 0);
    }
    @Test
    void testSearchUsersByTags(){
        List<String> tagNameList = Arrays.asList("java","python");
        List<User> users = userService.searchUsersByTags(tagNameList);
        Assert.assertNotNull(users);
    }
}