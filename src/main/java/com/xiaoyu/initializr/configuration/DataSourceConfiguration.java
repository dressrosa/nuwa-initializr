package com.xiaoyu.initializr.configuration;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.Setter;

/**
 * 配置数据库连接池
 */
@Configuration
@ConfigurationProperties(prefix = "spring.datasource")
@Setter
public class DataSourceConfiguration {

    private String username;
    private String password;
    private String url;

    private String driverClassName;
    private int maximumPoolSize;
    private int minimumIdle;

    /**
     * 初始化Hikari连接池
     */
    @Bean(name = "dataSource")
    public DataSource initDataSource() {
        HikariConfig config = new HikariConfig();
        config.setMaximumPoolSize(maximumPoolSize);
        config.setMinimumIdle(minimumIdle);
        config.setDriverClassName(driverClassName);
        config.setUsername(username);
        config.setPassword(password);
        config.setJdbcUrl(url);
        return new HikariDataSource(config);
    }

}