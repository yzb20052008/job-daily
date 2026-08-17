package org.jeecg.modules.job.ums.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.job.ums.entity.UmsAccount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 会员账户
 * @Author: qingkonglan
 * @Date:   2022-12-23
 * @Version: V1.0
 */
public interface UmsAccountMapper extends BaseMapper<UmsAccount> {

    /**
     * 查询用户统计信息
     * @param startTime 开始时间 2023-10-20
     * @param endTime 结束时间 2023-10-21
     * @return
     */
    Map<String,Object> getUserCountInfo(@Param("startTime") String startTime, @Param("endTime")String endTime, @Param("roleId")String roleId);

    /**
     * 查询企业统计信息
     * @param startTime 开始时间 2023-10-20
     * @param endTime 结束时间 2023-10-21
     * @param authStatus 审核状态 0-默认，1-待审核，2-审核失败，3-审核通过
     * @return
     */
    Map<String,Object> getCompanyCountInfo(@Param("startTime") String startTime, @Param("endTime")String endTime, @Param("authStatus")String authStatus);

    /**
     * 查询岗位统计信息
     * @param startTime 开始时间 2023-10-20
     * @param endTime 结束时间 2023-10-21
     * @param  postStatus 1-待审核，2-招工中，3-发布失败，4-已停招，5-已取消，6-已招满
     * @return
     */
    Map<String,Object> getPostCountInfo(@Param("startTime") String startTime, @Param("endTime")String endTime, @Param("postStatus")String postStatus);


    /**
     * 查询订单统计信息
     * @param startTime 开始时间 2023-10-20
     * @param endTime 结束时间 2023-10-21
     * @param  orderStatus 0-待确认，1-待开工，2-工作中，3-待结算，4-待评价，5-已完成，6-已取消
     * @return
     */
    Map<String,Object> getOrderCountInfo(@Param("startTime") String startTime, @Param("endTime")String endTime, @Param("orderStatus")String orderStatus);

}
