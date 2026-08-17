package org.jeecg.modules.job.job.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.job.job.entity.JobEvaluateLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.job.job.entity.JobOrderLog;
import org.jeecg.modules.job.job.entity.JobPostContact;

/**
 * @Description: 评价记录
 * * @Author: qingkonglan
 * @Date:   2024-08-22
 * @Version: V1.0
 */
public interface JobEvaluateLogMapper extends BaseMapper<JobEvaluateLog> {

    /**
     * 分页查询评价记录
     * @param page
     * @param params 参数信息
     * @return
     */
    IPage<Map<String,Object>> getMyEvaluateList(IPage page, @Param("params") JobEvaluateLog params);


    /**
     * 分页查询评价记录
     * @param page
     * @param params 参数信息
     * @return
     */
    IPage<Map<String,Object>> getBossEvaluateList(IPage page, @Param("params") JobEvaluateLog params);


    /**
     * 分页查询评价记录
     * @param page
     * @param params 参数信息
     * @return
     */
    IPage<Map<String,Object>> getUserEvaluateList(IPage page, @Param("params") JobEvaluateLog params);

}
