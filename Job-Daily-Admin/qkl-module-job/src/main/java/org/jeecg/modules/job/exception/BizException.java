package org.jeecg.modules.job.exception;

import lombok.Getter;
import org.jeecg.modules.job.constant.BizErrorCodes;

/**
 * 日结业务异常（带业务错误码，message 对人可读）
 */
@Getter
public class BizException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final int errCode;

    public BizException(BizErrorCodes error) {
        super(error.getMessage());
        this.errCode = error.getCode();
    }

    public BizException(BizErrorCodes error, String message) {
        super(message);
        this.errCode = error.getCode();
    }

    public BizException(int errCode, String message) {
        super(message);
        this.errCode = errCode;
    }

    public static BizException of(BizErrorCodes error) {
        return new BizException(error);
    }

    public static BizException of(BizErrorCodes error, String message) {
        return new BizException(error, message);
    }
}
