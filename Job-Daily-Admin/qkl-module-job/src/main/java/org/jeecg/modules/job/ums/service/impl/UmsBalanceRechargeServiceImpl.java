package org.jeecg.modules.job.ums.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.job.cms.service.ICmsNoticeService;
import org.jeecg.modules.job.constant.BizConstants;
import org.jeecg.modules.job.ums.entity.UmsBalanceRecharge;
import org.jeecg.modules.job.ums.mapper.UmsBalanceRechargeMapper;
import org.jeecg.modules.job.ums.service.IUmsAccountService;
import org.jeecg.modules.job.ums.service.IUmsBalanceRechargeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * 余额充值：下单落单 → 回调验额入账
 */
@Slf4j
@Service
public class UmsBalanceRechargeServiceImpl extends ServiceImpl<UmsBalanceRechargeMapper, UmsBalanceRecharge>
        implements IUmsBalanceRechargeService {

    @Resource
    private IUmsAccountService accountService;
    @Resource
    private ICmsNoticeService noticeService;

    @Override
    public void createRechargeOrder(String orderSn, String userId, BigDecimal money, String payType) {
        if (oConvertUtils.isEmpty(orderSn) || oConvertUtils.isEmpty(userId)
                || money == null || money.compareTo(BigDecimal.ZERO) <= 0) {
            throw new JeecgBootException("余额充值参数无效");
        }
        UmsBalanceRecharge exists = this.getOne(new QueryWrapper<>(new UmsBalanceRecharge().setOrderSn(orderSn)));
        if (exists != null) {
            throw new JeecgBootException("充值单号已存在");
        }
        UmsBalanceRecharge recharge = new UmsBalanceRecharge();
        recharge.setOrderSn(orderSn);
        recharge.setUserId(userId);
        recharge.setMoney(money.setScale(2, java.math.RoundingMode.HALF_UP));
        recharge.setPayType(payType);
        recharge.setRechargeStatus(BizConstants.RECHARGE_STATUS_DEFAULT);
        this.save(recharge);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean paySuccess(String orderSn, BigDecimal paidAmount) {
        if (oConvertUtils.isEmpty(orderSn)) {
            log.error("余额充值回调缺少订单号");
            return false;
        }
        UmsBalanceRecharge order = this.getOne(new QueryWrapper<>(new UmsBalanceRecharge().setOrderSn(orderSn)));
        if (order == null) {
            log.error("余额充值单不存在 orderSn={}", orderSn);
            return false;
        }
        if (BizConstants.RECHARGE_STATUS_SUCCESS.equals(order.getRechargeStatus())) {
            log.warn("余额充值已入账，忽略重复回调 orderSn={}", orderSn);
            return true;
        }
        if (!BizConstants.RECHARGE_STATUS_DEFAULT.equals(order.getRechargeStatus())) {
            log.error("余额充值单状态异常 orderSn={}, status={}", orderSn, order.getRechargeStatus());
            return false;
        }
        if (paidAmount != null && order.getMoney().compareTo(paidAmount) != 0) {
            log.error("余额充值金额与渠道实付不一致 orderSn={}, orderAmount={}, paidAmount={}",
                    orderSn, order.getMoney(), paidAmount);
            return false;
        }
        order.setRechargeStatus(BizConstants.RECHARGE_STATUS_SUCCESS);
        this.updateById(order);
        accountService.addMemberBalance(order.getMoney(), BizConstants.TRADE_TYPE_RECHARGE_BALANCE,
                order.getUserId(), "余额充值：" + order.getMoney() + "元");
        noticeService.addBalanceNotice(order.getUserId(), "充值成功",
                "余额充值成功，到账金额：" + order.getMoney() + "元", order.getId());
        return true;
    }
}
