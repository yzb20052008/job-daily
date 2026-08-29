package org.jeecg.modules.job.exception;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 日结业务异常处理（优先于全局 Handler）
 * <p>返回 code=业务码，message=中文提示，移动端仍 toast message</p>
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class BizExceptionHandler {

    @ExceptionHandler(BizException.class)
    public Result<?> handleBizException(BizException e) {
        log.warn("业务异常 code={}, msg={}", e.getErrCode(), e.getMessage());
        return Result.error(e.getErrCode(), e.getMessage());
    }
}
