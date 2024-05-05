package com.jiangjing.im.common.constant;

/**
 * @author jingjing
 * @date 2023/6/24 9:50
 */
public class Constants {
    /**
     * channel绑定的userId Key
     */
    public static final String USERID = "userId";

    /**
     * channel绑定的appId
     */
    public static final String APPID = "appId";

    public static final String CLIENT_TYPE = "clientType";

    public static final String IMEI = "imei";

    /**
     * channel绑定的clientType 和 imel Key
     */
    public static final String CLIENT_IMEI = "clientImei";

    public static final String READ_TIME = "readTime";

    public static final String READ_TIME_COUNT = "readTimeCount";

    public static final String IM_CORE_ZK_ROOT = "/im-coreRoot";

    public static final String IM_CORE_ZK_ROOT_TCP = "/tcp";

    public static final String IM_CORE_ZK_ROOT_WEB = "/web";

    public static final String IM_NACOS_SERVICE_TCP = "im_tcp_service";

    public static final String IM_NACOS_SERVICE_WEB = "im_web_service";

    /**
     * Redis 相关的常量
     */
    public static class RedisConstants {

        /**
         * userSign，格式：appId:userSign:
         */
        public static final String USER_SIGN = ":userSign:";

        /**
         * 用户上线通知channel
         */
        public static final String USER_LOGIN_CHANNEL
                = "signal/channel/LOGIN_USER_INNER_QUEUE";


        /**
         * 用户session，appId + UserSessionConstants + 用户id 例如10000：userSession：lld
         */
        public static final String USER_SESSION_CONSTANTS = ":userSession:";

        /**
         * 缓存客户端消息防重，格式： appId + :cacheMessage: + messageId
         */
        public static final String CACHE_MESSAGE = ":cacheMessage:";

        public static final String OFFLINE_MESSAGE = ":offlineMessage:";

        /**
         * seq 前缀
         */
        public static final String SEQ_PREFIX = ":seq:";

        /**
         * 用户订阅列表，格式 ：appId + :subscribe: + userId。Hash结构，filed为订阅自己的人
         */
        public static final String SUBSCRIBE = ":subscribe:";

        /**
         * 用户自定义在线状态，格式 ：appId + :userCustomerStatus: + userId。set，value为用户id
         */
        public static final String USER_CUSTOMER_STATUS = ":userCustomerStatus:";

    }

    /**
     * RabbitMQ 相关的常量
     */
    public static class RabbitConstants {

        public static final String IM_2_USER_SERVICE = "pipeline2UserService";

        public static final String IM_2_MESSAGE_SERVICE = "pipeline2MessageService";

        public static final String IM_2_GROUP_SERVICE = "pipeline2GroupService";

        public static final String IM_2_FRIENDSHIP_SERVICE = "pipeline2FriendshipService";

        public static final String MESSAGE_SERVICE_2_IM = "messageService2Pipeline";

        public static final String GROUP_SERVICE_2_IM = "GroupService2Pipeline";

        public static final String FRIEND_SHIP_2_IM = "friendShip2Pipeline";

        public static final String STORE_P2P_MESSAGE = "storeP2PMessage";

        public static final String STORE_GROUP_MESSAGE = "storeGroupMessage";

    }

    public static class CallbackCommand {
        public static final String MODIFY_USER_AFTER = "user.modify.after";

        public static final String CREATE_GROUP_AFTER = "group.create.after";

        public static final String GROUP_UPDATE_AFTER = "group.update.after";

        public static final String GROUP_DESTROY_AFTER = "group.destroy.after";

        public static final String GROUP_TRANSFER_AFTER = "group.transfer.after";

        public static final String GROUP_MEMBER_ADD_BEFORE = "group.member.add.before";

        public static final String GROUP_MEMBER_ADD_AFTER = "group.member.add.after";

        public static final String GROUP_MEMBER_DELETE_AFTER = "group.member.delete.after";

        public static final String ADD_FRIEND_BEFORE = "friend.add.before";

        public static final String ADD_FRIEND_AFTER = "friend.add.after";

        public static final String UPDATE_FRIEND_BEFORE = "friend.update.before";

        public static final String UPDATE_FRIEND_AFTER = "friend.update.after";

        public static final String DELETE_FRIEND_AFTER = "friend.delete.after";

        public static final String ADD_BLACK_AFTER = "black.add.after";

        public static final String DELETE_BLACK = "black.delete";

        public static final String SEND_MESSAGE_AFTER = "message.send.after";

        public static final String SEND_MESSAGE_BEFORE = "message.send.before";
    }

    public static class SeqConstants {
        /**
         * 消息序列：  appid:messageSeq:fromID_toID/toId_fromId  (单聊消息的排序)
         */
        public static final String MESSAGE_SEQ = ":messageSeq:";

        public static final String GROUP_MESSAGE_SEQ = "groupMessageSeq";


        public static final String FRIENDSHIP_SEQ = "friendshipSeq";

//        public static final String FriendshipBlack = "friendshipBlackSeq";

        public static final String FRIENDSHIP_REQUEST_SEQ = "friendshipRequestSeq";

        public static final String FRIENDSHIP_GROUP_SEQ = "friendshipGroupSeq";

        public static final String GROUP_SEQ = "groupSeq";

        /**
         * 会话的序列 appid：conversationSeq   一个app一个序列
         */
        public static final String CONVERSATION_SEQ = ":conversationSeq";

    }

}
