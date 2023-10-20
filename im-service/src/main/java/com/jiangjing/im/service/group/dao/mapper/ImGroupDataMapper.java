package com.jiangjing.im.service.group.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangjing.im.service.group.dao.ImGroupEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author jingjing
 * @date 2023/5/24 23:53
 */
@Mapper
public interface ImGroupDataMapper extends BaseMapper<ImGroupEntity> {


    /**
     * 获取指定群组的最大的 seq
     *
     * @param groupIds
     * @param appId
     * @return
     */
    @Select(" <script> " +
            " select max(sequence) from im_group where app_id = #{appId} and group_id in " +
            "<foreach collection='groupIds' index='index' item='id' separator=',' close=')' open='('>" +
            " #{id} " +
            "</foreach>" +
            " </script> ")
    Long getGroupMaxSeq(List<String> groupIds, Integer appId);
}
