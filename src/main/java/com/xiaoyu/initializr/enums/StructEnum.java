package com.xiaoyu.initializr.enums;

public enum StructEnum {

    /**
     * 单模块
     */
    Single("single", 1),

    /**
     * mvc
     */
    Mvc("mvc", 2),;

    String name;

    int type;

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    StructEnum(String name, int type) {
        this.type = type;
        this.name = name;
    }

}
