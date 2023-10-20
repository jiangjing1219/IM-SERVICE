package com.jiangjing.im.service.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.jiangjing.im.common.BaseErrorCode;
import com.jiangjing.im.common.ResponseVO;
import com.jiangjing.im.common.enums.GateWayErrorCode;
import com.jiangjing.im.common.exception.ApplicationExceptionEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * 请求的拦截器，接口鉴权，需要配置该拦截器的拦截路径
 *
 * @author Admin
 */
@Component
public class GateWayInterceptor implements HandlerInterceptor {

    @Autowired
    IdentityCheck identityCheck;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        // 校验appId参数
        String appId = request.getParameter("appId");
        if (StringUtils.isBlank(appId)) {
            resp(ResponseVO.errorResponse(GateWayErrorCode
                    .APPID_NOT_EXIST), response);
            return false;
        }

        // 校验 identifier ，userId 参数
        String identifier = request.getParameter("identifier");
        if (StringUtils.isBlank(identifier)) {
            resp(ResponseVO.errorResponse(GateWayErrorCode
                    .OPERATER_NOT_EXIST), response);
            return false;
        }

        /*
         * 校验用户签名
         *      1、可以由用户端请求后台接口获取
         *      2、appService 根据一顶的规则获取（腾讯就是由 appService 生成）
         *  获取对应应用的密钥，正式的生产环境会维护一张密钥的表，appId —— privateKey ，每个应用的密钥不同：目前的实现是直接在配置文件配置密钥
         *      String privateKey = appConfig.getPrivateKey();
         *      生成当前用户的鉴权票据，和设置超时时间，这一部分是直接放在 appService 端生成的,添加了密钥生成 sign
         *      String genUserSig = new SigAPI(10000, privateKey).genUserSig("1001", 60 * 30);
         */
        String userSign = request.getParameter("userSign");
        if (StringUtils.isBlank(userSign)) {
            resp(ResponseVO.errorResponse(GateWayErrorCode.PRIVATE_KEY_IS_ERROR), response);
            return false;
        }

        /*
         * 解签，校验签名参数
         */
        ApplicationExceptionEnum applicationExceptionEnum = identityCheck.checkUserSig(identifier, appId, userSign);
        if (applicationExceptionEnum != BaseErrorCode.SUCCESS) {
            resp(ResponseVO.errorResponse(applicationExceptionEnum), response);
            return false;
        }
        return true;
    }


    /**
     * 会写错误信息
     *
     * @param respVo
     * @param response
     */
    private void resp(ResponseVO respVo, HttpServletResponse response) {
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Max-Age", "3600");
        try {
            String resp = JSONObject.toJSONString(respVo);
            writer = response.getWriter();
            writer.write(resp);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.checkError();
            }
        }
    }

}
