package org.jeecg.modules.job.ums.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.job.ums.entity.UmsBalanceRecharge;

import java.math.BigDecimal;

/**
 * 余额充值
 */
public interface IUmsBalanceRechargeService extends IService<UmsBalanceRecharge> {

    /**
     * 创建待支付充值单
     */
    void createRechargeOrder(String orderSn, String userId, BigDecimal money, String payType);

    /**
     * 支付成功入账（幂等）；paidAmount 非空时与单据金额比对
     * @return true 已处理或幂等成功
     */
    boolean paySuccess(String orderSn, BigDecimal paidAmount);
}
