package org.jeecg.modules.job.integral.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.job.integral.entity.IntegralGoodsOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.job.integral.entity.IntegralRecharge;

/**
 * @Description: 积分订单
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
public interface IntegralGoodsOrderMapper extends BaseMapper<IntegralGoodsOrder> {


    /**
     * 分页查询积分订单
     * @param page
     * @param params 参数信息
     * @return
     */
    IPage<Map<String,Object>> getGoodsOrderList(IPage page, @Param("params") IntegralGoodsOrder params);

}
