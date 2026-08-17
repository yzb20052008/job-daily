package org.jeecg.modules.job.quartz;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.annotation.Resource;

/**
 *  道具定时任务
 * 
 */
@Slf4j
public class GoodsJob implements Job {
//	@Resource
//	private IJfGoodsEffectService effectService;

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		log.info(" effect Execution key："+jobExecutionContext.getJobDetail().getKey());
//		effectService.autoFinishOrder();
		log.info(String.format(" 道具定时任务 GoodsJob !  时间:" + DateUtils.getTimestamp()));
	}
}
