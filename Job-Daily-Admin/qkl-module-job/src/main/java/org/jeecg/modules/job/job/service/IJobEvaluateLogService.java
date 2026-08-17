package org.jeecg.modules.job.job.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.job.job.entity.JobEvaluateLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * @Description: 评价记录
 * * @Author: qingkonglan
 * @Date:   2024-08-22
 * @Version: V1.0
 */
public interface IJobEvaluateLogService extends IService<JobEvaluateLog> {

    /**
     * 添加评价记录
     * @param log
     */
    void addEvaluateLog(JobEvaluateLog log);

    /**
     * 分页查询评价记录
     * @param page
     * @param params 参数信息
     * @return
     */
    IPage<Map<String,Object>> getMyEvaluateList(Page<JobEvaluateLog> page, JobEvaluateLog params);


    /**
     * 分页查询评价记录
     * @param page
     * @param params 参数信息
     * @return
     */
    IPage<Map<String,Object>> getEvaluateList(Page<JobEvaluateLog> page, JobEvaluateLog params);

}
