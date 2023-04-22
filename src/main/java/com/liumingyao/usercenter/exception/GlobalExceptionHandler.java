package com.liumingyao.usercenter.exception;

import com.liumingyao.usercenter.common.BaseResponse;
import com.liumingyao.usercenter.common.ErrorCode;
import com.liumingyao.usercenter.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 作用：
 * 捕获代码中所有的异常，内部消化，集中处理，让前端更详细的业务报错，
 * 同时屏蔽掉项目框架本身的异常(不暴露服务器内部状态)
 * 集中处理，比如记录日志
 *
 * 实现：
 * Spring AOP :在调用方法前后进行额外的处理
 * @author LiuMingyao
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessExceptionHandler(BusinessException e){
        log.error("businessException" + e.getMessage() , e);
        return ResultUtils.error(e.getCode(), e.getMessage(), e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHandler(RuntimeException e){
        log.error("runtimeException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, e.getMessage());
    }

}
