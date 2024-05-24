package com.xiaoyu.initializr.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xiaoyu.initializr.common.base.ResponseMapper;
import com.xiaoyu.initializr.common.request.GenerateReq;
import com.xiaoyu.initializr.entity.Dependency;
import com.xiaoyu.initializr.service.DependencyService;

@RestController
public class DependencyController {

    @Autowired
    private DependencyService dependencyService;

    @PostMapping("api/v1/dependencies")
    public ResponseMapper dependencies(GenerateReq req) throws IOException {
        if (StringUtils.isBlank(req.getLanguage())) {
            req.setLanguage("java");
        }
        Map<String, List<Dependency>> ret = this.dependencyService.dependencies(req);
        return ResponseMapper.createMapper().data(ret);
    }

}
