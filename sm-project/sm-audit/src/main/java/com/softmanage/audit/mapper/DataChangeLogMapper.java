package com.softmanage.audit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.softmanage.audit.entity.DataChangeLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DataChangeLogMapper extends BaseMapper<DataChangeLog> {
}

