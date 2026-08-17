package org.jeecg.modules.job.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.job.cms.service.ICmsNoticeService;
import org.jeecg.modules.job.constant.BizConstants;
import org.jeecg.modules.job.home.service.WechatApiService;
import org.jeecg.modules.job.job.entity.JobEvaluateLog;
import org.jeecg.modules.job.job.entity.JobOrder;
import org.jeecg.modules.job.job.mapper.JobEvaluateLogMapper;
import org.jeecg.modules.job.job.mapper.JobOrderMapper;
import org.jeecg.modules.job.job.service.IJobEvaluateLogService;
import org.jeecg.modules.job.job.service.IJobEvaluateService;
import org.jeecg.modules.job.job.service.IJobOrderLogService;
import org.jeecg.modules.job.job.service.IJobOrderService;
import org.jeecg.modules.job.utils.JsonUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Description: 评价记录
 * * @Author: qingkonglan
 * @Date:   2024-08-22
 * @Version: V1.0
 */
@Service
public class JobEvaluateLogServiceImpl extends ServiceImpl<JobEvaluateLogMapper, JobEvaluateLog> implements IJobEvaluateLogService {

    @Resource
    private JobOrderMapper orderMapper;
    @Resource
    private IJobEvaluateService evaluateService;
    @Resource
    private WechatApiService wechatApiService;
    @Resource
    private IJobOrderLogService orderLogService;
    @Resource
    private ICmsNoticeService noticeService;

    @Transactional
    @Override
    public void addEvaluateLog(JobEvaluateLog log) {
        //判断是否已评价
        JobEvaluateLog evaluateLog;
        if (log.getRoleCode().equals(BizConstants.ROLE_CODE_MEMBER)){
            evaluateLog=this.getOne(new QueryWrapper<>(new JobEvaluateLog().setUserId(log.getUserId()).setOrderId(log.getOrderId()).setRoleCode(log.getRoleCode())));
        }else{
            evaluateLog=this.getOne(new QueryWrapper<>(new JobEvaluateLog().setPostId(log.getPostUserId()).setOrderId(log.getOrderId()).setRoleCode(log.getRoleCode())));
        }
        if (evaluateLog!=null){
            throw new RuntimeException("已评价过，请勿重复！");
        }
        JobOrder order=orderMapper.selectById(log.getOrderId());
        if (!order.getOrderStatus().equals(BizConstants.ORDER_STATUS_WAIT_COMMENT)){
            throw new RuntimeException("订单状态不匹配");
        }
        //敏感字校验
        boolean result = wechatApiService.checkText(JsonUtils.objectToJson(log));
        if (result==false){
            throw new RuntimeException("内容存在违规信息");
        }
        log.setPostId(order.getPostId());
        //更新订单状态
        if (log.getRoleCode().equals(BizConstants.ROLE_CODE_MEMBER)){
            log.setPostUserId(order.getPostUserId());
            if (order.getCompanyEvaluate()==1){
                //老板已评价
                order.setOrderStatus(BizConstants.ORDER_STATUS_FINISH);
            }
            order.setUserEvaluate(1);
            //工人通知
            noticeService.addOrderNotice(BizConstants.ROLE_CODE_MEMBER,order.getUserId(),"评价成功","已完成对老板的评价",null,order.getId(),order.getPostId());
        }else{
            log.setUserId(order.getUserId());
            if (order.getUserEvaluate()==1){
                //工人已评价
                order.setOrderStatus(BizConstants.ORDER_STATUS_FINISH);
            }
            order.setCompanyEvaluate(1);
            //老板通知
            noticeService.addOrderNotice(BizConstants.ROLE_CODE_COMPANY,order.getPostUserId(),"评价成功","已完成对工人的评价",null,order.getId(),order.getPostId());
        }
        this.save(log);
        orderMapper.updateById(order);
        //更新用户评分
        if (log.getRoleCode().equals(BizConstants.ROLE_CODE_MEMBER)){//工人评价老板
            evaluateService.updateUserEvaluate(log.getPostUserId(),BizConstants.ROLE_CODE_COMPANY,log.getScore().intValue());
            orderLogService.addOrderLog(order.getOrderStatus(),order.getId(),"工人已完成评价",null);
        }else{ //老板评价工人
            evaluateService.updateUserEvaluate(log.getUserId(),BizConstants.ROLE_CODE_MEMBER,log.getScore().intValue());
            orderLogService.addOrderLog(order.getOrderStatus(),order.getId(),"老板已完成评价",null);
        }
    }

    @Override
    public IPage<Map<String, Object>> getMyEvaluateList(Page<JobEvaluateLog> page, JobEvaluateLog params) {
        return this.baseMapper.getMyEvaluateList(page,params);
    }

    @Override
    public IPage<Map<String, Object>> getEvaluateList(Page<JobEvaluateLog> page, JobEvaluateLog params) {
        if (params.getRoleCode().equals("company")){//老板端查询用户
            return this.baseMapper.getUserEvaluateList(page,params);
        }else{//用户查询老板
            return this.baseMapper.getBossEvaluateList(page,params);
        }
    }


}
