package org.jeecg.modules.job.api.vo;

import lombok.Data;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.job.job.entity.JobOrder;
import org.jeecg.modules.job.job.entity.JobOrderClock;
import org.jeecg.modules.job.job.entity.JobPost;
import org.springframework.beans.BeanUtils;

@Data
public class JobOrderVo extends JobOrder {
    //上班打卡
    private JobOrderClock startClock;
    //下班打卡
    private JobOrderClock endClock;
    //岗位信息
    private JobPost post;
    //发布人信息
    private LoginUser postUser;
    //工人信息
    private LoginUser user;

    public void setJobOrder(JobOrder order){
        BeanUtils.copyProperties(order, this);
    }


}
