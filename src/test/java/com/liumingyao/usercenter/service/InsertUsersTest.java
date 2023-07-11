package com.liumingyao.usercenter.service;

import com.liumingyao.usercenter.mapper.UserMapper;
import com.liumingyao.usercenter.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest
public class InsertUsersTest {
    @Resource
    private UserService userService;

    /**
     * 60: 当前线程池能同时运行多少线程
     * 1000：最大多少线程
     * 10000： 线程存活时间
     * 时间单位: 线程多久没有用会回收掉
     * 任务队列：同时塞10000个任务
     * 任务策略
     *
     *
     * CPU密集型：分配的核心线程数 = CPU - 1
     * IO密集型：分配的核心线程数可以大于CPU核数
     * 以核心业务为准
     */
    private ExecutorService executorService = new ThreadPoolExecutor(60, 1000, 10000, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));

    /**
     * 批量插入用户
     */
    @Test
    public void doInsertUsers(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 100;
        List<User> users = new ArrayList<>();
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("假用户");
            user.setUserAccount("fakeyupi");
            user.setAvatarUrl("https://img.wxcha.com/m00/90/a5/d1d167451213c94da52f7ddf31d8da3b.jpg");
            user.setGender(0);
            user.setUserPassword("123456789");
            user.setPhone("123");
            user.setEmail("123@qq.com");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setPlanetCode("11111");
            user.setTags("[]");
            user.setProfile("11111");
            users.add(user);
        }
        userService.saveBatch(users, 20);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    /**
     * 多线程并发批量插入用户
     * 10w条数据
     * 问题：20个线程，40个线程处理的时间相同。
     * 原因：因为我们使用的默认线程池
     * 20个线程中只有15(16-1 和电脑CPU有关)个线程在干活，有5个线程在干两份活
     * 而40个线程也并非是同时有40个线程在干活，有更多的线程需要干两份活
     *
     * 解决：自己新建线程池
     */
    @Test
    public void doConcurrencyInsertUsers(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        //10w条数据分十组，20个线程插入, 每个线程处理5000条
        int batchSize = 5000;
        int j = 0;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            List<User> userList = new ArrayList<>();
            while (true) {
                j++;
                User user = new User();
                user.setUsername("假用户");
                user.setUserAccount("fakeyupi");
                user.setAvatarUrl("https://img.wxcha.com/m00/90/a5/d1d167451213c94da52f7ddf31d8da3b.jpg");
                user.setGender(0);
                user.setUserPassword("123456789");
                user.setPhone("123");
                user.setEmail("123@qq.com");
                user.setUserStatus(0);
                user.setUserRole(0);
                user.setPlanetCode("11111");
                user.setTags("[]");
                user.setProfile("11111");
                userList.add(user);

                //插入条数为5000倍数，退出循环
                if (j % batchSize == 0) {
                    break;
                }
            }
            //异步执行
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println("threadName:" + Thread.currentThread().getName());
                //批量插入，每次存5000条
                userService.saveBatch(userList, batchSize);
            }, executorService);
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
