package com.jiangjing.im.message.model;

import com.jiangjing.im.common.model.message.MessageContent;
import com.jiangjing.im.message.dao.ImMessageBodyEntity;
import lombok.Data;

/**
 * @author: Chackylee
 * @description:
 **/
@Data
public class DoStoreP2PMessageDto {

    private MessageContent messageContent;

    private ImMessageBodyEntity messageBody;

}
