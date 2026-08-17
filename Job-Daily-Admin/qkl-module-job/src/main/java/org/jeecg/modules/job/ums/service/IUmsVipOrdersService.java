package org.jeecg.modules.job.ums.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.job.ums.entity.UmsVipOrders;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: VIP订单
 * @Author: qingkonglan
 * @Date:   2022-12-18
 * @Version: V1.0
 */
public interface IUmsVipOrdersService extends IService<UmsVipOrders> {



    /**
     * 购买VIP订单
     * @param vipOrders
     * @return
     */
    boolean createVipOrder(UmsVipOrders vipOrders);


    /**
     * 分页查询订单
     * @param page
     * @param paramCondition 参数信息
     * @return
     */
    IPage<UmsVipOrders> getPageListForAdmin(Page<UmsVipOrders> page, @Param("paramCondition") UmsVipOrders paramCondition);

}
