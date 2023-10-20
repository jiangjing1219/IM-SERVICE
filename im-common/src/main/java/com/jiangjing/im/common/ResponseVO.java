package com.jiangjing.im.common;

import com.jiangjing.im.common.exception.ApplicationExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Admin
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseVO<T> implements Serializable {

    private int code;

    private String message;

    private T data;


    public static ResponseVO successResponse(Object data) {
        return new ResponseVO(200, "success", data);
    }

    public static ResponseVO successResponse() {
        return new ResponseVO(200, "success");
    }

    public static ResponseVO errorResponse() {
        return new ResponseVO(500, "系统内部异常");
    }

    public static ResponseVO errorResponse(int code, String msg) {
        return new ResponseVO(code, msg);
    }

    public static ResponseVO errorResponse(ApplicationExceptionEnum enums) {
        return new ResponseVO(enums.getCode(), enums.getError());
    }

    public static ResponseVO errorResponse(ApplicationExceptionEnum enums,String msg) {
        return new ResponseVO(enums.getCode(), enums.getError()).setMessage(msg);
    }

    public boolean isOk() {
        return this.code == 200;
    }


    public ResponseVO(int code, String msg) {
        this.code = code;
        this.message = msg;
//		this.data = null;
    }

    public ResponseVO success() {
        this.code = 200;
        this.message = "success";
        return this;
    }

    public ResponseVO success(T data) {
        this.code = 200;
        this.message = "success";
        this.data = data;
        return this;
    }

    public ResponseVO setMessage(String message) {
        this.message += ","+message;
        return this;
    }
}
