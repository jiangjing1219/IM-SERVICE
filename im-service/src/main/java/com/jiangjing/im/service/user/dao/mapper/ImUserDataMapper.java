package com.jiangjing.im.service.user.dao.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangjing.im.service.user.dao.ImUserDataEntity;
import com.jiangjing.im.service.user.model.page.MyPage;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Admin
 */
@Mapper
public interface ImUserDataMapper extends BaseMapper<ImUserDataEntity> {


    MyPage<ImUserDataEntity> queryUserPage(MyPage<ImUserDataEntity> imUserDataEntityMyPage);
}
