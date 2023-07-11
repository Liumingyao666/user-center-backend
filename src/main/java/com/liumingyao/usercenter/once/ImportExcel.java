package com.liumingyao.usercenter.once;

import com.alibaba.excel.EasyExcel;

import java.util.List;

/**
 * 导入Excel数据
 */
public class ImportExcel {
    public static void main(String[] args) {
        // 写法1：JDK8+
        // since: 3.0.0-beta1
        String fileName = "E:\\星球项目\\yupao-backend\\src\\main\\resources\\testExcel.xlsx";
//        readByListener(fileName);
        synchronousRead(fileName);
    }

    /**
     * 监听器读取
     * @param fileName
     */
    public static void readByListener(String fileName) {
        EasyExcel.read(fileName, XingQiuUserInfo.class, new TableListener()).sheet().doRead();
    }

    /**
     * 同步读
     * @param fileName
     */
    public static void synchronousRead(String fileName) {
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
        List<XingQiuUserInfo> totalDataList =
                EasyExcel.read(fileName).head(XingQiuUserInfo.class).sheet().doReadSync();
        for (XingQiuUserInfo XingQiuUserInfo : totalDataList) {
            System.out.println(XingQiuUserInfo);
        }
    }
}
