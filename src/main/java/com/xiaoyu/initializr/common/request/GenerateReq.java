package com.xiaoyu.initializr.common.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerateReq {

    private String language;
    private String groupId;
    private String artifactId;
    private String projectName;
    private String description;
    private String packageName;
    private String structName;

    private List<Long> dependencyIds;
}
