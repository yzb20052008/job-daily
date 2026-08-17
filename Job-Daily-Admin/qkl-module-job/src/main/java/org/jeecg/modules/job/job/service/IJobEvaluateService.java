package org.jeecg.modules.job.job.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.job.job.entity.JobEvaluate;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.job.job.entity.JobEvaluateLog;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @Description: 用户评分
 * * @Author: qingkonglan
 * @Date:   2024-08-22
 * @Version: V1.0
 */
public interface IJobEvaluateService extends IService<JobEvaluate> {

    /**
     * 更新用户评分
     * @param userId
     * @param score
     */
    void updateUserEvaluate(String userId,String roleCode,int score);

    /**
     * 查询用户评分
     * @param userId
     * @param roleCode
     * @return
     */
    BigDecimal getUserEvaluate(String userId,String roleCode);


    /**
     * 分页查询评价记录
     * @param page
     * @param params 参数信息
     * @return
     */
    IPage<Map<String,Object>> getEvaluateList(Page<JobEvaluate> page, JobEvaluate params);

}
