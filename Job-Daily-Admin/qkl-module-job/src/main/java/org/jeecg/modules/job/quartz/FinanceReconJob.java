package org.jeecg.modules.job.quartz;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.job.finance.service.FinanceReconService;
import org.jeecg.modules.job.quartz.support.MonitoredJobSupport;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.annotation.Resource;

/**
 * 资金日终对账定时任务（建议 cron：0 30 1 * * ?）
 */
@Slf4j
public class FinanceReconJob implements Job {

    @Resource
    private FinanceReconService financeReconService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        MonitoredJobSupport.run(context, "FinanceReconJob", () -> financeReconService.dailyRecon());
    }
}
