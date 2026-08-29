package org.jeecg.modules.job.job.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.job.api.util.WxUtil;
import org.jeecg.modules.job.api.vo.JobOrderVo;
import org.jeecg.modules.job.cms.service.ICmsNoticeService;
import org.jeecg.modules.job.constant.BizConstants;
import org.jeecg.modules.job.constant.BizErrorCodes;
import org.jeecg.modules.job.constant.MsgTemplateCodes;
import org.jeecg.modules.job.exception.BizException;
import org.jeecg.modules.job.integral.service.IIntegralLogService;
import org.jeecg.modules.job.job.entity.JobOrder;
import org.jeecg.modules.job.job.entity.JobPost;
import org.jeecg.modules.job.job.mapper.JobOrderMapper;
import org.jeecg.modules.job.job.mapper.JobPostMapper;
import org.jeecg.modules.job.job.service.IJobOrderClockService;
import org.jeecg.modules.job.job.service.IJobOrderLogService;
import org.jeecg.modules.job.job.service.IJobOrderService;
import org.jeecg.modules.job.job.support.OrderStatusMachine;
import org.jeecg.modules.job.job.support.SalaryCalcHelper;
import org.jeecg.modules.job.msg.service.IBizMsgTemplateService;
import org.jeecg.modules.job.ums.service.IUmsAccountRecordsService;
import org.jeecg.modules.job.ums.service.IUmsAccountService;
import org.jeecg.modules.job.utils.WX_TemplateMsgUtil;
import org.jeecg.modules.job.utils.WxMsgSendUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 订单信息
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
@Slf4j
@Service
public class JobOrderServiceImpl extends ServiceImpl<JobOrderMapper, JobOrder> implements IJobOrderService {

    @Resource
    private JobPostMapper postMapper;
    @Resource
    private IJobOrderLogService orderLogService;
    @Resource
    private IUmsAccountService accountService;
    @Resource
    private IUmsAccountRecordsService accountRecordsService;
    @Resource
    private ISysBaseAPI sysBaseAPI;
    @Resource
    private IJobOrderClockService clockService;
    @Resource
    private ICmsNoticeService noticeService;
    @Resource
    private IIntegralLogService integralLogService;
    @Resource
    private IBizMsgTemplateService msgTemplateService;
    @Resource
    private org.jeecg.modules.job.base.service.IBaseConfigService configService;
    @Resource
    private TransactionTemplate transactionTemplate;

