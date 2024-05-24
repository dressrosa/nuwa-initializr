package com.xiaoyu.initializr.common.base;

import com.xiaoyu.nuwa.utils.response.BaseResponseMapper;

import lombok.Getter;

/**
 * @author xiaoyu
 * @description
 */
@Getter
public class ResponseMapper implements BaseResponseMapper {

    /**
     * 默认为成功
     */
    private long count;
    private String code;
    private String message;
    private Object data;

    private ResponseMapper() {
    }

    public static final ResponseMapper createMapper() {
        return createMapper(ResponseCode.SUCCESS);
    }

    public static final ResponseMapper createMapper(ResponseCode resp) {
        return createMapper(resp.code(), resp.msg());
    }

    public static final ResponseMapper createMapper(String code, String msg) {
        ResponseMapper mapper = new ResponseMapper();
        mapper.message = msg;
        mapper.code = code;
        return mapper;
    }

    public ResponseMapper count(Long count) {
        if (count == null) {
            return this;
        }
        this.count = count;
        return this;
    }

    public ResponseMapper data(Object data) {
        if (data == null) {
            return this;
        }
        this.data = data;
        return this;
    }

    @Override
    public String returnCode() {
        return this.code;
    }

}
