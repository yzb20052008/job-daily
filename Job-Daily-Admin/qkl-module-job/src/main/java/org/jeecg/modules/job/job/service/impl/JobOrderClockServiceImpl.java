package org.jeecg.modules.job.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.job.base.entity.BaseConfig;
import org.jeecg.modules.job.base.service.IBaseConfigService;
import org.jeecg.modules.job.cms.service.ICmsNoticeService;
import org.jeecg.modules.job.constant.BizConstants;
import org.jeecg.modules.job.job.entity.JobOrder;
import org.jeecg.modules.job.job.entity.JobOrderClock;
import org.jeecg.modules.job.job.entity.JobPost;
import org.jeecg.modules.job.job.mapper.JobOrderClockMapper;
import org.jeecg.modules.job.job.mapper.JobOrderMapper;
import org.jeecg.modules.job.job.mapper.JobPostMapper;
import org.jeecg.modules.job.job.service.IJobOrderClockService;
import org.jeecg.modules.job.job.service.IJobOrderLogService;
import org.jeecg.modules.job.job.support.ClockGeoHelper;
import org.jeecg.modules.job.job.support.OrderStatusMachine;
import org.jeecg.modules.job.job.support.SalaryCalcHelper;
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
    private JobPostMapper postMapper;
    @Resource
    private IJobOrderLogService orderLogService;
    @Resource
    private ICmsNoticeService noticeService;
    @Resource
    private IBaseConfigService configService;

    @Transactional(rollbackFor = Exception.class)
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
        if (oConvertUtils.isEmpty(orderClock.getImages())) {
            throw new JeecgBootException("请上传打卡图片凭证");
        }
        // 同类型防重
        long sameTypeCount = this.count(new QueryWrapper<>(new JobOrderClock()
                .setOrderId(orderClock.getOrderId())
                .setClockType(orderClock.getClockType())));
        if (sameTypeCount > 0) {
            throw new JeecgBootException("该类型打卡已完成，请勿重复操作");
        }
        // 地理围栏：岗位有坐标时打卡坐标必填且校验距离
        JobPost post = postMapper.selectById(order.getPostId());
        if (post != null) {
            boolean postHasGeo = oConvertUtils.isNotEmpty(post.getLatitude())
                    && oConvertUtils.isNotEmpty(post.getLongitude());
            if (postHasGeo) {
                if (oConvertUtils.isEmpty(orderClock.getLatitude())
                        || oConvertUtils.isEmpty(orderClock.getLongitude())) {
                    throw new JeecgBootException("请开启定位后再打卡");
                }
                Double distance = ClockGeoHelper.distanceMeters(
                        post.getLatitude(), post.getLongitude(),
                        orderClock.getLatitude(), orderClock.getLongitude());
                if (distance == null) {
                    throw new JeecgBootException("打卡坐标无效");
                }
                double maxMeters = ClockGeoHelper.DEFAULT_MAX_METERS;
                BaseConfig rangeCfg = configService.getConfigByCode(BizConstants.CLOCK_RANGE);
                if (rangeCfg != null) {
                    maxMeters = ClockGeoHelper.resolveMaxMeters(rangeCfg.getConfigValue());
                }
                if (distance > maxMeters) {
                    throw new JeecgBootException("打卡位置超出岗位允许范围（" + (int) maxMeters + "米）");
                }
                orderClock.setDistance(String.valueOf(Math.round(distance)));
            }
        }

        Date now = new Date();
        if (BizConstants.CLOCK_TYPE_ON.equals(orderClock.getClockType())) {
            OrderStatusMachine.assertTransition(order.getOrderStatus(), BizConstants.ORDER_STATUS_WORKING);
            // 已过用工结束时间不可再开工（与 OrderJob 自动取消策略一致）
            if (order.getEndTime() != null && !now.before(order.getEndTime())) {
                throw new JeecgBootException("用工已过结束时间，无法上班打卡，订单将自动取消");
            }
            // 时间窗：最早允许岗位开始前 2 小时
            if (order.getStartTime() != null) {
                long earliest = order.getStartTime().getTime() - 2L * 3600_000;
                if (now.getTime() < earliest) {
                    throw new JeecgBootException("未到可上班打卡时间");
                }
            }
            order.setOrderStatus(BizConstants.ORDER_STATUS_WORKING);
            order.setStartTime(now);
            noticeService.addOrderNotice(BizConstants.ROLE_CODE_MEMBER, order.getUserId(), "上班打卡成功", "上班打卡成功~", orderClock.getImages(), order.getId(), order.getPostId());
            noticeService.addOrderNotice(BizConstants.ROLE_CODE_COMPANY, order.getPostUserId(), "工人上班打卡", "已有工人完成上班打卡，点击可查看详情~~", orderClock.getImages(), order.getId(), order.getPostId());
        } else if (BizConstants.CLOCK_TYPE_OFF.equals(orderClock.getClockType())) {
            OrderStatusMachine.assertTransition(order.getOrderStatus(), BizConstants.ORDER_STATUS_WAIT_PAY);
            if (order.getStartTime() != null && now.before(order.getStartTime())) {
                throw new JeecgBootException("下班时间不能早于上班时间");
            }
            order.setOrderStatus(BizConstants.ORDER_STATUS_WAIT_PAY);
            order.setEndTime(now);
            int hours = SalaryCalcHelper.calcDurationHours(order.getStartTime(), now);
            if (hours > 0) {
                order.setDuration(hours);
            }
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
