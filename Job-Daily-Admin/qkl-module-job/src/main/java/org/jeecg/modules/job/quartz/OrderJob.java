package org.jeecg.modules.job.quartz;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.job.job.service.IJobOrderService;
import org.jeecg.modules.job.job.service.IJobPostService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.annotation.Resource;

/**
 *  订单定时任务
 * 
 */
@Slf4j
public class OrderJob implements Job {

	@Resource
	private IJobOrderService orderService;

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		log.info(" Job Execution key："+jobExecutionContext.getJobDetail().getKey());
		log.info(String.format(" 订单定时任务 OrderJob !  时间:" + DateUtils.getTimestamp()));
		//超时自动下架
		orderService.autoFinishOrder();
	}
}
