package org.jeecg.modules.job.integral.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.job.integral.entity.IntegralGoodsOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.job.integral.entity.IntegralRecharge;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @Description: 积分订单
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
public interface IIntegralGoodsOrderService extends IService<IntegralGoodsOrder> {

    /**
     * 添加积分订单
     * @param userId 用户ID
     * @param number 购买数量
     * @param goodsId 购买道具
     * @param amount 支付积分
     * @param dataId 关联数据ID
     * @return
     */
    boolean createJfOrder(String userId, String number, String goodsId, String amount,String dataId);


    /**
     * 分页查询积分订单
     * @param page
     * @param params 参数信息
     * @return
     */
    IPage<Map<String,Object>> getGoodsOrderList(Page<IntegralGoodsOrder> page, IntegralGoodsOrder params);

}
