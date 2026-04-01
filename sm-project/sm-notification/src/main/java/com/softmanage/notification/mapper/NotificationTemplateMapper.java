package com.softmanage.notification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.softmanage.notification.entity.NotificationTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface NotificationTemplateMapper extends BaseMapper<NotificationTemplate> {

    @Select("SELECT * FROM notification_template WHERE template_code = #{code} AND status = 1")
    NotificationTemplate findByCode(@Param("code") String code);
}

