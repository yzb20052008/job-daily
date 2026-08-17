package org.jeecg.modules.job.finance.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.job.constant.BizConstants;
import org.jeecg.modules.job.job.entity.JobOrder;
import org.jeecg.modules.job.job.service.IJobOrderService;
import org.jeecg.modules.job.pay.entity.TransferToUserResponse;
import org.jeecg.modules.job.ums.entity.UmsWithdraw;
import org.jeecg.modules.job.ums.service.IUmsWithdrawService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 资金日终对账：扫描异常提现/结算单并尝试补齐转账状态。
 */
@Slf4j
@Service
public class FinanceReconService {

    @Resource
    private IUmsWithdrawService withdrawService;
    @Resource
    private IJobOrderService orderService;

    /**
     * 执行日终对账扫描
     */
    public void dailyRecon() {
        Date end = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(end);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date start = cal.getTime();

        reconWithdrawTransfer();
        reconSalaryOrders(start, end);
        log.info("资金日终对账完成 window=[{} ~ {}]", start, end);
    }

    /**
     * 审核通过但转账未终态：
     * 1) 无商户单号 → 关闭并解冻（确认从未发起渠道转账）
     * 2) 有商户单号 → 向微信查单并回写
     */
    private void reconWithdrawTransfer() {
        QueryWrapper<UmsWithdraw> qw = new QueryWrapper<>();
        qw.eq("withdraw_status", BizConstants.WITHDRAW_STATUS_SUCCESS);
        qw.and(w -> w.isNull("transfer_status")
                .or().eq("transfer_status", "")
                .or().in("transfer_status",
                        BizConstants.TRANSFER_STATUS_ACCEPTED,
                        BizConstants.TRANSFER_STATUS_PROCESSING,
                        BizConstants.TRANSFER_STATUS_WAIT_USER_CONFIRM,
                        BizConstants.TRANSFER_STATUS_TRANSFERING,
                        BizConstants.TRANSFER_STATUS_CANCELING));
        List<UmsWithdraw> list = withdrawService.list(qw);
        int synced = 0;
        int closed = 0;
        int failed = 0;
        for (UmsWithdraw item : list) {
            if (oConvertUtils.isEmpty(item.getOutBillNo())) {
                try {
                    withdrawService.closeAbnormalWithdraw(item.getId(),
                            "日终对账自动关闭：审核通过但未生成转账单号，已解冻");
                    closed++;
                    log.warn("已关闭无商户单号异常提现 id={}, money={}", item.getId(), item.getMoney());
                } catch (Exception e) {
                    failed++;
                    log.error("关闭无商户单号提现失败 id={}", item.getId(), e);
                }
                continue;
            }
            try {
                TransferToUserResponse resp = withdrawService.getTransferByOutBillNo(item.getOutBillNo());
                if (resp == null) {
                    log.warn("提现查单无结果 outBillNo={}", item.getOutBillNo());
                    failed++;
                } else {
                    synced++;
                    log.info("提现查单回写 outBillNo={}, state={}", item.getOutBillNo(), resp.getState());
                }
            } catch (Exception e) {
                failed++;
                log.error("提现查单异常 outBillNo={}", item.getOutBillNo(), e);
            }
        }
        log.info("提现对账：待处理={}, 查单回写={}, 无单号关闭={}, 失败/无结果={}",
                list.size(), synced, closed, failed);
    }

    /**
     * 扫描近一日已发起支付但未完成结算的订单，输出告警日志供人工处理
     */
    private void reconSalaryOrders(Date start, Date end) {
        QueryWrapper<JobOrder> qw = new QueryWrapper<>();
        qw.eq("order_status", BizConstants.ORDER_STATUS_WAIT_PAY);
        qw.isNotNull("order_sn");
        qw.ne("order_sn", "");
        qw.and(w -> w.isNull("pay_status").or().ne("pay_status", "1"));
        qw.between("update_time", start, end);
        List<JobOrder> list = orderService.list(qw);
        BigDecimal total = BigDecimal.ZERO;
        for (JobOrder order : list) {
            if (order.getAmount() != null) {
                total = total.add(order.getAmount());
            }
            log.warn("待结算卡单 orderId={}, orderSn={}, amount={}, updateTime={}",
                    order.getId(), order.getOrderSn(), order.getAmount(), order.getUpdateTime());
        }
        log.info("结算对账：近一日卡单数={}, 金额合计={}", list.size(), total);
    }
}
