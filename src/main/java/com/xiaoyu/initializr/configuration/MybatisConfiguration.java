package com.xiaoyu.initializr.configuration;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

/**
 * 配置数据库连接池
 */
@Configuration
@MapperScan(basePackages = "com.xiaoyu.initializr.dao", sqlSessionFactoryRef = "sqlSessionFactory")
public class MybatisConfiguration {

    @Bean(name = "transactionManager")
    public DataSourceTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * 设置mybatis相关属性
     */
    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) throws Exception {
        final SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        // 初始化数据库
        factory.setDataSource(dataSource);
        factory.setFailFast(true);
        factory.setVfs(SpringBootVFS.class);
        // 设置别名包
        factory.setTypeAliasesPackage(
                "com.xiaoyu.initializr.dao,;com.xiaoyu.initializr.entity");
        // factory.setTypeAliases(new Class[] { MybatisEncryptHandler.class });
        final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        // 设置mapper映射路径
        factory.setMapperLocations(resolver.getResources("classpath*:mappers/*Dao.xml"));
        org.apache.ibatis.session.Configuration con = new org.apache.ibatis.session.Configuration();
        con.setLogPrefix("");
        con.setMapUnderscoreToCamelCase(true);
        factory.setConfiguration(con);
        return factory.getObject();
    }

    @Bean("sqlSessionTemplate")
    public SqlSessionTemplate qqlSessionTemplate(
            @Qualifier("sqlSessionFactory") SqlSessionFactory sessionfactory) {
        return new SqlSessionTemplate(sessionfactory);
    }

}
