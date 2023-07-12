package com.liumingyao.usercenter.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liumingyao.usercenter.model.User;
import com.liumingyao.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存预热任务
 *
 */
@Component
@Slf4j
public class PreCacheJob {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RedissonClient redissonClient;


    //重点用户
    private List<Long> mainUserList = Arrays.asList(1L);


    //每天执行，预热加载推荐用户
    @Scheduled(cron = "0 59 23 * * ? ")
    public void doCacheRecommendUser(){
        RLock lock = redissonClient.getLock("yupao:preCachejob:docache:lock");
        try {
            //只有一个线程能获取到值
            if (lock.tryLock(0, 30000, TimeUnit.MILLISECONDS)) {
                for (Long userId: mainUserList) {
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);
                    String redisKey = String.format("yupao:user:recommend:%s", userId);
                    //写缓存
                    try{
                        redisTemplate.opsForValue().set(redisKey,userPage, 300000, TimeUnit.MILLISECONDS);
                    }catch (Exception e) {
                        log.error("redis set key error", e);
                    }
                }
            }
        }catch (InterruptedException e){
            log.error("doCacheRecommendUser error", e);
        }finally {
            //只能释放自己的锁，写在finally，写在try中报错就不执行了
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}
