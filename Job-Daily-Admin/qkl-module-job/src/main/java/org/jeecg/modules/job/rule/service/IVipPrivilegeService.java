package org.jeecg.modules.job.rule.service;

/**
 * VIP / 权益统一校验入口
 */
public interface IVipPrivilegeService {

    /**
     * 用户当前角色是否为有效 VIP（未过期）
     */
    boolean isVipActive(String userId, String roleCode);

    /**
     * 拨号/联系是否免费（服务端判定，忽略客户端 ifFree）
     * <p>规则：① VIP 且 vip_contact_free=1；② 确认时长内已有同岗联系记录</p>
     */
    boolean isContactFree(String userId, String roleCode, String postId);

    /**
     * 发岗资格校验；不通过抛业务异常
     */
    void assertCanPublishPost(String userId);
}
