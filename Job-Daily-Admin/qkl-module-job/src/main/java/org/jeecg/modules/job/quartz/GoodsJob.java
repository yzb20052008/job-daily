package org.jeecg.modules.job.quartz;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.job.integral.service.IIntegralGoodsEffectService;
import org.jeecg.modules.job.quartz.support.MonitoredJobSupport;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.annotation.Resource;

/**
 * 道具时效定时任务（置顶/加粗过期回写）
 */
@Slf4j
public class GoodsJob implements Job {

	@Resource
	private IIntegralGoodsEffectService effectService;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		MonitoredJobSupport.run(context, "GoodsJob", () -> effectService.autoFinishOrder());
	}
}
