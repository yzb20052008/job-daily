package org.jeecg.modules.job.quartz.support;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.SpringContextUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 定时任务统一包装：计时 + 异常捕获 + 告警
 */
@Slf4j
public final class MonitoredJobSupport {

    private MonitoredJobSupport() {
    }

    @FunctionalInterface
    public interface JobAction {
        void run() throws Exception;
    }

    public static void run(JobExecutionContext context, String jobName, JobAction action) throws JobExecutionException {
        long start = System.currentTimeMillis();
        log.info("{} start key={} time={}", jobName, context.getJobDetail().getKey(), DateUtils.getTimestamp());
        try {
            action.run();
            log.info("{} end cost={}ms", jobName, System.currentTimeMillis() - start);
        } catch (Exception e) {
            log.error("{} failed cost={}ms", jobName, System.currentTimeMillis() - start, e);
            try {
                JobAlertService alertService = SpringContextUtils.getBean(JobAlertService.class);
                if (alertService != null) {
                    alertService.alert(jobName, e);
                }
            } catch (Exception ignore) {
                // 告警失败不影响抛出
            }
            throw new JobExecutionException(e);
        }
    }
}
