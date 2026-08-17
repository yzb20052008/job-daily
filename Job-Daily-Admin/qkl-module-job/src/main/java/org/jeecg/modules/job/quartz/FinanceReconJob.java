package org.jeecg.modules.job.quartz;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.job.finance.service.FinanceReconService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.annotation.Resource;

/**
 * 资金日终对账定时任务（建议 cron：0 30 1 * * ? 每天 01:30）
 */
@Slf4j
public class FinanceReconJob implements Job {

    @Resource
    private FinanceReconService financeReconService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("FinanceReconJob start, key={}, time={}",
                context.getJobDetail().getKey(), DateUtils.getTimestamp());
        try {
            financeReconService.dailyRecon();
        } catch (Exception e) {
            log.error("FinanceReconJob 执行失败", e);
            throw new JobExecutionException(e);
        }
        log.info("FinanceReconJob end");
    }
}
