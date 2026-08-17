package org.jeecg.modules.job.job.support;

import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.job.constant.BizConstants;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 用工订单状态机：校验合法迁移，禁止客户端任意跳态。
 */
public final class OrderStatusMachine {

    private static final Map<String, Set<String>> ALLOWED = new HashMap<>();

    static {
        // 待确认 → 待开工 / 取消
        allow(BizConstants.ORDER_STATUS_WAIT_ENSURE,
                BizConstants.ORDER_STATUS_WAIT_START,
                BizConstants.ORDER_STATUS_CANCEL);
        // 待开工 → 工作中 / 取消
        allow(BizConstants.ORDER_STATUS_WAIT_START,
                BizConstants.ORDER_STATUS_WORKING,
                BizConstants.ORDER_STATUS_CANCEL);
        // 工作中 → 待结算
        allow(BizConstants.ORDER_STATUS_WORKING,
                BizConstants.ORDER_STATUS_WAIT_PAY);
        // 待结算 → 待评价（支付回调）
        allow(BizConstants.ORDER_STATUS_WAIT_PAY,
                BizConstants.ORDER_STATUS_WAIT_COMMENT);
        // 待评价 → 已完成
        allow(BizConstants.ORDER_STATUS_WAIT_COMMENT,
                BizConstants.ORDER_STATUS_FINISH);
    }

    private OrderStatusMachine() {
    }

    private static void allow(String from, String... toList) {
        Set<String> set = new HashSet<>();
        Collections.addAll(set, toList);
        ALLOWED.put(from, set);
    }

    /**
     * 校验是否允许从当前状态迁移到目标状态
     */
    public static void assertTransition(String fromStatus, String toStatus) {
        if (fromStatus == null || toStatus == null) {
            throw new JeecgBootException("订单状态参数无效");
        }
        if (fromStatus.equals(toStatus)) {
            throw new JeecgBootException("订单状态未变化");
        }
        Set<String> next = ALLOWED.get(fromStatus);
        if (next == null || !next.contains(toStatus)) {
            throw new JeecgBootException("订单状态不允许从[" + statusLabel(fromStatus) + "]变更为[" + statusLabel(toStatus) + "]");
        }
    }

    /**
     * 是否终态
     */
    public static boolean isTerminal(String status) {
        return BizConstants.ORDER_STATUS_FINISH.equals(status)
                || BizConstants.ORDER_STATUS_CANCEL.equals(status);
    }

    public static String statusLabel(String status) {
        if (BizConstants.ORDER_STATUS_WAIT_ENSURE.equals(status)) {
            return "待确认";
        }
        if (BizConstants.ORDER_STATUS_WAIT_START.equals(status)) {
            return "待开工";
        }
        if (BizConstants.ORDER_STATUS_WORKING.equals(status)) {
            return "工作中";
        }
        if (BizConstants.ORDER_STATUS_WAIT_PAY.equals(status)) {
            return "待结算";
        }
        if (BizConstants.ORDER_STATUS_WAIT_COMMENT.equals(status)) {
            return "待评价";
        }
        if (BizConstants.ORDER_STATUS_FINISH.equals(status)) {
            return "已完成";
        }
        if (BizConstants.ORDER_STATUS_CANCEL.equals(status)) {
            return "已取消";
        }
        return status;
    }
}
