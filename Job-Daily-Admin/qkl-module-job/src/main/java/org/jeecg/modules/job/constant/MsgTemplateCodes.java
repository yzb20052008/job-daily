package org.jeecg.modules.job.constant;

/**
 * 消息模板编码（对应 biz_msg_template.template_code）
 */
public interface MsgTemplateCodes {

    /** 微信-认证结果 */
    String WX_AUTH = "wx_auth";
    /** 微信-提现进度 */
    String WX_WITHDRAW = "wx_withdraw";
    /** 微信-同意用工 */
    String WX_ORDER_AGREE = "wx_order_agree";
    /** 微信-拒绝用工 */
    String WX_ORDER_UNAGREE = "wx_order_unagree";
    /** 微信-录用结果 */
    String WX_APPLY_RESULT = "wx_apply_result";
    /** 微信-新订单提醒 */
    String WX_NEW_ORDER = "wx_new_order";

    /** 站内信-订单已结算（工人） */
    String SITE_ORDER_PAID_MEMBER = "site_order_paid_member";
    /** 站内信-工资结算成功（老板） */
    String SITE_ORDER_PAID_COMPANY = "site_order_paid_company";
    /** 站内信-新申请单（老板） */
    String SITE_NEW_ORDER_COMPANY = "site_new_order_company";
    /** 站内信-生成订单（工人） */
    String SITE_NEW_ORDER_MEMBER = "site_new_order_member";
}
