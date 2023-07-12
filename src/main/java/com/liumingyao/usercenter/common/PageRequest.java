package com.liumingyao.usercenter.common;

import lombok.Data;

/**
 * 通用分页请求参数
 */
@Data
public class PageRequest {

    /**
     * 页面大小
     */
    protected int pageSize = 10;

    /**
     * 当前是第几页
     */
    protected int pageNum = 1;
}
