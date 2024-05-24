package com.xiaoyu.initializr.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xiaoyu.initializr.entity.Dependency;

public interface DependencyDao {

    List<Dependency> queryRequired();

    List<Dependency> queryAll();

    List<Dependency> queryByIds(@Param("list") List<Long> dependencyIds);
}