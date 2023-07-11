package com.liumingyao.usercenter.once;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TableListener implements ReadListener<XingQiuUserInfo> {

    /**
     * 每一条数据来解析都会调用
     * @param data
     * @param context
     */
    @Override
    public void invoke(XingQiuUserInfo data, AnalysisContext context) {
        System.out.println(data);
    }


    /**
     * 所有数据解析完成，来调用
     * @param analysisContext
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        System.out.println("已解析完成");
    }
}
