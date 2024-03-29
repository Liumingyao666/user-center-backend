package com.liumingyao.usercenter.enums;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 队伍状态枚举
 */
@Getter
public enum TeamStatusEnum {

    PUBLIC(0,"公开"),
    PRIVATE(1, "私有"),
    SECRET(2,"加密");

    private int value;

    private String text;

    TeamStatusEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public static TeamStatusEnum getEnumByValue(Integer value){
        if (value == null) {
            return null;
        }
        TeamStatusEnum[] values = TeamStatusEnum.values();
        for (TeamStatusEnum teamStatusEnum: values) {
            if (teamStatusEnum.getValue() == value) {
                return teamStatusEnum;
            }
        }
        return null;
    }
}
