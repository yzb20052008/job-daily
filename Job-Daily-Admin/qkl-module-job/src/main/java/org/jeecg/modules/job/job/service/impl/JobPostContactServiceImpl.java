package org.jeecg.modules.job.job.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.job.base.entity.BaseConfig;
import org.jeecg.modules.job.base.service.IBaseConfigService;
import org.jeecg.modules.job.constant.BizConstants;
import org.jeecg.modules.job.integral.entity.IntegralLog;
import org.jeecg.modules.job.integral.service.IIntegralLogService;
import org.jeecg.modules.job.job.entity.JobPostContact;
import org.jeecg.modules.job.job.mapper.JobPostContactMapper;
import org.jeecg.modules.job.job.service.IJobOrderService;
import org.jeecg.modules.job.job.service.IJobPostContactService;
import org.quartz.Job;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: 拨号记录
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
@Service
public class JobPostContactServiceImpl extends ServiceImpl<JobPostContactMapper, JobPostContact> implements IJobPostContactService {

    @Resource
    private IIntegralLogService integralLogService;
    @Resource
    private IJobOrderService orderService;
    @Resource
    private IBaseConfigService configService;

    @Transactional
    @Override
    public boolean addContact(JobPostContact contact) {
        this.save(contact);
        if (contact.isIfFree()){
            //免费，不扣积分
            return true;
        }
        //扣除积分
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        integralLogService.addIntegralLogForReduce(user.getId(),BizConstants.JF_CALL,contact.getId(),"拨号消费积分：");
        return true;
    }

    @Transactional
    @Override
    public boolean updateAgreeState(String id, int agreeState,String userId) {
        if (agreeState==BizConstants.AGREE_STATUS_SUCCESS){
            //生成订单
            JobPostContact contact=this.getById(id);
            orderService.createOrder(userId,contact.getPostId());
        }
        return this.updateById(new JobPostContact().setId(id).setAgreeState(agreeState));
    }

    @Override
    public JobPostContact getLastContact(String userId, String postId,String roleCode) {
        BaseConfig config=configService.getConfigByCode(BizConstants.CALL_ENSURE_TIME);
        int time=Integer.parseInt(config.getConfigValue());//分钟
        Date date=DateUtil.offsetMinute(new Date(),-time);
        QueryWrapper queryWrapper=new QueryWrapper<>(new JobPostContact().setUserId(userId).setPostId(postId).setRoleCode(roleCode));
        queryWrapper.ge("create_time",date);
        queryWrapper.orderByDesc("create_time");
        queryWrapper.last(" limit 1");
        List<JobPostContact> list =this.list(queryWrapper);
       if (list.size()>0){
           return list.get(0);
       }else{
           return null;
       }
    }

    @Override
    public IPage<Map<String, Object>> getContactList(Page<JobPostContact> page, JobPostContact params) {
        return baseMapper.getContactList(page,params);
    }

    @Override
    public IPage<Map<String, Object>> getContactListForAdmin(Page<JobPostContact> page, JobPostContact params) {
        return baseMapper.getContactListForAdmin(page,params);
    }
}
