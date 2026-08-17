package org.jeecg.modules.job.job.service.impl;

import org.jeecg.modules.job.constant.BizConstants;
import org.jeecg.modules.job.job.entity.JobOrderLog;
import org.jeecg.modules.job.job.mapper.JobOrderLogMapper;
import org.jeecg.modules.job.job.service.IJobOrderLogService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 订单操作记录
 * * @Author: qingkonglan
 * @Date:   2024-08-26
 * @Version: V1.0
 */
@Service
public class JobOrderLogServiceImpl extends ServiceImpl<JobOrderLogMapper, JobOrderLog> implements IJobOrderLogService {


    @Override
    public boolean addOrderLog(String orderStatus,String orderId, String note, String imgs) {
        if (note==null){
            if (BizConstants.ORDER_STATUS_WAIT_ENSURE.equals(orderStatus)){
                note="工人确认生成订单";
            }else if(BizConstants.ORDER_STATUS_WAIT_START.equals(orderStatus)){
                note="老板确认订单";
            }else if(BizConstants.ORDER_STATUS_WORKING.equals(orderStatus)){
                note="工人上班打卡";
            }else if(BizConstants.ORDER_STATUS_WAIT_PAY.equals(orderStatus)){
                note="工人下班打卡";
            }else if(BizConstants.ORDER_STATUS_WAIT_COMMENT.equals(orderStatus)){
                note="老板已结算";
            }else if(BizConstants.ORDER_STATUS_FINISH.equals(orderStatus)){
                note="已评价";
            }else if(BizConstants.ORDER_STATUS_CANCEL.equals(orderStatus)){
                note="订单已取消";
            }
        }
        JobOrderLog log=new JobOrderLog();
        log.setOrderId(orderId);
        log.setRemark(note);
        log.setImgs(imgs);
        return save(log);
    }
}
