package org.jeecg.modules.job.quartz;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.job.job.service.IJobPostService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.annotation.Resource;

/**
 *  招工定时任务
 * 
 */
@Slf4j
public class PostJob implements Job {

	@Resource
	private IJobPostService jobPostService;

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		log.info(" Job Execution key："+jobExecutionContext.getJobDetail().getKey());
		log.info(String.format(" 招工定时任务 PostJob !  时间:" + DateUtils.getTimestamp()));
		//超时自动下架
		jobPostService.autoOfflinePost();
	}
}
