package com.xiaoyu.initializr.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Comparator;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xiaoyu.initializr.common.base.ResponseCode;
import com.xiaoyu.initializr.common.base.ResponseMapper;
import com.xiaoyu.initializr.common.request.GenerateReq;
import com.xiaoyu.initializr.service.GenerateService;
import com.xiaoyu.nuwa.utils.AssertUtils;

@Controller
public class GenerateController {

    @Autowired
    private GenerateService generateService;

    @PostMapping("api/v1/generate")
    @ResponseBody
    public ResponseMapper generate(@RequestBody GenerateReq req, HttpServletResponse response) throws IOException {
        if (StringUtils.isBlank(req.getLanguage())) {
            req.setLanguage("java");
        }
        AssertUtils.notBlank(req.getStructName(), "架构模型不能为空");
        AssertUtils.notBlank(req.getArtifactId(), "artifact不能为空");
        AssertUtils.notBlank(req.getGroupId(), "groupId不能为空");
        AssertUtils.notBlank(req.getPackageName(), "包名不能为空");
        AssertUtils.notBlank(req.getProjectName(), "项目名不能为空");

        AssertUtils.isTrue(req.getArtifactId().matches("[a-z]+"), "项目名只能包含小写字母");
        AssertUtils.isTrue(req.getProjectName().matches("[a-z]+"), "项目名只能包含小写字母");
        AssertUtils.isTrue(req.getPackageName().matches("^[a-z]+(\\.[a-z]+)+$"), "包名格式不正确");

        Path path = generateService.generate(req);
        if (path == null) {
            return ResponseMapper.createMapper(ResponseCode.FAILED);
        }

        Path url = generateService.compress(path, req.getProjectName());
        Files.walk(path)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        Object result = transferResult(url);
        Files.deleteIfExists(url);
        return ResponseMapper.createMapper().data(result);
    }

    private Object transferResult(Path path) throws IOException {
        byte[] bytes = Files.readAllBytes(path);
        return Base64.getEncoder().encodeToString(bytes);
    }
}
