package com.jiangjing.im.service.message.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangjing.im.service.message.dao.ImMessageHistoryEntity;
import com.jiangjing.im.service.message.model.req.P2pMessageHistoryReq;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;

/**
 * @author Admin
 */
@Mapper
public interface ImMessageHistoryMapper extends BaseMapper<ImMessageHistoryEntity> {

    /**
     * 批量插入（mysql）
     *
     * @param entityList
     * @return
     */
    Integer insertBatchSomeColumn(Collection<ImMessageHistoryEntity> entityList);

    // todo 写死了 limit ， messageBody
    @Select(
            " <script> " +
            "SELECT * FROM im_message_history " +
            "WHERE owner_id = #{ownerId} " +
            "AND app_id = #{appId} " +
            "AND from_id in ( #{ownerId},#{toId} )" +
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
            "order by sequence asc " +
            "LIMIT 20 " +
            " </script> "
    )
    List<ImMessageHistoryEntity> queryMessageHistory(P2pMessageHistoryReq req);
}
