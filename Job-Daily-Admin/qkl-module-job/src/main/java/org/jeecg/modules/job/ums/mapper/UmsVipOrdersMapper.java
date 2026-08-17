package org.jeecg.modules.job.ums.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.job.ums.entity.UmsVipOrders;

/**
 * @Description: VIP订单
 * @Author: qingkonglan
 * @Date:   2022-12-18
 * @Version: V1.0
 */
public interface UmsVipOrdersMapper extends BaseMapper<UmsVipOrders> {

    /**
     * 分页查询订单
     * @param page
     * @param paramCondition 参数信息
     * @return
     */
    IPage<UmsVipOrders> getPageListForAdmin(IPage page, @Param("paramCondition") UmsVipOrders paramCondition);

}
