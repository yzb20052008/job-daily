package org.jeecg.modules.job.support;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.job.constant.BizErrorCodes;
import org.jeecg.modules.job.exception.BizException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 短时幂等锁（Redis SET NX）
 * <p>Key 约定：idem:{biz}:{userOrBizId}</p>
 */
@Slf4j
@Component
public class IdempotentHelper {

    public static final String BIZ_APPLY = "apply";
    public static final String BIZ_PAY_SALARY = "paySalary";
    public static final String BIZ_WITHDRAW = "withdraw";

    private static final String KEY_PREFIX = "idem:";

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取短时锁；失败抛 {@link BizException}
     *
     * @param biz        业务前缀
     * @param bizId      用户或业务主键
     * @param ttlSeconds 锁时长秒
     */
    public void assertOnce(String biz, String bizId, int ttlSeconds) {
        if (oConvertUtils.isEmpty(biz) || oConvertUtils.isEmpty(bizId)) {
            throw BizException.of(BizErrorCodes.PARAM_INVALID);
        }
        String key = KEY_PREFIX + biz + ":" + bizId;
        Boolean ok;
        try {
            ok = redisTemplate.opsForValue().setIfAbsent(key, "1", ttlSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            // Redis 异常时降级放行，避免拖垮主流程，仅记日志
            log.error("幂等锁 Redis 异常，降级放行 key={}", key, e);
            return;
        }
        if (ok == null || !ok) {
            throw BizException.of(BizErrorCodes.IDEMPOTENT_REPEAT);
        }
    }

    /**
     * 报名专用：userId + postId
     */
    public void assertApplyOnce(String userId, String postId) {
        assertOnce(BIZ_APPLY, userId + ":" + postId, 8);
    }

    /**
     * 结算发起：orderId
     */
    public void assertPaySalaryOnce(String orderId) {
        assertOnce(BIZ_PAY_SALARY, orderId, 10);
    }

    /**
     * 提现申请：userId
     */
    public void assertWithdrawOnce(String userId) {
        assertOnce(BIZ_WITHDRAW, userId, 5);
    }
}
