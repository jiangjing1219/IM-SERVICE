package com.jiangjing.im.service.utils;


import com.jiangjing.im.common.ResponseVO;
import com.jiangjing.im.common.config.AppConfig;
import com.jiangjing.im.common.utils.HttpRequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 业务回调方法
 * 1、before
 * 2、after
 * <p>
 * 后续的改进：需要将 CallbackService 最为一个回调的分发出口，不同的app可以自定义配置回调的地址，不同的回调业务接口地址也不同
 *
 * @author Admin
 */

@Component
public class CallbackService {

    private final Logger logger = LoggerFactory.getLogger(CallbackService.class);

    @Autowired
    HttpRequestUtils httpRequestUtils;

    @Autowired
    AppConfig appConfig;


    /**
     * 处理后回调，不关心返回值直接使用线程池执行任务
     *
     * @param appId
     * @param callbackCommand
     * @param jsonBody
     */
    public void callback(Integer appId, String callbackCommand, String jsonBody) {
        ThreadPoolExecutorUtils.THREAD_POOL_EXECUTOR.execute(() -> {
            try {
                httpRequestUtils.doPost(appConfig.getCallbackUrl(), Object.class, builderUrlParams(appId, callbackCommand),
                        jsonBody, null);
            } catch (Exception e) {
                logger.error("callback 回调{} : {}出现异常 ： {} ", callbackCommand, appId, e.getMessage());
            }
        });
    }

    /**
     * 处理前回调，需要获取回调的返回值判断是否需要继续执行
     *
     * @param appId
     * @param callbackCommand
     * @param jsonBody
     * @return
     */
    public ResponseVO beforeCallback(Integer appId, String callbackCommand, String jsonBody) {
        try {
            ResponseVO responseVO = httpRequestUtils.doPost(appConfig.getCallbackUrl(), ResponseVO.class, builderUrlParams(appId, callbackCommand),
                    jsonBody, null);
            return responseVO;
        } catch (Exception e) {
            logger.error("callback 之前 回调{} : {}出现异常 ： {} ", callbackCommand, appId, e.getMessage());
            return ResponseVO.successResponse();
        }
    }

    /**
     * 封装业务方法，主要的参数就是 appid、command
     *
     * @param appId
     * @param command
     * @return
     */
    public Map<String, Object> builderUrlParams(Integer appId, String command) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("appId", appId);
        map.put("command", command);
        return map;
    }

}
