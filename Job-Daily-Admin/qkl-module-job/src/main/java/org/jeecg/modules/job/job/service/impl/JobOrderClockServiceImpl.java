package org.jeecg.modules.job.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.job.cms.service.ICmsNoticeService;
import org.jeecg.modules.job.constant.BizConstants;
import org.jeecg.modules.job.job.entity.JobOrder;
import org.jeecg.modules.job.job.entity.JobOrderClock;
import org.jeecg.modules.job.job.mapper.JobOrderClockMapper;
import org.jeecg.modules.job.job.mapper.JobOrderMapper;
import org.jeecg.modules.job.job.service.IJobOrderClockService;
import org.jeecg.modules.job.job.service.IJobOrderLogService;
import org.jeecg.modules.job.job.support.OrderStatusMachine;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Description: 打卡记录
 * @Author: jeecg-boot
 * @Date:   2024-11-02
 * @Version: V1.0
 */
@Service
public class JobOrderClockServiceImpl extends ServiceImpl<JobOrderClockMapper, JobOrderClock> implements IJobOrderClockService {

    @Resource
    private JobOrderMapper orderMapper;
    @Resource
    private IJobOrderLogService orderLogService;
    @Resource
    private ICmsNoticeService noticeService;

    @Transactional
    @Override
    public boolean addOrderClock(JobOrderClock orderClock) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (user == null) {
            throw new JeecgBootException("请先登录");
        }
        JobOrder order = orderMapper.selectById(orderClock.getOrderId());
        if (order == null) {
            throw new JeecgBootException("订单不存在");
        }
        if (!user.getId().equals(order.getUserId())) {
            throw new JeecgBootException("非本人打卡");
        }
        // 上班打卡
        if (BizConstants.CLOCK_TYPE_ON.equals(orderClock.getClockType())) {
            OrderStatusMachine.assertTransition(order.getOrderStatus(), BizConstants.ORDER_STATUS_WORKING);
            order.setOrderStatus(BizConstants.ORDER_STATUS_WORKING);
            order.setStartTime(new Date());
            noticeService.addOrderNotice(BizConstants.ROLE_CODE_MEMBER, order.getUserId(), "上班打卡成功", "上班打卡成功~", orderClock.getImages(), order.getId(), order.getPostId());
            noticeService.addOrderNotice(BizConstants.ROLE_CODE_COMPANY, order.getPostUserId(), "工人上班打卡", "已有工人完成上班打卡，点击可查看详情~~", orderClock.getImages(), order.getId(), order.getPostId());
        } else if (BizConstants.CLOCK_TYPE_OFF.equals(orderClock.getClockType())) {
            OrderStatusMachine.assertTransition(order.getOrderStatus(), BizConstants.ORDER_STATUS_WAIT_PAY);
            order.setOrderStatus(BizConstants.ORDER_STATUS_WAIT_PAY);
            order.setEndTime(new Date());
            noticeService.addOrderNotice(BizConstants.ROLE_CODE_MEMBER, order.getUserId(), "下班打卡成功", "下班打卡成功~", orderClock.getImages(), order.getId(), order.getPostId());
            noticeService.addOrderNotice(BizConstants.ROLE_CODE_COMPANY, order.getPostUserId(), "工人下班打卡", "已有工人完成下班打卡,请尽快完成工人工资结算~~", orderClock.getImages(), order.getId(), order.getPostId());
        } else {
            throw new JeecgBootException("打卡类型无效");
        }
        orderMapper.updateById(order);
        orderLogService.addOrderLog(order.getOrderStatus(), order.getId(), null, orderClock.getImages());
        return this.save(orderClock);
    }

    @Override
    public JobOrderClock getOrderClock(int clockType, String orderId) {
        List<JobOrderClock> clockList = this.list(new QueryWrapper<>(new JobOrderClock().setOrderId(orderId).setClockType(clockType)).orderByDesc("create_time"));
        if (clockList.size() > 0) {
            return clockList.get(0);
        }
        return null;
    }
}
