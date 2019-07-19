package com.bin.kong.proxy.model.env.enums;

import com.bin.kong.proxy.model.proxy.enums.UserInfoStatusEnum;

public enum EnvStatusEnum {
    //状态：-1.停止 1. 开始
    STOP(-1, "停止"),  START(1, "开启");

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    private int code;
    private String desc;


    EnvStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static EnvStatusEnum getByCode(int code) {
        for (EnvStatusEnum statusEnum : values()) {
            if (statusEnum.getCode() == code) {
                return statusEnum;
            }
        }
        return null;
    }

    public static String getDescByCode(int code) {
        for (EnvStatusEnum statusEnum : values()) {
            if (statusEnum.getCode() == code) {
                return statusEnum.getDesc();
            }
        }
        return null;
    }
}
