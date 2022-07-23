package com.jielu.mybatis.plus;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Collection;

/**
 * @param <T>
 */
public interface CustomBaseMapper<T> extends BaseMapper<T> {

    /**
     * 批量插入
     * @param collections
     * @return
     */
    int insertBatch(Collection<T> collections);
}
