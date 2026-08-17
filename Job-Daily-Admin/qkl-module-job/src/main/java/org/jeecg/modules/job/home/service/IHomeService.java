package org.jeecg.modules.job.home.service;

import java.util.Map;

public interface IHomeService {

    /**
     * 查询用户统计信息
     * @param startTime 开始时间 2023-10-20
     * @param endTime 结束时间 2023-10-21
     * @return
     */
    Map<String,Object> getUserCountInfo(String startTime, String endTime, String roleId);

    /**
     * 查询企业统计信息
     * @param startTime 开始时间 2023-10-20
     * @param endTime 结束时间 2023-10-21
     * @param verifyStatus 审核状态 0-默认，1-待审核，2-审核失败，3-审核通过
     * @return
     */
    Map<String,Object> getCompanyCountInfo(String startTime,String endTime,String authStatus);

    /**
     * 查询岗位统计信息
     * @param startTime 开始时间 2023-10-20
     * @param endTime 结束时间 2023-10-21
     * @param  status 1-待开放、2-已开放、3-已关闭、4-审核失败
     * @return
     */
    Map<String,Object> getPositionCountInfo(String startTime,  String endTime,String status);


    /**
     * 查询订单统计信息
     * @param startTime 开始时间 2023-10-20
     * @param endTime 结束时间 2023-10-21
     * @param  orderStatus 0-待确认，1-待开工，2-工作中，3-待结算，4-待评价，5-已完成，6-已取消
     * @return
     */
    Map<String,Object> getOrderCountInfo(String startTime,  String endTime,String orderStatus);

}
