package com.xiaoyu.initializr.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.UnixStat;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.xiaoyu.initializr.common.Constants;
import com.xiaoyu.initializr.common.base.ResponseCode;
import com.xiaoyu.initializr.common.request.GenerateReq;
import com.xiaoyu.initializr.dao.DependencyDao;
import com.xiaoyu.initializr.dao.TemplateDao;
import com.xiaoyu.initializr.entity.Dependency;
import com.xiaoyu.initializr.entity.Template;
import com.xiaoyu.initializr.enums.StructEnum;
import com.xiaoyu.nuwa.utils.cache.SimpleLocalCacheUtils;
import com.xiaoyu.nuwa.utils.exception.CommonException;
import com.samskivert.mustache.Mustache;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GenerateService {

    @Autowired
    private TemplateDao templateDao;

    @Autowired
    private DependencyDao dependencyDao;

    public Path generate(GenerateReq req) throws IOException {
        if ("java".equals(req.getLanguage())) {
            return this.generateJava(req);
        }
        return null;
    }

    private Path generateJava(GenerateReq req) throws IOException {
        List<Dependency> requiredList = this.dependencyDao.queryRequired();
        Set<Long> filter = new HashSet<>();
        requiredList.forEach(a -> {
            filter.add(a.getId());
        });
        if (req.getDependencyIds() != null && !req.getDependencyIds().isEmpty()) {
            List<Long> ids = new ArrayList<>(req.getDependencyIds().size());
            req.getDependencyIds().forEach(a -> {
                if (!filter.contains(a.longValue())) {
                    ids.add(a);
                }
            });
            if (!ids.isEmpty()) {
                List<Dependency> depens = this.dependencyDao.queryByIds(ids);
                if (!depens.isEmpty()) {
                    requiredList.addAll(depens);
                }
            }
        }
        if (StructEnum.Single.getName().equals(req.getStructName())) {
            return doGenerateSingle(req, requiredList);
        }
        return doGenerateMulti(req, requiredList);
    }

    private Map<String, Object> getInitParamsMap(GenerateReq req, List<Dependency> depenList) {
        Map<String, Object> initParamMap = new HashMap<>(32);
        initParamMap.put("projectName", req.getProjectName());
        initParamMap.put("bootVersion", Constants.BootVersion);
        initParamMap.put("groupId", req.getGroupId());
        initParamMap.put("artifactId", req.getArtifactId());
        initParamMap.put("description", req.getDescription());
        initParamMap.put("packageName", req.getPackageName());
        initParamMap.put("isSingle", StructEnum.Single.getName().equals(req.getStructName()));
        initParamMap.put("mysql", false);
        initParamMap.put("mybatis", false);
        initParamMap.put("redis", false);
        StringBuilder den = new StringBuilder();
        if (depenList != null && !depenList.isEmpty()) {
            for (Dependency en : depenList) {
                // packName做为配置文件里面判断依据
                if (StringUtils.isNotBlank(en.getPackName())) {
                    initParamMap.put(en.getPackName(), true);
                }
                // 属于插件 不是真的pom依赖
                if (StringUtils.isBlank(en.getArtifactId())) {
                    continue;
                }
                den.append("        <!-- ").append(en.getArtifactId()).append("-->").append("\n");
                den.append("        <dependency>").append("\n");
                den.append("            <groupId>").append(en.getGroupId()).append("</groupId>").append("\n");
                den.append("            <artifactId>").append(en.getArtifactId()).append("</artifactId>").append("\n");
                if (StringUtils.isNotBlank(en.getVersion())) {
                    den.append("            <version>").append(en.getVersion()).append("</version>").append("\n");
                }
                if (StringUtils.isNotBlank(en.getScope())) {
                    den.append("            <scope>").append(en.getScope()).append("</scope>").append("\n");
                }
                den.append("        </dependency>").append("\n");
            }
        }
        initParamMap.put("dependencies", den.toString());
        return initParamMap;
    }

    private Path doGenerateMulti(GenerateReq req, List<Dependency> depenList) throws IOException {
        Map<String, Object> initParamMap = getInitParamsMap(req, depenList);
        // 这里返回的tem没有content
        List<Template> temList = this.templateDao.queryByStructType(StructEnum.Mvc.getType());
        if (temList.isEmpty()) {
            return null;
        }
        // 加上套装
        temList.addAll(getTemplateByPack(req, depenList));
        // 生成基础结构
        Path projectPath = this.createBaseStruct(req);
        // 按模版生成文件
        for (Template t : temList) {
            Template tem = getTemplate(t.getId());
            Path path = getPath(tem, projectPath, initParamMap);
            this.createFile(tem, path, initParamMap);
        }
        return projectPath;
    }

    private Path doGenerateSingle(GenerateReq req, List<Dependency> depenList) throws IOException {
        Map<String, Object> initParamMap = getInitParamsMap(req, depenList);
        // 这里返回的tem没有content
        List<Template> temList = this.templateDao.queryByStructType(StructEnum.Single.getType());
        if (temList.isEmpty()) {
            return null;
        }
        // 加上套装
        temList.addAll(getTemplateByPack(req, depenList));
        // 生成基础结构
        Path projectPath = this.createBaseStruct(req);
        // 按模版生成文件
        for (Template t : temList) {
            Template tem = getTemplate(t.getId());
            Path path = getPath(tem, projectPath, initParamMap);
            this.createFile(tem, path, initParamMap);
        }
        return projectPath;
    }

    // 查询套装需要的模版
    private List<Template> getTemplateByPack(GenerateReq req, List<Dependency> depenList) {
        if (depenList == null || depenList.isEmpty()) {
            return new ArrayList<>(0);
        }
        List<Template> totalTems = new ArrayList<>();
        Set<String> filter = new HashSet<>();
        int structType = StructEnum.Single.getName().equals(req.getStructName()) ? StructEnum.Single.getType()
                : StructEnum.Mvc.getType();
        for (Dependency de : depenList) {
            if (filter.contains(de.getPackName())) {
                continue;
            }
            List<Template> tems = this.templateDao.queryByPackName(de.getPackName(), structType);
            if (!tems.isEmpty()) {
                totalTems.addAll(tems);
            }
        }
        return totalTems;
    }

    private void createFile(Template tem, Path subPath, Map<String, Object> initParamMap) throws IOException {
        if (StringUtils.isNotBlank(tem.getParams())) {
            JSONObject jo = JSON.parseObject(tem.getParams());
            jo.entrySet().forEach(en -> {
                initParamMap.put(en.getKey(), en.getValue());
            });
        }
        String ret = Mustache.compiler().compile(tem.getContent()).execute(initParamMap);
        Path path = Paths.get(subPath.toString(), tem.getName());
        Files.write(path, ret.getBytes(), StandardOpenOption.CREATE);
    }

    private Path getPath(Template tem, Path projectPath, Map<String, Object> initParamMap) throws IOException {
        if (StringUtils.isBlank(tem.getPath())) {
            return projectPath;
        }
        String retPath = Mustache.compiler().compile(tem.getPath()).execute(initParamMap);
        String pathStr = retPath.replace(".", File.separator);
        Path path = Paths.get(projectPath.toString(), pathStr);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        return path;
    }

    private Path createBaseStruct(GenerateReq req) throws IOException {
        // 生成项目临时目录
        Path projectPath = Files.createTempDirectory("initializr_" + req.getProjectName());
        log.info("generate project {} in {}", req.getProjectName(), projectPath.toString());
        String[] prefixArr = null;
        if (StructEnum.Single.getName().equals(req.getStructName())) {
            prefixArr = Arrays.array("");
        } else {
            prefixArr = Arrays.array(req.getProjectName() + "-web" + File.separator,
                    req.getProjectName() + "-service" + File.separator,
                    req.getProjectName() + "-common" + File.separator);
        }
        for (String prefix : prefixArr) {
            // 创建java主目录
            Path subJavaPath = projectPath
                    .resolve(prefix + "src" + File.separator + "main" + File.separator + "java");
            Files.createDirectories(subJavaPath);
            // 创建test目录
            Path subTestPath = projectPath
                    .resolve(prefix + "src" + File.separator + "test" + File.separator + "java");
            Files.createDirectories(subTestPath);
            // 创建package
            Path subPackagePath = subJavaPath
                    .resolve(req.getPackageName().replace(".", File.separator));
            Files.createDirectories(subPackagePath);
            // 创建资源目录
            if (!prefix.equals(req.getProjectName() + "-common" + File.separator)) {
                Path subResourcePath = projectPath
                        .resolve(prefix + "src" + File.separator + "main" + File.separator + "resources");
                Files.createDirectories(subResourcePath);
            }
        }

        return projectPath;
    }

    private Template getTemplate(Long temId) {
        String key = "template_" + temId;
        Template cache = SimpleLocalCacheUtils.getCache(key);
        if (cache != null) {
            return cache;
        }
        Template tem = doGetTemplate(temId, false);
        if (StringUtils.isBlank(tem.getContent())) {
            // 补充模版内容
            Template relate = doGetTemplate(tem.getRelativeId(), true);
            tem.setContent(relate.getContent());
        }
        SimpleLocalCacheUtils.setCache(key, tem);
        return tem;
    }

    private Template doGetTemplate(Long temId, boolean doCache) {
        String key = "template_" + temId;
        Template cache = SimpleLocalCacheUtils.getCache(key);
        if (cache != null) {
            return cache;
        }
        Template tem = this.templateDao.getById(temId);
        if (tem == null) {
            throw new CommonException(ResponseCode.Args_Error, "模版id不存在->" + temId);
        }
        if (doCache) {
            SimpleLocalCacheUtils.setCache(key, tem);
        }
        return tem;
    }

    public Path compress(Path projectPath, String projectName) throws IOException {
        Path archive = projectPath.resolveSibling(projectName + ".zip");
        try (ArchiveOutputStream output = new ZipArchiveOutputStream(Files.newOutputStream(archive))) {
            Files.walk(projectPath).filter((path) -> !projectPath.equals(path))
                    .forEach((path) -> {
                        try {
                            String entryName = getEntryName(projectPath, path);
                            ZipArchiveEntry entry = new ZipArchiveEntry(path.toFile(), entryName);
                            entry.setUnixMode(getUnixMode(entryName, path));
                            output.putArchiveEntry(entry);
                            if (!Files.isDirectory(path)) {
                                Files.copy(path, output);
                            }
                            output.closeArchiveEntry();
                        } catch (IOException ex) {
                            throw new IllegalStateException(ex);
                        }
                    });
        }
        log.info("compress path->{}", archive.toString());
        return archive;
    }

    private String getEntryName(Path root, Path path) {
        String entryName = root.relativize(path).toString().replace('\\', '/');
        if (Files.isDirectory(path)) {
            entryName += "/";
        }
        return entryName;
    }

    private int getUnixMode(String entryName, Path path) {
        if (Files.isDirectory(path)) {
            return UnixStat.DIR_FLAG | UnixStat.DEFAULT_DIR_PERM;
        }
        return UnixStat.FILE_FLAG | UnixStat.DEFAULT_FILE_PERM;
    }

}
