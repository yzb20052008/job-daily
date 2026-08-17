package org.jeecg.modules.job.integral.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.job.integral.entity.IntegralLog;
import org.jeecg.modules.job.integral.entity.IntegralRecharge;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.job.job.entity.JobCollect;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @Description: 积分充值记录
 * * @Author: qingkonglan
 * @Date:   2024-08-29
 * @Version: V1.0
 */
public interface IIntegralRechargeService extends IService<IntegralRecharge> {

    /**
     * 余额购买积分
     * @param userId 用户ID
     * @param money 消费余额
     * @return
     */
    boolean yePayIntegral(String userId, BigDecimal money);



    /**
     * 创建充值订单
     * @param orderSn
     * @param userId
     * @param money
     * @param payType
     */
    void createRechargeOrder(String orderSn,String userId,String money,String payType);

    /**
     * 支付成功
     * @param orderSn
     */
    void updateRechargeOrder(String orderSn);


    /**
     * 分页查询充值订单
     * @param page
     * @param params 参数信息
     * @return
     */
    IPage<Map<String,Object>> getRechargeOrderList(Page<IntegralRecharge> page, IntegralRecharge params);


}
