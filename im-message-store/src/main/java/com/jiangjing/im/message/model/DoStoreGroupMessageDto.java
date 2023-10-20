package com.jiangjing.im.message.model;

import com.jiangjing.im.common.model.message.GroupChatMessageContent;
import com.jiangjing.im.message.dao.ImMessageBodyEntity;
import lombok.Data;

/**
 * @author: Chackylee
 * @description:
 **/
@Data
public class DoStoreGroupMessageDto {

    private GroupChatMessageContent groupChatMessageContent;

    private ImMessageBodyEntity messageBody;

}
