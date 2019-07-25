package com.bin.kong.proxy.model.repeater.enums;

public enum MockStatusEnum {
    //状态：0.未命中 1. 命中
    NO_MOCK(0, "未命中"), IS_MOCK(1, "命中");

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


    MockStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static MockStatusEnum getByCode(int code) {
        for (MockStatusEnum statusEnum : values()) {
            if (statusEnum.getCode() == code) {
                return statusEnum;
            }
        }
        return null;
    }

    public static String getDescByCode(int code) {
        for (MockStatusEnum statusEnum : values()) {
            if (statusEnum.getCode() == code) {
                return statusEnum.getDesc();
            }
        }
        return null;
    }
}
