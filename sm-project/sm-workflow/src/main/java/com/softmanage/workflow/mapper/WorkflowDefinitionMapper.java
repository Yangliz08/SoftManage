package com.softmanage.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.softmanage.workflow.entity.WorkflowDefinition;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WorkflowDefinitionMapper extends BaseMapper<WorkflowDefinition> {
}

