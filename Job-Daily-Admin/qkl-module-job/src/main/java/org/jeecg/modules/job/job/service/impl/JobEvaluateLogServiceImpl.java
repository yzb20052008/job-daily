package org.jeecg.modules.job.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.job.cms.service.ICmsNoticeService;
import org.jeecg.modules.job.constant.BizConstants;
import org.jeecg.modules.job.constant.BizErrorCodes;
import org.jeecg.modules.job.exception.BizException;
import org.jeecg.modules.job.home.service.WechatApiService;
import org.jeecg.modules.job.job.entity.JobEvaluateLog;
import org.jeecg.modules.job.job.entity.JobOrder;
import org.jeecg.modules.job.job.mapper.JobEvaluateLogMapper;
import org.jeecg.modules.job.job.mapper.JobOrderMapper;
import org.jeecg.modules.job.job.service.IJobEvaluateLogService;
import org.jeecg.modules.job.job.service.IJobEvaluateService;
import org.jeecg.modules.job.job.service.IJobOrderLogService;
import org.jeecg.modules.job.job.support.OrderStatusMachine;
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addEvaluateLog(JobEvaluateLog log) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (loginUser == null) {
            throw BizException.of(BizErrorCodes.NOT_LOGIN);
        }
        JobOrder order = orderMapper.selectById(log.getOrderId());
        if (order == null) {
            throw BizException.of(BizErrorCodes.ORDER_NOT_FOUND);
        }
        if (!BizConstants.ORDER_STATUS_WAIT_COMMENT.equals(order.getOrderStatus())) {
            throw BizException.of(BizErrorCodes.ORDER_STATUS_INVALID, "订单状态不匹配");
        }
        // 当事人校验
        if (BizConstants.ROLE_CODE_MEMBER.equals(log.getRoleCode())) {
            if (!loginUser.getId().equals(order.getUserId())) {
                throw BizException.of(BizErrorCodes.ORDER_PERMISSION);
            }
            log.setUserId(order.getUserId());
        } else if (BizConstants.ROLE_CODE_COMPANY.equals(log.getRoleCode())) {
            if (!loginUser.getId().equals(order.getPostUserId())) {
                throw BizException.of(BizErrorCodes.ORDER_PERMISSION);
            }
            log.setPostUserId(order.getPostUserId());
        } else {
            throw BizException.of(BizErrorCodes.PARAM_INVALID, "评价角色无效");
        }
        // 防重：同一订单同一角色仅一次
        JobEvaluateLog evaluateLog = this.getOne(new QueryWrapper<>(new JobEvaluateLog()
                .setOrderId(log.getOrderId())
                .setRoleCode(log.getRoleCode())));
        if (evaluateLog != null) {
            throw BizException.of(BizErrorCodes.PARAM_INVALID, "已评价过，请勿重复");
        }
        boolean result = wechatApiService.checkText(JsonUtils.objectToJson(log));
        if (!result) {
            throw new RuntimeException("内容存在违规信息");
        }
        log.setPostId(order.getPostId());
        if (BizConstants.ROLE_CODE_MEMBER.equals(log.getRoleCode())) {
            log.setPostUserId(order.getPostUserId());
            if (order.getCompanyEvaluate() != null && order.getCompanyEvaluate() == 1) {
                OrderStatusMachine.assertTransition(order.getOrderStatus(), BizConstants.ORDER_STATUS_FINISH);
                order.setOrderStatus(BizConstants.ORDER_STATUS_FINISH);
            }
            order.setUserEvaluate(1);
            noticeService.addOrderNotice(BizConstants.ROLE_CODE_MEMBER, order.getUserId(), "评价成功", "已完成对老板的评价", null, order.getId(), order.getPostId());
        } else {
            log.setUserId(order.getUserId());
            if (order.getUserEvaluate() != null && order.getUserEvaluate() == 1) {
                OrderStatusMachine.assertTransition(order.getOrderStatus(), BizConstants.ORDER_STATUS_FINISH);
                order.setOrderStatus(BizConstants.ORDER_STATUS_FINISH);
            }
            order.setCompanyEvaluate(1);
            noticeService.addOrderNotice(BizConstants.ROLE_CODE_COMPANY, order.getPostUserId(), "评价成功", "已完成对工人的评价", null, order.getId(), order.getPostId());
        }
        this.save(log);
        orderMapper.updateById(order);
        if (BizConstants.ROLE_CODE_MEMBER.equals(log.getRoleCode())) {
            evaluateService.updateUserEvaluate(log.getPostUserId(), BizConstants.ROLE_CODE_COMPANY, log.getScore().intValue());
            orderLogService.addOrderLog(order.getOrderStatus(), order.getId(), "工人已完成评价", null);
        } else {
            evaluateService.updateUserEvaluate(log.getUserId(), BizConstants.ROLE_CODE_MEMBER, log.getScore().intValue());
            orderLogService.addOrderLog(order.getOrderStatus(), order.getId(), "老板已完成评价", null);
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
