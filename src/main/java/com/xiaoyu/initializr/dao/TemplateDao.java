package com.xiaoyu.initializr.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xiaoyu.initializr.entity.Template;

public interface TemplateDao {

    List<Template> queryByPackName(@Param("packName") String packName, @Param("structType") int structType);

    Template getById(@Param("id") Long id);

    List<Template> queryByStructType(@Param("structType") Integer structType);
}