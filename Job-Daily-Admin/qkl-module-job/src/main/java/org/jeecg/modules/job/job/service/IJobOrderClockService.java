package org.jeecg.modules.job.job.service;

import org.jeecg.modules.job.job.entity.JobOrderClock;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 打卡记录
 * @Author: jeecg-boot
 * @Date:   2024-11-02
 * @Version: V1.0
 */
public interface IJobOrderClockService extends IService<JobOrderClock> {

    /**
     * 上下班打卡
     * @param orderClock
     * @return
     */
    boolean addOrderClock(JobOrderClock orderClock);

    /**
     * 查询订单打卡消息
     * @param clockType
     * @param orderId
     * @return
     */
    JobOrderClock getOrderClock(int clockType,String orderId);

}
