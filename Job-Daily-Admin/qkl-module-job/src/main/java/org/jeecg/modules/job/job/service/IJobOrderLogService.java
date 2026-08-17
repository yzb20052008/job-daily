package org.jeecg.modules.job.job.service;

import org.jeecg.modules.job.job.entity.JobOrderLog;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 订单操作记录
 * * @Author: qingkonglan
 * @Date:   2024-08-26
 * @Version: V1.0
 */
public interface IJobOrderLogService extends IService<JobOrderLog> {

    /**
     * 添加订单操作日志
     * @param orderId
     * @param note
     * @param imgs
     * @return
     */
    boolean addOrderLog(String orderStatus,String orderId,String note,String imgs);

}
