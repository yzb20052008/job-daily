package org.jeecg.modules.job.job.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import org.jeecg.modules.job.integral.service.IIntegralLogService;
import org.jeecg.modules.job.job.entity.JobOrder;
import org.jeecg.modules.job.job.entity.JobPost;
import org.jeecg.modules.job.job.mapper.JobOrderMapper;
import org.jeecg.modules.job.job.mapper.JobPostMapper;
import org.jeecg.modules.job.job.service.IJobOrderClockService;
import org.jeecg.modules.job.job.service.IJobOrderLogService;
import org.jeecg.modules.job.job.service.IJobOrderService;
import org.jeecg.modules.job.job.support.OrderStatusMachine;
import org.jeecg.modules.job.ums.service.IUmsAccountRecordsService;
import org.jeecg.modules.job.ums.service.IUmsAccountService;
import org.jeecg.modules.job.utils.WX_TemplateMsgUtil;
import org.jeecg.modules.job.utils.WxMsgSendUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public JobOrderVo getOrderDetail(String id) {
        JobOrder order=this.getById(id);
        JobOrderVo orderVo=new JobOrderVo();
        orderVo.setJobOrder(order);
        //打卡信息
        orderVo.setStartClock(clockService.getOrderClock(BizConstants.CLOCK_TYPE_ON,id));
        //下班
        orderVo.setEndClock(clockService.getOrderClock(BizConstants.CLOCK_TYPE_OFF,id));
        //岗位信息
        orderVo.setPost(postMapper.selectById(order.getPostId()));
        //发布人信息
        orderVo.setPostUser(sysBaseAPI.getUserById(order.getPostUserId()));
        //工人信息
        orderVo.setUser(sysBaseAPI.getUserById(order.getUserId()));
        return orderVo;
    }

    @Transactional
    @Override
    public boolean doApply(String userId, String postId, Integer integral) {
        this.createOrder(userId,postId);
        if (integral>0){
            //扣除积分
            LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            integralLogService.addIntegralLogForReduce(user.getId(),BizConstants.JF_CALL,null,"提交报名消费积分：");
        }
        return true;
    }

    @Transactional
    @Override
    public boolean createOrder(String userId, String postId) {
        JobPost post=postMapper.selectById(postId);
        if (post==null){
            throw new JeecgBootException("岗位不存在");
        }
        if (BizConstants.POST_STATUS_FULL.equals(post.getPostStatus())
                || BizConstants.POST_STATUS_STOP.equals(post.getPostStatus())
                || BizConstants.POST_STATUS_CANCEL.equals(post.getPostStatus())
                || BizConstants.POST_STATUS_FAILURE.equals(post.getPostStatus())
                || BizConstants.POST_STATUS_VERIFY.equals(post.getPostStatus())) {
            throw new JeecgBootException("当前岗位不可报名");
        }
        JobOrder order=this.getOne(new QueryWrapper<>(new JobOrder().setUserId(userId).setPostId(postId)));
        if (order!=null){
            throw new JeecgBootException("请勿重复提交");
        }
        order=new JobOrder();
        order.setUserId(userId);
        order.setPostUserId(post.getUserId());
        order.setPostId(postId);
        order.setStartTime(post.getStartTime());
        order.setEndTime(post.getEndTime());
        order.setCity(post.getCity());
        order.setCityCode(post.getCityCode());
        order.setPCity(post.getPCity());
        order.setPCityCode(post.getPCityCode());
        // 订单状态：0-待确认，1-待开工，2-工作中，3-待结算，4-待评价，5-已完成
        order.setOrderStatus(BizConstants.ORDER_STATUS_WAIT_ENSURE);
        if (oConvertUtils.isNotEmpty(post.getSalary())){
            order.setUnitPrice(new BigDecimal(post.getSalary()));
        }
        order.setEnsureTime(DateUtil.offsetMinute(new Date(),120));
        this.save(order);
        orderLogService.addOrderLog(order.getOrderStatus(),order.getId(),null,null);
        //工人通知
        noticeService.addOrderNotice(BizConstants.ROLE_CODE_MEMBER,order.getUserId(),"生成订单","您已确认生成，请及时关注订单进展~~",null,order.getId(),order.getPostId());
        //老板通知
        noticeService.addOrderNotice(BizConstants.ROLE_CODE_COMPANY,order.getPostUserId(),"新申请单","已有联系过您的工人提交了合作订单，请及时确认~~",null,order.getId(),order.getPostId());
        //推送给老板
        try{
            String token= WX_TemplateMsgUtil.getAccessToken(WxUtil.getAppId(),WxUtil.getAppSecret());
            String page = "pages/index/index";
            LoginUser user = sysBaseAPI.getUserById(post.getUserId());
            WxMsgSendUtils.sendMsgForNewOrderNotice(post.getTitle(),"工人提交用工订单","请及时完成确认或者取消操作",user.getThirdId(),token,page);
        }catch (Exception e){
            // 订阅消息失败不影响主流程
            log.warn("新订单订阅消息推送失败 orderId={}", order.getId(), e);
        }
        return true;
    }

    @Transactional
    @Override
    public boolean updateOrderStatus(String id, String orderStatus,String imgs) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (loginUser == null) {
            throw new JeecgBootException("请先登录");
        }
        JobOrder order = this.getById(id);
        if (order == null) {
            throw new JeecgBootException("订单不存在");
        }
        if (OrderStatusMachine.isTerminal(order.getOrderStatus())) {
            throw new JeecgBootException("订单已结束，无法变更状态");
        }
        String fromStatus = order.getOrderStatus();
        OrderStatusMachine.assertTransition(fromStatus, orderStatus);
        assertOperatorPermission(loginUser.getId(), order, orderStatus);

        // 确认接单：先锁岗位再校验人数，防止并发超招
        if (BizConstants.ORDER_STATUS_WAIT_START.equals(orderStatus)) {
            confirmOrderWithQuota(order);
        }

        order.setOrderStatus(orderStatus);
        if (BizConstants.ORDER_STATUS_WORKING.equals(orderStatus)) {
            order.setStartTime(new Date());
            noticeService.addOrderNotice(BizConstants.ROLE_CODE_MEMBER, order.getUserId(), "上班打卡成功", "上班打卡成功~", null, order.getId(), order.getPostId());
            noticeService.addOrderNotice(BizConstants.ROLE_CODE_COMPANY, order.getPostUserId(), "工人上班打卡", "已有工人完成上班打卡~~", null, order.getId(), order.getPostId());
        } else if (BizConstants.ORDER_STATUS_WAIT_PAY.equals(orderStatus)) {
            order.setEndTime(new Date());
            noticeService.addOrderNotice(BizConstants.ROLE_CODE_MEMBER, order.getUserId(), "下班打卡成功", "下班打卡成功~", null, order.getId(), order.getPostId());
            noticeService.addOrderNotice(BizConstants.ROLE_CODE_COMPANY, order.getPostUserId(), "工人下班打卡", "已有工人完成下班打卡~~", null, order.getId(), order.getPostId());
        }
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
            boolean cancelByBoss = loginUser.getId().equals(order.getPostUserId());
            String memberTitle = cancelByBoss ? "老板已取消订单" : "您已取消订单";
            String memberContent = cancelByBoss ? "您提交的订单申请未通过，如有疑问请及时电话联系老板~" : "您已取消该用工订单";
            noticeService.addOrderNotice(BizConstants.ROLE_CODE_MEMBER, order.getUserId(), memberTitle, memberContent, null, order.getId(), order.getPostId());
            noticeService.addOrderNotice(BizConstants.ROLE_CODE_COMPANY, order.getPostUserId(), "订单已取消", "用工订单已取消~~", null, order.getId(), order.getPostId());
            if (cancelByBoss && BizConstants.ORDER_STATUS_WAIT_ENSURE.equals(fromStatus)) {
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
        return true;
    }

    /**
     * 校验操作人是否有权将订单变更为目标状态
     */
    private void assertOperatorPermission(String operatorId, JobOrder order, String toStatus) {
        boolean isWorker = operatorId.equals(order.getUserId());
        boolean isBoss = operatorId.equals(order.getPostUserId());
        if (!isWorker && !isBoss) {
            throw new JeecgBootException("无权操作该订单");
        }
        if (BizConstants.ORDER_STATUS_WAIT_START.equals(toStatus) && !isBoss) {
            throw new JeecgBootException("仅老板可确认接单");
        }
        if (BizConstants.ORDER_STATUS_WORKING.equals(toStatus) && !isWorker) {
            throw new JeecgBootException("仅工人可上班打卡");
        }
        if (BizConstants.ORDER_STATUS_WAIT_PAY.equals(toStatus) && !isWorker) {
            throw new JeecgBootException("仅工人可下班打卡");
        }
        if (BizConstants.ORDER_STATUS_CANCEL.equals(toStatus)) {
            // 待确认：双方可取消；待开工：双方可取消；其他状态禁止经此接口取消
            if (!BizConstants.ORDER_STATUS_WAIT_ENSURE.equals(order.getOrderStatus())
                    && !BizConstants.ORDER_STATUS_WAIT_START.equals(order.getOrderStatus())) {
                throw new JeecgBootException("当前状态不可取消");
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
            throw new JeecgBootException("岗位不存在");
        }
        if (BizConstants.POST_STATUS_FULL.equals(post.getPostStatus())
                || BizConstants.POST_STATUS_STOP.equals(post.getPostStatus())
                || BizConstants.POST_STATUS_CANCEL.equals(post.getPostStatus())) {
            throw new JeecgBootException("岗位已停止招聘");
        }
        long activeCount = this.count(new QueryWrapper<JobOrder>()
                .eq("post_id", order.getPostId())
                .gt("order_status", BizConstants.ORDER_STATUS_WAIT_ENSURE)
                .lt("order_status", BizConstants.ORDER_STATUS_CANCEL));
        Integer recruitsNumber = post.getRecruitsNumber();
        if (recruitsNumber == null || recruitsNumber <= 0) {
            throw new JeecgBootException("岗位招聘人数配置异常");
        }
        if (activeCount >= recruitsNumber) {
            throw new JeecgBootException("已超出招聘人数");
        }
        // 本次确认后是否招满
        if (activeCount + 1 >= recruitsNumber) {
            post.setPostStatus(BizConstants.POST_STATUS_FULL);
            postMapper.updateById(post);
        }
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

    @Transactional
    @Override
    public boolean paySalarySuccess(String orderSn, BigDecimal paidAmount) {
        if (oConvertUtils.isEmpty(orderSn)) {
            log.error("结算回调缺少订单号");
            return false;
        }
        JobOrder order = this.getOne(new QueryWrapper<>(new JobOrder().setOrderSn(orderSn)));
        if (order == null) {
            log.error("结算回调订单不存在 orderSn={}", orderSn);
            return false;
        }
        // 幂等：已结算直接返回成功，避免渠道重复通知
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
        // 渠道实付与订单金额一致性校验（允许分位误差 0）
        if (paidAmount != null && order.getAmount().compareTo(paidAmount) != 0) {
            log.error("结算金额与渠道实付不一致 orderSn={}, orderAmount={}, paidAmount={}",
                    orderSn, order.getAmount(), paidAmount);
            return false;
        }
        OrderStatusMachine.assertTransition(order.getOrderStatus(), BizConstants.ORDER_STATUS_WAIT_COMMENT);
        order.setOrderStatus(BizConstants.ORDER_STATUS_WAIT_COMMENT);
        order.setPayStatus("1");
        updateById(order);
        orderLogService.addOrderLog(BizConstants.ORDER_STATUS_WAIT_COMMENT, order.getId(), null, null);
        accountService.addMemberBalance(order.getAmount(), BizConstants.TRADE_TYPE_SALARY_IN, order.getUserId(), "收到工资：" + order.getAmount() + "元");
        accountRecordsService.addAccountRecords(order.getPostUserId(), order.getAmount(), BizConstants.TRADE_TYPE_SALARY_OUT, "", "支付工资：" + order.getAmount() + "元");
        noticeService.addOrderNotice(BizConstants.ROLE_CODE_MEMBER, order.getUserId(), "订单已结算", "您的工作订单已完成线上结算，结算金额：" + order.getAmount() + "元", null, order.getId(), order.getPostId());
        noticeService.addOrderNotice(BizConstants.ROLE_CODE_COMPANY, order.getPostUserId(), "工资结算成功", "工人结算支付成功，结算金额：" + order.getAmount() + "元", null, order.getId(), order.getPostId());
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
        return baseMapper.getPostUserList(page,params);
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

    @Override
    public void autoFinishOrder() {
        // 待确认订单超时自动取消
        QueryWrapper<JobOrder> queryWrapper = new QueryWrapper<>(new JobOrder().setOrderStatus(BizConstants.ORDER_STATUS_WAIT_ENSURE));
        queryWrapper.le("ensure_time", new Date());
        List<JobOrder> list = this.list(queryWrapper);
        if (list == null || list.isEmpty()) {
            return;
        }
        list.forEach(item -> {
            item.setOrderStatus(BizConstants.ORDER_STATUS_CANCEL);
            orderLogService.addOrderLog(BizConstants.ORDER_STATUS_CANCEL, item.getId(), "超时自动取消", null);
        });
        this.updateBatchById(list);
    }
}
