package com.jiangjing.im.service.friendship.model.req;

import com.jiangjing.im.common.enums.FriendShipStatusEnum;
import com.jiangjing.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;


/**
 * @author Admin
 */
@Data
public class ImporFriendShipReq extends RequestBase {

    @NotBlank(message = "fromId不能为空")
    private String fromId;

    /**
     * 一个用户对应多个好友关系
     */
    private List<ImportFriendDto> friendItem;

    @Data
    public static class ImportFriendDto{

        /**
         * 需要添加的用户
         */
        private String toId;

        /**
         * 备注
         */
        private String remark;

        /**
         * 添加来源
         */
        private String addSource;

        /**
         * 默认的好友状态，0  未添加
         */
        private Integer status = FriendShipStatusEnum.FRIEND_STATUS_NO_FRIEND.getCode();

        /**
         * 拉黑状态，默认 1 正常；
         */
        private Integer black = FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode();
    }

}
