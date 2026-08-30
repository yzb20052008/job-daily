package org.jeecg.modules.job.constant;

import lombok.Getter;

/**
 * 日结业务错误码（移动端仍以 message 展示，code 供排查/后续映射）
 * <pre>
 * 10xxx 通用
 * 20xxx 岗位/报名
 * 30xxx 订单/打卡
 * 40xxx 结算/支付
 * 50xxx 账户/提现
 * 60xxx 积分/VIP
 * 90xxx 幂等/限流
 * </pre>
 */
@Getter
public enum BizErrorCodes {

    PARAM_INVALID(10001, "参数错误"),
    NOT_LOGIN(10002, "请先登录"),
    NO_PERMISSION(10003, "无权限操作"),
    DATA_NOT_FOUND(10004, "数据不存在"),

    POST_NOT_FOUND(20001, "岗位不存在"),
    POST_CANNOT_APPLY(20002, "当前岗位不可报名"),
    APPLY_DUPLICATE(20003, "请勿重复提交"),
    APPLY_INTEGRAL_SHORT(20004, "积分不足"),

    ORDER_NOT_FOUND(30001, "订单不存在"),
    ORDER_STATUS_INVALID(30002, "订单状态不允许此操作"),
    ORDER_PERMISSION(30003, "无权操作该订单"),

    PAY_AMOUNT_INVALID(40001, "结算金额异常"),
    PAY_ONLY_BOSS(40002, "仅老板可结算工资"),
    PAY_STATUS_INVALID(40003, "订单非待结算状态"),

    WITHDRAW_AMOUNT_INVALID(50001, "提现金额异常"),
    WITHDRAW_BALANCE_SHORT(50002, "可提现余额不足"),
    WITHDRAW_DUPLICATE(50003, "提现处理中，请勿重复提交"),
    WITHDRAW_BELOW_MIN(50004, "提现金额低于最低限额"),
    WITHDRAW_ABOVE_MAX(50005, "提现金额超过单次限额"),
    WITHDRAW_DAY_LIMIT(50006, "今日提现金额已达上限"),
    WITHDRAW_CHANNEL_UNSUPPORTED(50007, "当前仅支持微信提现"),
    WITHDRAW_FROZEN_SHORT(50008, "冻结余额不足，无法审核通过"),
    WITHDRAW_MCH_BALANCE_SHORT(50009, "微信商户余额不足，无法审核通过"),

    VIP_REQUIRED(60001, "需要开通会员后操作"),

    IDEMPOTENT_REPEAT(90001, "操作过于频繁，请稍后再试");

    private final int code;
    private final String message;

    BizErrorCodes(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
