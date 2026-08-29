package org.jeecg.modules.job.job.support;

import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.job.job.entity.JobOrder;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 用工结算金额核算：优先单价×工时，无法核算时允许协商金额（带上限）。
 */
public final class SalaryCalcHelper {

    /** 协商金额上限（元），防异常大额下单 */
    private static final BigDecimal MAX_NEGOTIATED = new BigDecimal("99999.99");

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
     * 解析并校验客户端申报金额：有核算价则必须一致；否则接受协商价。
     */
    public static BigDecimal resolvePayAmount(JobOrder order, BigDecimal clientAmount) {
        BigDecimal suggest = calcSuggestAmount(order);
        if (suggest != null) {
            if (clientAmount == null || clientAmount.compareTo(suggest) != 0) {
                throw new JeecgBootException("结算金额须等于系统核算金额：" + suggest.toPlainString() + "元（单价×工时）");
            }
            return suggest;
        }
        if (clientAmount == null || clientAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new JeecgBootException("结算金额必须大于0");
        }
        BigDecimal money = clientAmount.setScale(2, RoundingMode.HALF_UP);
        if (money.compareTo(MAX_NEGOTIATED) > 0) {
            throw new JeecgBootException("结算金额超出上限");
        }
        return money;
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
