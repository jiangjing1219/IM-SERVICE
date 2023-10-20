package com.jiangjing.im.service.friendship.model.resp;

import lombok.Data;

import java.util.List;

/**
 * @author jingjing
 * @date 2023/5/23 0:13
 */
@Data
public class ImportFriendShipGroupMemberResp {

    private List<String> successId;

    private List<String> errorId;
}
