package com.jiangjing.im.service.user.model.req;

import com.jiangjing.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;


/**
 *
 * 批量删除用户
 * @author Admin
 */
@Data
public class DeleteUserReq extends RequestBase {

    @NotEmpty(message = "用户id不能为空")
    private List<String> userId;
}
