package com.softmanage.common.constant;

/**
 * RabbitMQ 消息队列常量
 */
public class MQConstant {
    // 交换机
    public static final String WORKFLOW_EXCHANGE = "workflow.exchange";
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
    public static final String AUDIT_EXCHANGE = "audit.exchange";

    // 队列
    public static final String WORKFLOW_EVENTS_QUEUE = "workflow.events.queue";
    public static final String NOTIFICATION_QUEUE = "notification.queue";
    public static final String AUDIT_QUEUE = "audit.queue";
    public static final String REPORT_QUEUE = "report.queue";

    // 路由键
    public static final String TASK_CREATED_KEY = "task.created";
    public static final String TASK_ASSIGNED_KEY = "task.assigned";
    public static final String TASK_UPDATED_KEY = "task.updated";
    public static final String STATE_CHANGED_KEY = "state.changed";
    public static final String TASK_BLOCKED_KEY = "task.blocked";
    public static final String TASK_UNBLOCKED_KEY = "task.unblocked";
    public static final String REVIEW_RESULT_KEY = "review.result";
    public static final String TEST_RESULT_KEY = "test.result";
    public static final String RELEASE_RESULT_KEY = "release.result";
    public static final String EXCEPTION_KEY = "exception.occurred";
}

