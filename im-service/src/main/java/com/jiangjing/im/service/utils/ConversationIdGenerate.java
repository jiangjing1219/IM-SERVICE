package com.jiangjing.im.service.utils;


/**
 * @author: Chackylee
 **/
public class ConversationIdGenerate {

    /**
     * 获单聊消息的 messageSeq   A:B  /   B:A   谁的 id 大就谁排前面，群聊消息直接使用 groupId 即可
     *
     * @param fromId
     * @param toId
     * @return
     */
    public static String generateP2PId(String fromId,String toId){
        int i = fromId.compareTo(toId);
        if(i < 0){
            return toId+":"+fromId;
        }else if(i > 0){
            return fromId+":"+toId;
        }
        throw new RuntimeException("");
    }
}
