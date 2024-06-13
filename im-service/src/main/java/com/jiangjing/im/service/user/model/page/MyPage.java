package com.jiangjing.im.service.user.model.page;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author miemie
 * @since 2018-08-10
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class MyPage<T> extends Page<T> {
    private static final long serialVersionUID = 5194933845448697148L;

    private String nickName;

    private String userId;
    public MyPage(long current, long size, String nickName,String userId) {
        super(current, size);
        this.nickName = nickName;
        this.userId = userId;
    }

}
