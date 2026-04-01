package com.softmanage.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.softmanage.workflow.entity.StateTransitionRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StateTransitionRuleMapper extends BaseMapper<StateTransitionRule> {

    @Select("SELECT * FROM state_transition_rule WHERE workflow_id = #{workflowId} AND from_state = #{fromState} ORDER BY sort_order")
    List<StateTransitionRule> findByFromState(@Param("workflowId") Long workflowId, @Param("fromState") String fromState);

    @Select("SELECT * FROM state_transition_rule WHERE workflow_id = #{workflowId} AND from_state = #{fromState} AND action_code = #{actionCode}")
    StateTransitionRule findByAction(@Param("workflowId") Long workflowId, @Param("fromState") String fromState, @Param("actionCode") String actionCode);

    @Select("SELECT * FROM state_transition_rule WHERE workflow_id = #{workflowId} ORDER BY sort_order")
    List<StateTransitionRule> findByWorkflowId(@Param("workflowId") Long workflowId);
}

