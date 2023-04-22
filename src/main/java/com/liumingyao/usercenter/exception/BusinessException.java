package com.liumingyao.usercenter.exception;

import com.liumingyao.usercenter.common.ErrorCode;

/**
 * 自定义异常类
 * 相对于Java的异常类，支持更多字段
 * 自定义构造函数，更灵活的设置字段
 *
 * @author liumingyao
 */
public class BusinessException extends RuntimeException{

    private final int code;

    private final String description;

    public BusinessException(String message, int code, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    public BusinessException(ErrorCode errorCode, String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
