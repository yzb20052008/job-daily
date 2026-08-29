package org.jeecg.modules.job.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.job.constant.BizConstants;
import org.jeecg.modules.job.job.entity.JobCompany;
import org.jeecg.modules.job.job.entity.JobOrder;
import org.jeecg.modules.job.job.entity.JobPost;
import org.jeecg.modules.job.job.service.IJobCompanyService;
import org.jeecg.modules.job.job.service.IJobOrderService;
import org.jeecg.modules.job.job.service.IJobPostService;
import org.jeecg.modules.job.ops.service.IOpsWorkbenchService;
import org.jeecg.modules.job.ops.vo.OpsWorkbenchSummaryVO;
import org.jeecg.modules.job.ums.entity.UmsRealnameAuth;
import org.jeecg.modules.job.ums.entity.UmsWithdraw;
import org.jeecg.modules.job.ums.service.IUmsRealnameAuthService;
import org.jeecg.modules.job.ums.service.IUmsWithdrawService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 运营工作台服务实现
 */
@Slf4j
@Service
public class OpsWorkbenchServiceImpl implements IOpsWorkbenchService {

    private static final List<String> TRANSFER_OPEN = Arrays.asList(
            BizConstants.TRANSFER_STATUS_ACCEPTED,
            BizConstants.TRANSFER_STATUS_PROCESSING,
            BizConstants.TRANSFER_STATUS_WAIT_USER_CONFIRM,
            BizConstants.TRANSFER_STATUS_TRANSFERING,
            BizConstants.TRANSFER_STATUS_CANCELING
    );

    @Resource
    private IJobPostService postService;
    @Resource
    private IJobOrderService orderService;
    @Resource
    private IJobCompanyService companyService;
    @Resource
    private IUmsRealnameAuthService realnameAuthService;
    @Resource
    private IUmsWithdrawService withdrawService;

    @Override
    public OpsWorkbenchSummaryVO getSummary() {
        OpsWorkbenchSummaryVO vo = new OpsWorkbenchSummaryVO();
        vo.setPendingPostCount(postService.count(new QueryWrapper<JobPost>()
                .eq("post_status", BizConstants.POST_STATUS_VERIFY)));
        vo.setPendingRealnameCount(realnameAuthService.count(new QueryWrapper<UmsRealnameAuth>()
                .eq("auth_status", BizConstants.AUTH_STATUS_DEFAULT)));
        vo.setPendingCompanyCount(companyService.count(new QueryWrapper<JobCompany>()
                .eq("auth_status", BizConstants.AUTH_STATUS_DEFAULT)));
        vo.setPendingWithdrawCount(withdrawService.count(new QueryWrapper<UmsWithdraw>()
                .eq("withdraw_status", BizConstants.WITHDRAW_STATUS_DEFAULT)));
        vo.setAbnormalWithdrawCount(countAbnormalWithdraw());
        Date now = new Date();
        long timeoutEnsure = orderService.count(new QueryWrapper<JobOrder>()
                .eq("order_status", BizConstants.ORDER_STATUS_WAIT_ENSURE)
                .lt("ensure_time", now));
        long timeoutNoStart = orderService.count(new QueryWrapper<JobOrder>()
                .eq("order_status", BizConstants.ORDER_STATUS_WAIT_START)
                .isNotNull("end_time")
                .lt("end_time", now));
        vo.setTimeoutOrderCount(timeoutEnsure + timeoutNoStart);
        vo.setWaitPayOrderCount(orderService.count(new QueryWrapper<JobOrder>()
                .eq("order_status", BizConstants.ORDER_STATUS_WAIT_PAY)));

        Date start7 = daysAgo(7);
        long orderTotal = orderService.count(new QueryWrapper<JobOrder>().ge("create_time", start7));
        long orderFinish = orderService.count(new QueryWrapper<JobOrder>()
                .ge("create_time", start7)
                .eq("order_status", BizConstants.ORDER_STATUS_FINISH));
        long orderPaid = orderService.count(new QueryWrapper<JobOrder>()
                .ge("create_time", start7)
                .eq("pay_status", "1"));
        long withdrawTotal = withdrawService.count(new QueryWrapper<UmsWithdraw>().ge("create_time", start7));
        long withdrawSuccess = withdrawService.count(new QueryWrapper<UmsWithdraw>()
                .ge("create_time", start7)
                .eq("withdraw_status", BizConstants.WITHDRAW_STATUS_SUCCESS)
                .eq("transfer_status", BizConstants.TRANSFER_STATUS_SUCCESS));

        vo.setOrderTotal7d(orderTotal);
        vo.setOrderFinish7d(orderFinish);
        vo.setOrderPaid7d(orderPaid);
        vo.setWithdrawTotal7d(withdrawTotal);
        vo.setWithdrawSuccess7d(withdrawSuccess);
        vo.setFinishRate7d(rate(orderFinish, orderTotal));
        vo.setPaySuccessRate7d(rate(orderPaid, orderTotal));
        vo.setWithdrawSuccessRate7d(rate(withdrawSuccess, withdrawTotal));
        return vo;
    }

