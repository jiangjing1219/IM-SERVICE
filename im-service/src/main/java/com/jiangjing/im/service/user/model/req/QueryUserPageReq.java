package com.jiangjing.im.service.user.model.req;

import com.jiangjing.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class QueryUserPageReq extends RequestBase {
    @NotNull(message = "currentPage不能为空")
    private long currentPage;

    @NotNull(message = "pageSize不能为空")
    private long pageSize;

    private String nickName;

    private String tel;
}

