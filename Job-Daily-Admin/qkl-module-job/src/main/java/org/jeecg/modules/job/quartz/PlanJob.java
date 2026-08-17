package org.jeecg.modules.job.quartz;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.annotation.Resource;

/**
 *  咨询定时任务
 * 
 */
@Slf4j
public class PlanJob implements Job {

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		log.info(" Job Execution key："+jobExecutionContext.getJobDetail().getKey());
		log.info(String.format(" 咨询定时任务 PlanJob !  时间:" + DateUtils.getTimestamp()));
	}
}
