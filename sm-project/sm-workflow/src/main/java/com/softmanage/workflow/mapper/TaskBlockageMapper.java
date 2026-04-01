package com.softmanage.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.softmanage.workflow.entity.TaskBlockage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TaskBlockageMapper extends BaseMapper<TaskBlockage> {

    @Select("SELECT * FROM task_blockage WHERE task_id = #{taskId} AND status = 'BLOCKED' ORDER BY blocked_at DESC LIMIT 1")
    TaskBlockage findActiveBlockage(@Param("taskId") Long taskId);
}

