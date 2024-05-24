package com.xiaoyu.initializr.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaoyu.initializr.common.request.GenerateReq;
import com.xiaoyu.initializr.dao.DependencyDao;
import com.xiaoyu.initializr.entity.Dependency;
import com.xiaoyu.nuwa.utils.cache.SimpleLocalCacheUtils;

@Service
public class DependencyService {

    @Autowired
    private DependencyDao dependencyDao;

    public Map<String, List<Dependency>> dependencies(GenerateReq req) throws IOException {
        List<Dependency> depenList = new ArrayList<>(0);
        if ("java".equals(req.getLanguage())) {
            depenList = this.queryForJava(req);
        }
        Map<String, List<Dependency>> totalMap = new HashMap<>();
        depenList.forEach(d -> {
            List<Dependency> subList = totalMap.get(d.getCatalog());
            if (subList == null) {
                subList = new ArrayList<>();
                totalMap.put(d.getCatalog(), subList);
            }
            subList.add(d);
        });
        return totalMap;
    }

    private List<Dependency> queryForJava(GenerateReq req) throws IOException {
        String key = req.getLanguage() + "_dependencies";
//        List<Dependency> cache = SimpleLocalCacheUtils.getCache(key);
//        if (cache != null) {
//            return cache;
//        }
        List<Dependency> dlist = this.dependencyDao.queryAll();
        if (!dlist.isEmpty()) {
            SimpleLocalCacheUtils.setCache(key, dlist);
        }
        return dlist;
    }

}
