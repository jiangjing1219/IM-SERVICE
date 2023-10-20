package com.jiangjing.im.service.interceptor;

import com.alibaba.fastjson2.JSONObject;
import com.jiangjing.im.common.BaseErrorCode;
import com.jiangjing.im.common.ResponseVO;
import com.jiangjing.im.common.config.AppConfig;
import com.jiangjing.im.common.constant.Constants;
import com.jiangjing.im.common.enums.GateWayErrorCode;
import com.jiangjing.im.common.enums.ImUserTypeEnum;
import com.jiangjing.im.common.exception.ApplicationExceptionEnum;
import com.jiangjing.im.common.utils.SigAPI;
import com.jiangjing.im.service.user.dao.ImUserDataEntity;
import com.jiangjing.im.service.user.service.ImUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 校验签名
 *
 * @author Admin
 */
@Component
public class IdentityCheck {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    AppConfig appConfig;

    @Autowired
    ImUserService imUserService;

    private static final Logger logger = LoggerFactory.getLogger(IdentityCheck.class);

    public ApplicationExceptionEnum checkUserSig(String identifier, String appId, String userSig) {

        // 尝试从 redis 中获取签名信息
        String cacheKey = appId + Constants.RedisConstants.USER_SIGN + identifier + ":" + userSig;
        if (redisTemplate.opsForValue().get(cacheKey) != null) {
            // 判断当前用户是否是管理员
            setIsAdmin(identifier, Integer.valueOf(appId));
            return BaseErrorCode.SUCCESS;
        }

        // 解析到当前用户的签名
        JSONObject jsonObject = SigAPI.decodeUserSig(userSig);
        //取出解密后的appid 和 操作人 和 过期时间做匹配，不通过则提示错误
        long failureTime = 0L;
        long expireSec = 0L;
        long sigCurrentTime = 0L;
        String decoerAppId = "";
        String decoderidentifier = "";
        String decoderSign = "";

        try {
            decoerAppId = jsonObject.getString("TLS.appId");
            decoderSign = jsonObject.getString("TLS.sig");
            decoderidentifier = jsonObject.getString("TLS.identifier");
            // 过期时间，30 分钟（保活时间）
            String expireStr = jsonObject.get("TLS.expire").toString();
            // 生成签名时的当前时间
            String expireTimeStr = jsonObject.get("TLS.expireTime").toString();
            expireSec = Long.parseLong(expireStr);
            sigCurrentTime = Long.parseLong(expireTimeStr);
            // 失效时间
            failureTime = Long.parseLong(expireTimeStr) + expireSec;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("checkUserSig-error:{}", e.getMessage());
        }

        /*
         * 将明文传递过来的信息和签名解析出来的信息做对比，不一致说明由篡改，鉴权失败
         * （本质还是类似于 token 一样的东西）
         */
        if (!decoderidentifier.equals(identifier)) {
            return GateWayErrorCode.USERSIGN_OPERATE_NOT_MATE;
        }

        if (!decoerAppId.equals(appId)) {
            return GateWayErrorCode.USERSIGN_IS_ERROR;
        }

        if (expireSec == 0L) {
            return GateWayErrorCode.USERSIGN_IS_EXPIRED;
        }

        if (failureTime < System.currentTimeMillis() / 1000) {
            return GateWayErrorCode.USERSIGN_IS_EXPIRED;
        }

        /*
         * 校验私钥签名
         */
        String hmacsha256 = SigAPI.getHmacsha256(Integer.valueOf(appId), identifier, sigCurrentTime, expireSec, null, appConfig.getPrivateKey());
        if (!decoderSign.equals(hmacsha256)) {
            return GateWayErrorCode.USERSIGN_IS_EXPIRED;
        }

        /*
         * 将鉴权的签名信息，保存在 redis 中，如果该签名在有效期内，不需要重复解析
         *
         * key = appid : userSign : userid : userSig
         */
        String key = appId + Constants.RedisConstants.USER_SIGN + identifier + userSig;
        // 将剩余的过期时间设置为过期时间
        redisTemplate.opsForValue().set(key, String.valueOf(expireSec), failureTime - System.currentTimeMillis() / 1000, TimeUnit.SECONDS);
        return BaseErrorCode.SUCCESS;
    }

    /**
     * 根据appid,identifier判断是否App管理员,并设置到RequestHolder
     *
     * @param identifier
     * @param appId
     * @return
     */
    public void setIsAdmin(String identifier, Integer appId) {
        //去DB或Redis中查找, 后面写
        ResponseVO<ImUserDataEntity> singleUserInfo = imUserService.getSingleUserInfo(identifier, appId);
        if (singleUserInfo.isOk()) {
            RequestHolder.set(singleUserInfo.getData().getUserType() == ImUserTypeEnum.APP_ADMIN.getCode());
        } else {
            RequestHolder.set(false);
        }
    }
}
