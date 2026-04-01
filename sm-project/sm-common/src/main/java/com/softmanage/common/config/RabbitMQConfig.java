package com.softmanage.common.config;

import com.softmanage.common.constant.MQConstant;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置
 */
@Configuration
public class RabbitMQConfig {

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // ===== 工作流交换机和队列 =====
    @Bean
    public TopicExchange workflowExchange() {
        return new TopicExchange(MQConstant.WORKFLOW_EXCHANGE);
    }

    @Bean
    public Queue workflowEventsQueue() {
        return QueueBuilder.durable(MQConstant.WORKFLOW_EVENTS_QUEUE).build();
    }

    @Bean
    public Binding workflowBinding() {
        return BindingBuilder.bind(workflowEventsQueue())
                .to(workflowExchange())
                .with("state.#");
    }

    // ===== 通知交换机和队列 =====
    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(MQConstant.NOTIFICATION_EXCHANGE);
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(MQConstant.NOTIFICATION_QUEUE).build();
    }

    @Bean
    public Binding notificationBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(notificationExchange())
                .with("#");
    }

    // ===== 审计交换机和队列 =====
    @Bean
    public TopicExchange auditExchange() {
        return new TopicExchange(MQConstant.AUDIT_EXCHANGE);
    }

    @Bean
    public Queue auditQueue() {
        return QueueBuilder.durable(MQConstant.AUDIT_QUEUE).build();
    }

    @Bean
    public Binding auditBinding() {
        return BindingBuilder.bind(auditQueue())
                .to(auditExchange())
                .with("#");
    }

    // ===== 报表队列 =====
    @Bean
    public Queue reportQueue() {
        return QueueBuilder.durable(MQConstant.REPORT_QUEUE).build();
    }

    @Bean
    public Binding reportBinding() {
        return BindingBuilder.bind(reportQueue())
                .to(workflowExchange())
                .with("state.#");
    }
}

