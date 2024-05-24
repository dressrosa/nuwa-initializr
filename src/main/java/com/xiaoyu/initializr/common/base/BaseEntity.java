/**
 * copyright com.xiaoyu
 */
package com.xiaoyu.initializr.common.base;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 基本实体类参数配置
 *
 */
@Getter
@Setter
@Accessors(chain = true)
public class BaseEntity {

    /**
     * 唯一主键
     */
    private Long id;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}
