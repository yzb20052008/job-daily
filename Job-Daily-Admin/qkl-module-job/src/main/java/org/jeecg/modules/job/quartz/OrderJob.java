package org.jeecg.modules.job.quartz;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.job.job.service.IJobOrderService;
import org.jeecg.modules.job.quartz.support.MonitoredJobSupport;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.annotation.Resource;

/**
 * 订单定时任务（超时自动完结等）
 */
@Slf4j
public class OrderJob implements Job {

	@Resource
	private IJobOrderService orderService;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// 待确认超时取消 + 待开工过期取消 + 待评价超时完结
		MonitoredJobSupport.run(context, "OrderJob", () -> orderService.autoFinishOrder());
	}
}
