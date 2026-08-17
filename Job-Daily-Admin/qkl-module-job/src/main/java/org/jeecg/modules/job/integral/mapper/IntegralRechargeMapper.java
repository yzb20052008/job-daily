package org.jeecg.modules.job.integral.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.job.integral.entity.IntegralRecharge;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.job.job.entity.JobCollect;

/**
 * @Description: 积分充值记录
 * * @Author: qingkonglan
 * @Date:   2024-08-29
 * @Version: V1.0
 */
public interface IntegralRechargeMapper extends BaseMapper<IntegralRecharge> {

    /**
     * 分页查询收藏记录
     * @param page
     * @param params 参数信息
     * @return
     */
    IPage<Map<String,Object>> getRechargeOrderList(IPage page, @Param("params") IntegralRecharge params);

}
