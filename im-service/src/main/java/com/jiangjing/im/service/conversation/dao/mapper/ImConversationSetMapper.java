package com.jiangjing.im.service.conversation.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangjing.im.service.conversation.dao.ImConversationSetEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @description:
 * @author: lld
 * @version: 1.0
 */
@Mapper
public interface ImConversationSetMapper extends BaseMapper<ImConversationSetEntity> {

    @Update(" update im_conversation_set set readed_sequence = #{readedSequence},sequence = #{sequence} " +
            " where conversation_id = #{conversationId} and app_id = #{appId} AND readed_sequence < #{readedSequence}")
    public void readMark(ImConversationSetEntity imConversationSetEntity);

    /**
     * 获取指定用户的最大会话 sequence
     *
     * @param appId
     * @param userId
     * @return
     */
    @Select(" select max(sequence) from im_conversation_set where app_id = #{appId} AND from_id = #{userId} ")
    Long getConversationSetMaxSeq(Integer appId, String userId);
}
