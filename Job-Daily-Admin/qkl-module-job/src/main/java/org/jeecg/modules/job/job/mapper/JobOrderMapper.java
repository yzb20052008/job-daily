package org.jeecg.modules.job.job.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.job.job.entity.JobOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.job.job.entity.JobPost;

/**
 * @Description: 订单信息
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
public interface JobOrderMapper extends BaseMapper<JobOrder> {

    /**
     * 分页查询订单列表
     * @param page
     * @param params 参数信息
     * @return
     */
    IPage<Map<String,Object>> getOrderList(IPage page, @Param("params") JobOrder params);

    /**
     * 分页查询订单列表
     * @param page
     * @param params 参数信息
     * @return
     */
    IPage<Map<String,Object>> getOrderListForAdmin(IPage page, @Param("params") JobOrder params);


    /**
     * 分页查询订单列表
     * @param page
     * @param params 参数信息
     * @return
     */
    IPage<Map<String,Object>> getPostUserList(IPage page, @Param("params") JobOrder params);

}
