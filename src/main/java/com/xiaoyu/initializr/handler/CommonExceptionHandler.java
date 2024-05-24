package com.xiaoyu.initializr.handler;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xiaoyu.initializr.common.base.ResponseCode;
import com.xiaoyu.initializr.common.base.ResponseMapper;
import com.xiaoyu.nuwa.utils.BizLogUtils;
import com.xiaoyu.nuwa.utils.exception.CommonException;

import lombok.extern.slf4j.Slf4j;

/**
 * 全局异常拦截
 * 
 * @author xiaoyu
 *
 */
@ControllerAdvice
@Slf4j
public class CommonExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Object exceptionHandler(Exception e) {
        if (e instanceof CommonException) {
            CommonException ex = (CommonException) e;
            switch (ex.level()) {
            case DEBUG:
                log.debug("get exception->{}", e);
                break;
            case ERROR:
                log.error("get error->{}", e);
                break;
            case INFO:
                log.info("get exception->{}", e.getMessage());
                break;
            case NO_LOG:
                break;
            case NO_STACK:
                log.error("get exception->{}", e.getMessage());
                break;
            case WARN:
                log.warn("get exception->{}", e);
                break;
            default:
                log.error("get error->{}", e);
                break;
            }
            ResponseMapper resp = ResponseMapper.createMapper(ex.getCode(), ex.getMessage());
            return resp;
        } else {
            BizLogUtils.bizErrorLog("systemError", e);
            ResponseMapper resp = ResponseMapper.createMapper(ResponseCode.FAILED.code(), "system error");
            return resp;
        }
    }

}
