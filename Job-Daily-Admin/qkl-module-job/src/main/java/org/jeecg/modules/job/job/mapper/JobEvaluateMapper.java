package org.jeecg.modules.job.job.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.job.job.entity.JobEvaluate;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.job.job.entity.JobEvaluateLog;

/**
 * @Description: 用户评分
 * * @Author: qingkonglan
 * @Date:   2024-08-22
 * @Version: V1.0
 */
public interface JobEvaluateMapper extends BaseMapper<JobEvaluate> {

    /**
     * 分页查询用户评分
     * @param page
     * @param params 参数信息
     * @return
     */
    IPage<Map<String,Object>> getEvaluateList(IPage page, @Param("params") JobEvaluate params);


}
