package com.xiaoyu.initializr.entity;

import com.xiaoyu.initializr.common.base.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Template extends BaseEntity {

    private String language;
    private Integer genOption;
    private String name;
    private String params;
    private String path;

    private String packName;

    private String content;

    private Long relativeId;

    private Integer structType;

}