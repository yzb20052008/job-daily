package org.jeecg.modules.job.constant;

/**
 * 日结域权益编码（与 base_config / 规则校验对齐）
 */
public interface PrivilegeCodes {

    /** VIP 拨号/联系免积分：base_config.vip_contact_free = 1 开启 */
    String VIP_CONTACT_FREE = "vip_contact_free";

    /** 发岗是否要求企业 VIP：base_config.vip_post_require = 1 开启 */
    String VIP_POST_REQUIRE = "vip_post_require";

    /** 权益动作：联系老板/工人 */
    String ACTION_CONTACT = "CONTACT";

    /** 权益动作：发布招工 */
    String ACTION_POST_PUBLISH = "POST_PUBLISH";
}