    @Override
    public List<JobPost> listPendingPosts(int limit) {
        return postService.list(new QueryWrapper<JobPost>()
                .eq("post_status", BizConstants.POST_STATUS_VERIFY)
                .orderByDesc("create_time")
                .last("LIMIT " + safeLimit(limit)));
    }

    @Override
    public List<UmsRealnameAuth> listPendingRealnames(int limit) {
        return realnameAuthService.list(new QueryWrapper<UmsRealnameAuth>()
                .eq("auth_status", BizConstants.AUTH_STATUS_DEFAULT)
                .orderByDesc("create_time")
                .last("LIMIT " + safeLimit(limit)));
    }

    @Override
    public List<JobCompany> listPendingCompanies(int limit) {
        return companyService.list(new QueryWrapper<JobCompany>()
                .eq("auth_status", BizConstants.AUTH_STATUS_DEFAULT)
                .orderByDesc("create_time")
                .last("LIMIT " + safeLimit(limit)));
    }

    @Override
    public List<JobOrder> listAbnormalOrders(int limit) {
        Date now = new Date();
        // 超时待确认 / 逾期待开工 / 待结算
        return orderService.list(new QueryWrapper<JobOrder>()
                .and(w -> w.nested(n -> n.eq("order_status", BizConstants.ORDER_STATUS_WAIT_ENSURE)
                                .lt("ensure_time", now))
                        .or(n -> n.eq("order_status", BizConstants.ORDER_STATUS_WAIT_START)
                                .isNotNull("end_time")
                                .lt("end_time", now))
                        .or()
                        .eq("order_status", BizConstants.ORDER_STATUS_WAIT_PAY))
                .orderByAsc("order_status")
                .orderByAsc("ensure_time")
                .last("LIMIT " + safeLimit(limit)));
    }

    @Override
    public List<UmsWithdraw> listWithdrawQueue(int limit) {
        return withdrawService.list(new QueryWrapper<UmsWithdraw>()
                .and(w -> w.eq("withdraw_status", BizConstants.WITHDRAW_STATUS_DEFAULT)
                        .or(o -> o.eq("withdraw_status", BizConstants.WITHDRAW_STATUS_SUCCESS)
                                .and(t -> t.isNull("transfer_status")
                                        .or().eq("transfer_status", "")
                                        .or().in("transfer_status", TRANSFER_OPEN))))
                .orderByAsc("withdraw_status")
                .orderByDesc("create_time")
                .last("LIMIT " + safeLimit(limit)));
    }

    private long countAbnormalWithdraw() {
        return withdrawService.count(new QueryWrapper<UmsWithdraw>()
                .eq("withdraw_status", BizConstants.WITHDRAW_STATUS_SUCCESS)
                .and(t -> t.isNull("transfer_status")
                        .or().eq("transfer_status", "")
                        .or().in("transfer_status", TRANSFER_OPEN)));
    }

    private BigDecimal rate(long num, long den) {
        if (den <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(num)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(den), 2, RoundingMode.HALF_UP);
    }

    private Date daysAgo(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -days);
        return cal.getTime();
    }

    private int safeLimit(int limit) {
        if (limit <= 0) {
            return 20;
        }
        return Math.min(limit, 100);
    }
}
