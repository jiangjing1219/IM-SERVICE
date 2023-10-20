package com.jiangjing.im.service.message.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangjing.im.service.message.dao.ImGroupMessageHistoryEntity;
import com.jiangjing.im.service.message.model.req.GroupMessageHistoryReq;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ImGroupMessageHistoryMapper extends BaseMapper<ImGroupMessageHistoryEntity> {



    @Select("SELECT * FROM im_group_message_history " +
            "AND app_id = #{appId}" +
            "AND group_id = #{groupId} " +
            "<if test='startTime != null'>" +
            "AND message_time >= #{startTime} " +
            "</if>" +
            "<if test='endTime != null'>" +
            "AND message_time &lt;= #{endTime} " +
            "</if>" +
            "<if test='messageSequence != null and !messageSequence.isEmpty()'>" +
            "AND sequence " +
            "<if test='isGt != null and isGt'>" +
            ">= " +
            "</if>" +
            "<if test='isGt != null and !isGt'>" +
            "&lt; " +
            "</if>" +
            "#{messageSequence} " +
            "</if>" +
            "LIMIT #{limit}" +
            "order by sequence asc"
    )
    List<ImGroupMessageHistoryEntity> queryMessageHistory(GroupMessageHistoryReq req);
}
