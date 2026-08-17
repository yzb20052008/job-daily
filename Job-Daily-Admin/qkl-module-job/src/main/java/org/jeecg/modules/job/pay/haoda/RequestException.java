package org.jeecg.modules.job.pay.haoda;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

/**
 * @author qingkongl
 */
@Getter
@AllArgsConstructor
public class RequestException extends Exception {
    /**
     * HTTP 状态码.
     * 由于已经根据状态码等信息识别出了错误生成次异常对象，所以大部分情况下业务代码不需要在关心该状态码.
     */
    private final int statusCode;
    /**
     * 服务端响应的内容.
     * 由于服务端的响应可能为空，所以该参数也可能为空，但是一般情况不会为空.
     */
    private final @Nullable String body;
}