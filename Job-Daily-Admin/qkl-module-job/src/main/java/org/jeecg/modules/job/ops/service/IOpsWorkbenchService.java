package org.jeecg.modules.job.ops.service;

import org.jeecg.modules.job.job.entity.JobCompany;
import org.jeecg.modules.job.job.entity.JobOrder;
import org.jeecg.modules.job.job.entity.JobPost;
import org.jeecg.modules.job.ops.vo.OpsWorkbenchSummaryVO;
import org.jeecg.modules.job.ums.entity.UmsRealnameAuth;
import org.jeecg.modules.job.ums.entity.UmsWithdraw;

import java.util.List;

/**
 * 运营工作台服务
 */
public interface IOpsWorkbenchService {

    /**
     * 汇总待办与近7日核心指标
     */
    OpsWorkbenchSummaryVO getSummary();

    /**
     * 待审岗位（最多 limit 条）
     */
    List<JobPost> listPendingPosts(int limit);

    /**
     * 待审实名认证
     */
    List<UmsRealnameAuth> listPendingRealnames(int limit);

    /**
     * 待审企业认证
     */
    List<JobCompany> listPendingCompanies(int limit);

    /**
     * 异常订单：超时待确认 + 待结算
     */
    List<JobOrder> listAbnormalOrders(int limit);

    /**
     * 提现队列：待审 + 转账未终态
     */
    List<UmsWithdraw> listWithdrawQueue(int limit);
}
