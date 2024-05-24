package com.xiaoyu.initializr.entity;

import com.xiaoyu.initializr.common.base.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Dependency extends BaseEntity {

    private String groupId;
    private String artifactId;
    private String version;
    private String scope;
    private String catalog;
    private String language;
    private Integer genOption;
    private String packName;
    private String description;

}