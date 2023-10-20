package com.jiangjing.im.common.model;

import lombok.Data;

import java.util.Objects;

/**
 * @author jingjing
 * @date 2023/6/24 10:05
 */
@Data
public class UserClientDto {

    private Integer appId;

    private Integer clientType;

    private String userId;

    private String imei;


    /**
     * 使用到了 concurrentHashMap ，需要重写equals 和 hashCode 方法
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserClientDto that = (UserClientDto) o;
        return Objects.equals(appId, that.appId) && Objects.equals(clientType, that.clientType) && Objects.equals(userId, that.userId) && Objects.equals(imei, that.imei);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appId, clientType, userId, imei);
    }
}
