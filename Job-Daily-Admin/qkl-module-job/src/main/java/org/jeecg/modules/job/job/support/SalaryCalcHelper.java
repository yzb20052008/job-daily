package org.jeecg.modules.job.job.support;

import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.job.job.entity.JobOrder;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 用工结算金额：系统可给出单价×工时建议值，老板最终以申报金额为准（仅要求大于 0）。
 */
public final class SalaryCalcHelper {

    private SalaryCalcHelper() {
    }

    /**
     * 根据订单单价与工时核算建议金额；无法核算时返回 null。
     */
    public static BigDecimal calcSuggestAmount(JobOrder order) {
        if (order == null || order.getUnitPrice() == null
                || order.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        BigDecimal hours = resolveWorkHours(order);
        if (hours == null || hours.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        return order.getUnitPrice().multiply(hours).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 解析老板申报结算金额：不强制等于核算价、不设金额上限，仅要求大于 0。
     */
    public static BigDecimal resolvePayAmount(JobOrder order, BigDecimal clientAmount) {
        if (clientAmount == null || clientAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new JeecgBootException("结算金额必须大于0");
        }
        return clientAmount.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 工时：优先 duration；否则按上下班时间向上取整小时（不足 1 小时按 1）。
     */
    public static BigDecimal resolveWorkHours(JobOrder order) {
        if (order.getDuration() != null && order.getDuration() > 0) {
            return new BigDecimal(order.getDuration());
        }
        if (order.getStartTime() != null && order.getEndTime() != null) {
            long millis = order.getEndTime().getTime() - order.getStartTime().getTime();
            if (millis <= 0) {
                return null;
            }
            double hours = Math.ceil(millis / 3600000.0);
            if (hours < 1) {
                hours = 1;
            }
            return BigDecimal.valueOf(hours);
        }
        return null;
    }

    /**
     * 根据上下班时间回写整点工时（小时）。
     */
    public static int calcDurationHours(java.util.Date start, java.util.Date end) {
        if (start == null || end == null) {
            return 0;
        }
        long millis = end.getTime() - start.getTime();
        if (millis <= 0) {
            return 0;
        }
        int hours = (int) Math.ceil(millis / 3600000.0);
        return Math.max(1, hours);
    }
}
