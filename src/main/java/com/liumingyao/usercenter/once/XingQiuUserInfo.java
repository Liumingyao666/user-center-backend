package com.liumingyao.usercenter.once;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 星球表格用户信息，与表头映射
 */
@Data
public class XingQiuUserInfo {

    /**
     * 成员编号
     */
    @ExcelProperty("成员编号")
    private String planetCode;

    /**
     * 用户呢称
     */
    @ExcelProperty("成员呢称")
    private String userName;

}
