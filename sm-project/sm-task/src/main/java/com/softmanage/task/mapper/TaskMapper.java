package com.softmanage.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.softmanage.task.entity.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TaskMapper extends BaseMapper<Task> {

    @Select("SELECT * FROM task WHERE project_id = #{projectId} AND deleted = 0 ORDER BY priority DESC, created_at DESC")
    List<Task> findByProjectId(@Param("projectId") Long projectId);

    @Select("SELECT * FROM task WHERE assignee_id = #{assigneeId} AND deleted = 0 ORDER BY priority DESC, created_at DESC")
    List<Task> findByAssigneeId(@Param("assigneeId") Long assigneeId);

    @Select("SELECT * FROM task WHERE current_state = #{state} AND deleted = 0")
    List<Task> findByState(@Param("state") String state);

    @Select("SELECT * FROM task WHERE project_id = #{projectId} AND deleted = 0")
    List<Task> findBoardDataByProject(@Param("projectId") Long projectId);
}