    @Override
    public JobOrderVo getOrderDetail(String id) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (loginUser == null) {
            throw BizException.of(BizErrorCodes.NOT_LOGIN);
        }
        JobOrder order = this.getById(id);
        if (order == null) {
            throw BizException.of(BizErrorCodes.ORDER_NOT_FOUND);
        }
        if (!loginUser.getId().equals(order.getUserId())
                && !loginUser.getId().equals(order.getPostUserId())) {
            throw BizException.of(BizErrorCodes.ORDER_PERMISSION);
        }
        JobOrderVo orderVo = new JobOrderVo();
        orderVo.setJobOrder(order);
        //打卡信息
        orderVo.setStartClock(clockService.getOrderClock(BizConstants.CLOCK_TYPE_ON, id));
        //下班
        orderVo.setEndClock(clockService.getOrderClock(BizConstants.CLOCK_TYPE_OFF, id));
        //岗位信息
        orderVo.setPost(postMapper.selectById(order.getPostId()));
        //发布人信息
        orderVo.setPostUser(sysBaseAPI.getUserById(order.getPostUserId()));
        //工人信息
        orderVo.setUser(sysBaseAPI.getUserById(order.getUserId()));
        return orderVo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean doApply(String userId, String postId, Integer integral) {
        // 客户端 integral 仅兼容旧入参，实际扣减以服务端配置为准
        JobOrder order = this.createOrderAndReturn(userId, postId);
        integralLogService.addIntegralLogForReduce(userId, BizConstants.JF_CALL, order.getId(), "提交报名消费积分：");
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean createOrder(String userId, String postId) {
        this.createOrderAndReturn(userId, postId);
        return true;
    }

    /**
     * 创建报名订单并返回实体（供扣积分绑定 dataId）
     */
    private JobOrder createOrderAndReturn(String userId, String postId) {
        JobPost post = postMapper.selectById(postId);
        if (post == null) {
            throw BizException.of(BizErrorCodes.POST_NOT_FOUND);
        }
        if (userId.equals(post.getUserId())) {
            throw BizException.of(BizErrorCodes.POST_CANNOT_APPLY, "不可报名自己发布的岗位");
        }
        if (BizConstants.POST_STATUS_FULL.equals(post.getPostStatus())
                || BizConstants.POST_STATUS_STOP.equals(post.getPostStatus())
                || BizConstants.POST_STATUS_CANCEL.equals(post.getPostStatus())
                || BizConstants.POST_STATUS_FAILURE.equals(post.getPostStatus())
                || BizConstants.POST_STATUS_VERIFY.equals(post.getPostStatus())) {
            throw BizException.of(BizErrorCodes.POST_CANNOT_APPLY);
        }
        // 仅拦截未取消的活跃单，取消后允许再报
        long active = this.count(new QueryWrapper<JobOrder>()
                .eq("user_id", userId)
                .eq("post_id", postId)
                .ne("order_status", BizConstants.ORDER_STATUS_CANCEL));
        if (active > 0) {
            throw BizException.of(BizErrorCodes.APPLY_DUPLICATE);
        }
        JobOrder order = new JobOrder();
        order.setUserId(userId);
        order.setPostUserId(post.getUserId());
        order.setPostId(postId);
        order.setStartTime(post.getStartTime());
        order.setEndTime(post.getEndTime());
        order.setCity(post.getCity());
        order.setCityCode(post.getCityCode());
        order.setPCity(post.getPCity());
        order.setPCityCode(post.getPCityCode());
        order.setOrderStatus(BizConstants.ORDER_STATUS_WAIT_ENSURE);
        if (oConvertUtils.isNotEmpty(post.getSalary())) {
            order.setUnitPrice(new BigDecimal(post.getSalary()));
        }
        order.setEnsureTime(DateUtil.offsetMinute(new Date(), 120));
        this.save(order);
        orderLogService.addOrderLog(order.getOrderStatus(), order.getId(), null, null);
        noticeService.addOrderNotice(BizConstants.ROLE_CODE_MEMBER, order.getUserId(),
                msgTemplateService.renderTitle(MsgTemplateCodes.SITE_NEW_ORDER_MEMBER, null, "生成订单"),
                msgTemplateService.renderContent(MsgTemplateCodes.SITE_NEW_ORDER_MEMBER, null, "您已确认生成，请及时关注订单进展~~"),
                null, order.getId(), order.getPostId());
        noticeService.addOrderNotice(BizConstants.ROLE_CODE_COMPANY, order.getPostUserId(),
                msgTemplateService.renderTitle(MsgTemplateCodes.SITE_NEW_ORDER_COMPANY, null, "新申请单"),
                msgTemplateService.renderContent(MsgTemplateCodes.SITE_NEW_ORDER_COMPANY, null, "已有联系过您的工人提交了合作订单，请及时确认~~"),
                null, order.getId(), order.getPostId());
        try {
            String token = WX_TemplateMsgUtil.getAccessToken(WxUtil.getAppId(), WxUtil.getAppSecret());
            String page = "pages/index/index";
            LoginUser user = sysBaseAPI.getUserById(post.getUserId());
            WxMsgSendUtils.sendMsgForNewOrderNotice(post.getTitle(), "工人提交用工订单", "请及时完成确认或者取消操作", user.getThirdId(), token, page);
        } catch (Exception e) {
            log.warn("新订单订阅消息推送失败 orderId={}", order.getId(), e);
        }
        return order;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateOrderStatus(String id, String orderStatus, String imgs) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (loginUser == null) {
            throw BizException.of(BizErrorCodes.NOT_LOGIN);
        }
        // 打卡/结算/评价/完结必须走专用接口，禁止通用改态绕过
        if (BizConstants.ORDER_STATUS_WORKING.equals(orderStatus)
                || BizConstants.ORDER_STATUS_WAIT_PAY.equals(orderStatus)
                || BizConstants.ORDER_STATUS_WAIT_COMMENT.equals(orderStatus)
                || BizConstants.ORDER_STATUS_FINISH.equals(orderStatus)) {
            throw BizException.of(BizErrorCodes.ORDER_STATUS_INVALID, "该状态请使用打卡/结算/评价专用接口");
        }
        JobOrder order = this.getById(id);
        if (order == null) {
            throw BizException.of(BizErrorCodes.ORDER_NOT_FOUND);
        }
        if (OrderStatusMachine.isTerminal(order.getOrderStatus())) {
            throw BizException.of(BizErrorCodes.ORDER_STATUS_INVALID, "订单已结束，无法变更状态");
        }
        String fromStatus = order.getOrderStatus();
        OrderStatusMachine.assertTransition(fromStatus, orderStatus);
        assertOperatorPermission(loginUser.getId(), order, orderStatus);

        if (BizConstants.ORDER_STATUS_WAIT_START.equals(orderStatus)) {
            confirmOrderWithQuota(order);
        }

        order.setOrderStatus(orderStatus);
        this.updateById(order);
        orderLogService.addOrderLog(orderStatus, id, null, imgs);

        if (BizConstants.ORDER_STATUS_WAIT_START.equals(orderStatus)) {
            JobPost post = postMapper.selectById(order.getPostId());
            noticeService.addOrderNotice(BizConstants.ROLE_CODE_MEMBER, order.getUserId(), "确认接单", "老板已确认接单，请及时关注订单进展~", null, order.getId(), order.getPostId());
            noticeService.addOrderNotice(BizConstants.ROLE_CODE_COMPANY, order.getPostUserId(), "确认接单成功", "您已确认工人合作订单~~", null, order.getId(), order.getPostId());
            try {
                String token = WX_TemplateMsgUtil.getAccessToken(WxUtil.getAppId(), WxUtil.getAppSecret());
                String page = "pages/index/index";
                LoginUser worker = sysBaseAPI.getUserById(order.getUserId());
                WxMsgSendUtils.sendMsgForApplyResult(post.getTitle(), "已录用",
                        DateUtils.formatDate(post.getStartTime(), "yyyy-MM-dd HH:mm:ss"),
                        post.getAddressName(), "请准时到达工作地打卡开工！", worker.getThirdId(), token, page);
            } catch (Exception e) {
                log.warn("录用通知推送失败 orderId={}", order.getId(), e);
            }
        } else if (BizConstants.ORDER_STATUS_CANCEL.equals(orderStatus)) {
            onOrderCancelled(order, fromStatus, loginUser.getId(), false);
        }
        return true;
    }

    /**
     * 取消后：退积分、释放招满名额、发通知
     */
    private void onOrderCancelled(JobOrder order, String fromStatus, String operatorId, boolean autoTimeout) {
        integralLogService.refundApplyIntegral(order.getUserId(), order.getId());
        releaseQuotaIfNeeded(order.getPostId());
        boolean cancelByBoss = operatorId != null && operatorId.equals(order.getPostUserId());
        String memberTitle;
        String memberContent;
        String companyContent;
        if (autoTimeout && BizConstants.ORDER_STATUS_WAIT_START.equals(fromStatus)) {
            memberTitle = "订单已逾期取消";
            memberContent = "用工结束时间已过仍未开工，订单已自动取消，报名积分已退回";
            companyContent = "待开工订单已过结束时间，系统已自动取消~~";
        } else if (autoTimeout) {
            memberTitle = "订单已超时取消";
            memberContent = "老板超时未确认，订单已自动取消，报名积分已退回";
            companyContent = "待确认订单超时已自动取消~~";
        } else {
            memberTitle = cancelByBoss ? "老板已取消订单" : "您已取消订单";
            memberContent = cancelByBoss ? "您提交的订单申请未通过，如有疑问请及时电话联系老板~" : "您已取消该用工订单";
            companyContent = "用工订单已取消~~";
        }
        noticeService.addOrderNotice(BizConstants.ROLE_CODE_MEMBER, order.getUserId(), memberTitle, memberContent, null, order.getId(), order.getPostId());
        noticeService.addOrderNotice(BizConstants.ROLE_CODE_COMPANY, order.getPostUserId(), "订单已取消",
                companyContent, null, order.getId(), order.getPostId());
        if (!autoTimeout && cancelByBoss && BizConstants.ORDER_STATUS_WAIT_ENSURE.equals(fromStatus)) {
            try {
                String token = WX_TemplateMsgUtil.getAccessToken(WxUtil.getAppId(), WxUtil.getAppSecret());
                String page = "pages/index/index";
                LoginUser worker = sysBaseAPI.getUserById(order.getUserId());
                JobPost post = postMapper.selectById(order.getPostId());
                WxMsgSendUtils.sendMsgForApplyResult(post.getTitle(), "未录用",
                        DateUtils.formatDate(post.getStartTime(), "yyyy-MM-dd HH:mm:ss"),
                        post.getAddressName(), "您暂不满足当前岗位要求！", worker.getThirdId(), token, page);
            } catch (Exception e) {
                log.warn("未录用通知推送失败 orderId={}", order.getId(), e);
            }
        }
    }

    /**
     * 校验操作人是否有权将订单变更为目标状态
     */
    private void assertOperatorPermission(String operatorId, JobOrder order, String toStatus) {
        boolean isWorker = operatorId.equals(order.getUserId());
        boolean isBoss = operatorId.equals(order.getPostUserId());
        if (!isWorker && !isBoss) {
            throw BizException.of(BizErrorCodes.ORDER_PERMISSION);
        }
        if (BizConstants.ORDER_STATUS_WAIT_START.equals(toStatus) && !isBoss) {
            throw BizException.of(BizErrorCodes.ORDER_PERMISSION, "仅老板可确认接单");
        }
        if (BizConstants.ORDER_STATUS_CANCEL.equals(toStatus)) {
            if (!BizConstants.ORDER_STATUS_WAIT_ENSURE.equals(order.getOrderStatus())
                    && !BizConstants.ORDER_STATUS_WAIT_START.equals(order.getOrderStatus())) {
                throw BizException.of(BizErrorCodes.ORDER_STATUS_INVALID, "当前状态不可取消");
            }
        }
    }

    /**
     * 确认接单并校验招聘名额（行锁防并发超招）
     */
    private void confirmOrderWithQuota(JobOrder order) {
        JobPost post = postMapper.selectOne(new QueryWrapper<JobPost>()
                .eq("id", order.getPostId())
                .last("FOR UPDATE"));
        if (post == null) {
            throw BizException.of(BizErrorCodes.POST_NOT_FOUND);
        }
        if (BizConstants.POST_STATUS_FULL.equals(post.getPostStatus())
                || BizConstants.POST_STATUS_STOP.equals(post.getPostStatus())
                || BizConstants.POST_STATUS_CANCEL.equals(post.getPostStatus())) {
            throw BizException.of(BizErrorCodes.POST_CANNOT_APPLY, "岗位已停止招聘");
        }
        long activeCount = countActiveRecruitOrders(order.getPostId(), null);
        Integer recruitsNumber = post.getRecruitsNumber();
        if (recruitsNumber == null || recruitsNumber <= 0) {
            throw new JeecgBootException("岗位招聘人数配置异常");
        }
        if (activeCount >= recruitsNumber) {
            throw new JeecgBootException("已超出招聘人数");
        }
        if (activeCount + 1 >= recruitsNumber) {
            post.setPostStatus(BizConstants.POST_STATUS_FULL);
            postMapper.updateById(post);
        }
    }

    /**
     * 取消后若未满员则从「已招满」恢复为「招工中」
     */
    private void releaseQuotaIfNeeded(String postId) {
        JobPost post = postMapper.selectOne(new QueryWrapper<JobPost>()
                .eq("id", postId)
                .last("FOR UPDATE"));
        if (post == null || !BizConstants.POST_STATUS_FULL.equals(post.getPostStatus())) {
            return;
        }
        Integer recruitsNumber = post.getRecruitsNumber();
        if (recruitsNumber == null || recruitsNumber <= 0) {
            return;
        }
        long activeCount = countActiveRecruitOrders(postId, null);
        if (activeCount < recruitsNumber) {
            post.setPostStatus(BizConstants.POST_STATUS_RUNNING);
            postMapper.updateById(post);
        }
    }

    /**
     * 统计占用名额的活跃订单（待开工~待评价，不含待确认/已取消/已完成按业务可选）
     * 与确认时口径一致：状态大于待确认且小于已取消
     */
    private long countActiveRecruitOrders(String postId, String excludeOrderId) {
        QueryWrapper<JobOrder> qw = new QueryWrapper<JobOrder>()
                .eq("post_id", postId)
                .gt("order_status", BizConstants.ORDER_STATUS_WAIT_ENSURE)
                .lt("order_status", BizConstants.ORDER_STATUS_CANCEL);
        if (oConvertUtils.isNotEmpty(excludeOrderId)) {
            qw.ne("id", excludeOrderId);
        }
        return this.count(qw);
    }

    @Override
    public BigDecimal resolvePaySalaryAmount(String orderId, BigDecimal clientAmount) {
        JobOrder order = this.getById(orderId);
        if (order == null) {
            throw new JeecgBootException("订单不存在");
        }
        if (!BizConstants.ORDER_STATUS_WAIT_PAY.equals(order.getOrderStatus())) {
            throw new JeecgBootException("订单状态不是待结算");
        }
        // 无 duration 时按打卡时间回写工时，便于核算
        if ((order.getDuration() == null || order.getDuration() <= 0)
                && order.getStartTime() != null && order.getEndTime() != null) {
            int hours = SalaryCalcHelper.calcDurationHours(order.getStartTime(), order.getEndTime());
            if (hours > 0) {
                order.setDuration(hours);
                this.updateById(order);
            }
        }
        return SalaryCalcHelper.resolvePayAmount(order, clientAmount);
    }

    @Override
    public void updatePayMoney(String orderId,String orderSn, String money, String payType) {
        JobOrder order=new JobOrder();
        order.setId(orderId);
        order.setOrderSn(orderSn);
        order.setPayableAmount(new BigDecimal(money));
        order.setAmount(new BigDecimal(money));
        order.setPayType(payType);
        this.updateById(order);
    }

    @Transactional
    @Override
    public void paySalarySuccess(String orderSn) {
        paySalarySuccess(orderSn, null);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean paySalarySuccess(String orderSn, BigDecimal paidAmount) {
        if (oConvertUtils.isEmpty(orderSn)) {
            log.error("结算回调缺少订单号");
            return false;
        }
        // 行锁，避免并发回调双入账
        JobOrder order = this.getOne(new QueryWrapper<JobOrder>()
                .eq("order_sn", orderSn)
                .last("FOR UPDATE"));
        if (order == null) {
            log.error("结算回调订单不存在 orderSn={}", orderSn);
            return false;
        }
        if ("1".equals(order.getPayStatus())
                || BizConstants.ORDER_STATUS_WAIT_COMMENT.equals(order.getOrderStatus())
                || BizConstants.ORDER_STATUS_FINISH.equals(order.getOrderStatus())) {
            log.warn("订单已结算，忽略重复回调 orderSn={}, status={}", orderSn, order.getOrderStatus());
            return true;
        }
        if (!BizConstants.ORDER_STATUS_WAIT_PAY.equals(order.getOrderStatus())) {
            log.error("订单状态非待结算，拒绝入账 orderSn={}, status={}", orderSn, order.getOrderStatus());
            return false;
        }
        if (order.getAmount() == null || order.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("结算金额异常 orderSn={}, amount={}", orderSn, order.getAmount());
            return false;
        }
        if (paidAmount != null && order.getAmount().compareTo(paidAmount) != 0) {
            log.error("结算金额与渠道实付不一致 orderSn={}, orderAmount={}, paidAmount={}",
                    orderSn, order.getAmount(), paidAmount);
            return false;
        }
        OrderStatusMachine.assertTransition(order.getOrderStatus(), BizConstants.ORDER_STATUS_WAIT_COMMENT);
        // 条件更新：仅待结算且未支付成功时可抢到一次
        UpdateWrapper<JobOrder> uw = new UpdateWrapper<>();
        uw.eq("id", order.getId())
                .eq("order_status", BizConstants.ORDER_STATUS_WAIT_PAY)
                .and(w -> w.isNull("pay_status").or().ne("pay_status", "1"))
                .set("order_status", BizConstants.ORDER_STATUS_WAIT_COMMENT)
                .set("pay_status", "1");
        boolean claimed = this.update(uw);
        if (!claimed) {
            log.warn("结算条件更新未抢到，视为幂等成功 orderSn={}", orderSn);
            return true;
        }
        orderLogService.addOrderLog(BizConstants.ORDER_STATUS_WAIT_COMMENT, order.getId(), null, null);
        accountService.addMemberBalance(order.getAmount(), BizConstants.TRADE_TYPE_SALARY_IN, order.getUserId(), "收到工资：" + order.getAmount() + "元");
        accountRecordsService.addAccountRecords(order.getPostUserId(), order.getAmount(), BizConstants.TRADE_TYPE_SALARY_OUT, "", "支付工资：" + order.getAmount() + "元");
        Map<String, String> payVars = new HashMap<>(2);
        payVars.put("amount", order.getAmount().toPlainString());
        noticeService.addOrderNotice(BizConstants.ROLE_CODE_MEMBER, order.getUserId(),
                msgTemplateService.renderTitle(MsgTemplateCodes.SITE_ORDER_PAID_MEMBER, payVars, "订单已结算"),
                msgTemplateService.renderContent(MsgTemplateCodes.SITE_ORDER_PAID_MEMBER, payVars,
                        "您的工作订单已完成线上结算，结算金额：" + order.getAmount() + "元"),
                null, order.getId(), order.getPostId());
        noticeService.addOrderNotice(BizConstants.ROLE_CODE_COMPANY, order.getPostUserId(),
                msgTemplateService.renderTitle(MsgTemplateCodes.SITE_ORDER_PAID_COMPANY, payVars, "工资结算成功"),
                msgTemplateService.renderContent(MsgTemplateCodes.SITE_ORDER_PAID_COMPANY, payVars,
                        "工人结算支付成功，结算金额：" + order.getAmount() + "元"),
                null, order.getId(), order.getPostId());
        noticeService.addBalanceNotice(order.getUserId(), "工资已到账", "工资已结算，到账金额：" + order.getAmount() + "元", order.getId());
        return true;
    }

    @Override
    public IPage<Map<String, Object>> getOrderList(Page<JobOrder> page, JobOrder params) {
        return baseMapper.getOrderList(page,params);
    }

    @Override
    public IPage<Map<String, Object>> getOrderListForAdmin(Page<JobOrder> page, JobOrder params) {
        return baseMapper.getOrderListForAdmin(page,params);
    }

    @Override
    public IPage<Map<String, Object>> getPostUserList(Page<JobOrder> page, JobOrder params) {
        if (params == null || oConvertUtils.isEmpty(params.getPostUserId())) {
            throw BizException.of(BizErrorCodes.NO_PERMISSION, "仅可查询本人岗位下的工人");
        }
        return baseMapper.getPostUserList(page, params);
    }

    @Override
    public Map<String, Object> getOrderStatistics(String postId) {
        Map<String,Object> map=new HashMap<>();
        //查询待接单数量
        long waitEnsure=this.count(new QueryWrapper<>(new JobOrder().setPostId(postId).setOrderStatus(BizConstants.ORDER_STATUS_WAIT_ENSURE)));
        //查询待开工数量
        long waitStart=this.count(new QueryWrapper<>(new JobOrder().setPostId(postId).setOrderStatus(BizConstants.ORDER_STATUS_WAIT_START)));
        //工作中
        long working=this.count(new QueryWrapper<>(new JobOrder().setPostId(postId).setOrderStatus(BizConstants.ORDER_STATUS_WORKING)));
        //待结算
        long waitPay=this.count(new QueryWrapper<>(new JobOrder().setPostId(postId).setOrderStatus(BizConstants.ORDER_STATUS_WAIT_PAY)));
        //待评价,特殊处理
        QueryWrapper queryWrapper=new QueryWrapper<>(new JobOrder().setPostId(postId).setOrderStatus(BizConstants.ORDER_STATUS_WAIT_COMMENT).setCompanyEvaluate(0));
        long waitComment=this.count(queryWrapper);
        map.put("waitEnsure",waitEnsure);
        map.put("waitStart",waitStart);
        map.put("working",working);
        map.put("waitPay",waitPay);
        map.put("waitComment",waitComment);
        return map;
    }

    /**
     * 订单定时清理：待确认超时取消 / 待开工过期取消 / 待评价超时完结。
     * 注意：外层不加大事务，单条独立提交，避免历史脏数据一条失败拖垮整批。
     */
    @Override
    public void autoFinishOrder() {
        int c1 = cancelWaitEnsureTimeout();
        int c2 = autoCancelNoStartAfterEnd();
        int c3 = autoFinishEvaluateTimeout();
        log.info("OrderJob 完成：待确认超时取消={}, 逾期待开工取消={}, 评价超时完结={}", c1, c2, c3);
    }

    /** 待确认超时自动取消，返回成功条数 */
    private int cancelWaitEnsureTimeout() {
        QueryWrapper<JobOrder> queryWrapper = new QueryWrapper<>(new JobOrder().setOrderStatus(BizConstants.ORDER_STATUS_WAIT_ENSURE));
        queryWrapper.le("ensure_time", new Date()).last("LIMIT 500");
        List<JobOrder> list = this.list(queryWrapper);
        if (list == null || list.isEmpty()) {
            return 0;
        }
        int ok = 0;
        for (JobOrder item : list) {
            try {
                Boolean done = transactionTemplate.execute(status -> {
                    UpdateWrapper<JobOrder> uw = new UpdateWrapper<>();
                    uw.eq("id", item.getId())
                            .eq("order_status", BizConstants.ORDER_STATUS_WAIT_ENSURE)
                            .set("order_status", BizConstants.ORDER_STATUS_CANCEL);
                    if (!this.update(uw)) {
                        return false;
                    }
                    item.setOrderStatus(BizConstants.ORDER_STATUS_CANCEL);
                    orderLogService.addOrderLog(BizConstants.ORDER_STATUS_CANCEL, item.getId(), "超时自动取消", null);
                    onOrderCancelled(item, BizConstants.ORDER_STATUS_WAIT_ENSURE, null, true);
                    return true;
                });
                if (Boolean.TRUE.equals(done)) {
                    ok++;
                }
            } catch (Exception e) {
                log.warn("超时取消失败 orderId={}, status={}", item.getId(), item.getOrderStatus(), e);
            }
        }
        return ok;
    }

    /**
     * 待开工且 end_time 已过：自动取消（可配置开关，默认开启）。
     * end_time 在现网多为 varchar，用 NOW() 比较避免 JDBC Date 与字符串比较踩坑。
     */
    private int autoCancelNoStartAfterEnd() {
        if (!isNoStartAfterEndEnabled()) {
            log.info("逾期待开工自动取消已关闭（order_no_start_after_end=0）");
            return 0;
        }
        // 兼容 datetime / varchar(yyyy-MM-dd HH:mm:ss)：由 MySQL NOW() 比较
        List<JobOrder> list = this.list(new QueryWrapper<JobOrder>()
                .eq("order_status", BizConstants.ORDER_STATUS_WAIT_START)
                .isNotNull("end_time")
                .ne("end_time", "")
                .apply("end_time < NOW()")
                .last("LIMIT 500"));
        if (list == null || list.isEmpty()) {
            return 0;
        }
        int ok = 0;
        for (JobOrder item : list) {
            try {
                Boolean done = transactionTemplate.execute(status -> {
                    UpdateWrapper<JobOrder> uw = new UpdateWrapper<>();
                    uw.eq("id", item.getId())
                            .eq("order_status", BizConstants.ORDER_STATUS_WAIT_START)
                            .set("order_status", BizConstants.ORDER_STATUS_CANCEL);
                    if (!this.update(uw)) {
                        return false;
                    }
                    item.setOrderStatus(BizConstants.ORDER_STATUS_CANCEL);
                    orderLogService.addOrderLog(BizConstants.ORDER_STATUS_CANCEL, item.getId(),
                            "待开工已过结束时间，系统自动取消", null);
                    onOrderCancelled(item, BizConstants.ORDER_STATUS_WAIT_START, null, true);
                    return true;
                });
                if (Boolean.TRUE.equals(done)) {
                    ok++;
                }
            } catch (Exception e) {
                log.warn("逾期待开工取消失败 orderId={}", item.getId(), e);
            }
        }
        return ok;
    }

    private boolean isNoStartAfterEndEnabled() {
        try {
            org.jeecg.modules.job.base.entity.BaseConfig cfg =
                    configService.getConfigByCode(BizConstants.ORDER_NO_START_AFTER_END);
            if (cfg != null && oConvertUtils.isNotEmpty(cfg.getConfigValue())) {
                return !"0".equals(cfg.getConfigValue().trim());
            }
        } catch (Exception e) {
            log.warn("读取逾期待开工取消配置失败，默认开启", e);
        }
        return true;
    }

    /**
     * 待评价超过配置小时数（默认 72）自动完结，避免单方评价长期卡单
     */
    private int autoFinishEvaluateTimeout() {
        int hours = 72;
        try {
            org.jeecg.modules.job.base.entity.BaseConfig cfg = configService.getConfigByCode(BizConstants.EVALUATE_TIMEOUT_HOURS);
            if (cfg != null && oConvertUtils.isNotEmpty(cfg.getConfigValue())) {
                hours = Integer.parseInt(cfg.getConfigValue().trim());
                if (hours <= 0) {
                    hours = 72;
                }
            }
        } catch (Exception e) {
            log.warn("读取评价超时配置失败，使用默认 {} 小时", hours, e);
        }
        Date deadline = DateUtil.offsetHour(new Date(), -hours);
        List<JobOrder> list = this.list(new QueryWrapper<JobOrder>()
                .eq("order_status", BizConstants.ORDER_STATUS_WAIT_COMMENT)
                .le("update_time", deadline)
                .last("LIMIT 500"));
        if (list == null || list.isEmpty()) {
            return 0;
        }
        int ok = 0;
        final int hoursFinal = hours;
        for (JobOrder item : list) {
            try {
                Boolean done = transactionTemplate.execute(status -> {
                    UpdateWrapper<JobOrder> uw = new UpdateWrapper<>();
                    uw.eq("id", item.getId())
                            .eq("order_status", BizConstants.ORDER_STATUS_WAIT_COMMENT)
                            .set("order_status", BizConstants.ORDER_STATUS_FINISH);
                    if (!this.update(uw)) {
                        return false;
                    }
                    orderLogService.addOrderLog(BizConstants.ORDER_STATUS_FINISH, item.getId(),
                            "待评价超时自动完结（" + hoursFinal + "小时）", null);
                    noticeService.addOrderNotice(BizConstants.ROLE_CODE_MEMBER, item.getUserId(),
                            "订单已完结", "评价期已结束，订单已自动完结", null, item.getId(), item.getPostId());
                    noticeService.addOrderNotice(BizConstants.ROLE_CODE_COMPANY, item.getPostUserId(),
                            "订单已完结", "评价期已结束，订单已自动完结", null, item.getId(), item.getPostId());
                    return true;
                });
                if (Boolean.TRUE.equals(done)) {
                    ok++;
                }
            } catch (Exception e) {
                log.warn("评价超时完结失败 orderId={}", item.getId(), e);
            }
        }
        return ok;
    }
}
