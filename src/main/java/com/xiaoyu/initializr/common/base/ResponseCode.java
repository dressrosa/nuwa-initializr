package com.xiaoyu.initializr.common.base;

import com.xiaoyu.nuwa.utils.response.BaseResponseCode;

/**
 * @author xiaoyu
 * @description 请求返回码<br/>
 *              错误码总6位 ,1xx通用,2xx保留,3xx为用户相关<br/>
 * 
 */
public enum ResponseCode implements BaseResponseCode {

    /**
     * 000000,"success"
     */
    SUCCESS("000000", "success"),
    /**
     * 110001,"params error"
     */
    ARGS_ERROR("110001", "params error"),
    /**
     * 100404,"no data"
     */
    NO_DATA("100404", "no data"),
    /**
     * 100500,"business failed"
     */
    FAILED("100500", "business failed"),
    /**
     * 100600,"out req error"
     */
    OUTER_ERROR("100600", "request out service error"),

    /**
     * 没登陆
     */
    UN_LOGIN("200001", " no login"),
    /**
     * 没有权限
     */
    REQ_NOACCESS("200002", "no access"),
    /**
     * 访问错误
     */
    REQ_ERROR("200003", "request error"),

    /**
     * 签名错误
     */
    SIGN_ERROR("200004", "sign error"),

    /**
     * 请求限流
     */
    REQUEST_LIMIT("200005", "request limit"),

    /**
     * 重复请求
     */
    REQUEST_REPEAT("200006", "请求处理中"),

    ;

    private String code;
    private String message;

    ResponseCode(String code, String msg) {
        this.code = code;
        this.message = msg;
    }

    public String code() {
        return this.code;
    }

    public String msg() {
        return this.message;
    }
}
