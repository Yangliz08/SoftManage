package com.softmanage.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.softmanage.workflow.entity.TaskStateHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TaskStateHistoryMapper extends BaseMapper<TaskStateHistory> {

    @Select("SELECT * FROM task_state_history WHERE task_id = #{taskId} ORDER BY created_at DESC")
    List<TaskStateHistory> findByTaskId(@Param("taskId") Long taskId);

    @Select("SELECT * FROM task_state_history WHERE task_id = #{taskId} ORDER BY created_at DESC LIMIT 1")
    TaskStateHistory findLatestByTaskId(@Param("taskId") Long taskId);
}

