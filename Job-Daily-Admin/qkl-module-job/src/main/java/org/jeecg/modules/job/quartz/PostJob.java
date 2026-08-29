package org.jeecg.modules.job.quartz;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.job.job.service.IJobPostService;
import org.jeecg.modules.job.quartz.support.MonitoredJobSupport;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.annotation.Resource;

/**
 * 招工定时任务（超时自动下架）
 */
@Slf4j
public class PostJob implements Job {

	@Resource
	private IJobPostService jobPostService;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		MonitoredJobSupport.run(context, "PostJob", () -> jobPostService.autoOfflinePost());
	}
}
