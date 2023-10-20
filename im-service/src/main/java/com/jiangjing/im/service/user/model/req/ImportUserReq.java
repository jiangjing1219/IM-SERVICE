package com.jiangjing.im.service.user.model.req;

import com.jiangjing.im.common.model.RequestBase;
import com.jiangjing.im.service.user.dao.ImUserDataEntity;
import lombok.Data;

import java.util.List;


/**
 * @author Admin
 */
@Data
public class ImportUserReq extends RequestBase {

    /**
     * 需要导入的用户信息
     */
    private List<ImUserDataEntity> userData;


}
