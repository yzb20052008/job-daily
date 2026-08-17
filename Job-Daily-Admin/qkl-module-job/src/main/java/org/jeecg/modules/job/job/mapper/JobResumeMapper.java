package org.jeecg.modules.job.job.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.job.job.entity.JobBrowse;
import org.jeecg.modules.job.job.entity.JobResume;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 简历信息
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
public interface JobResumeMapper extends BaseMapper<JobResume> {


    /**
     * 分页查询记录
     * @param page
     * @param params 参数信息
     * @return
     */
    IPage<Map<String,Object>> getResumeList(IPage page, @Param("params") JobResume params);

    /**
     * 分页查询记录
     * @param page
     * @param params 参数信息
     * @return
     */
    IPage<Map<String,Object>> getResumeListForAdmin(IPage page, @Param("params") JobResume params);


}
