package org.jeecg.modules.job.job.mapper;

import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.job.job.entity.JobPost;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 招工信息
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
public interface JobPostMapper extends BaseMapper<JobPost> {

    /**
     * 分页查询招工信息
     * @param page
     * @param params 参数信息
     * @return
     */
    IPage<Map<String,Object>> getPostMapList(IPage page, @Param("params") JobPost params);

    /**
     * 分页查询招工信息
     * @param page
     * @param params 参数信息
     * @return
     */
    IPage<Map<String,Object>> getPostMapListForAdmin(IPage page, @Param("params") JobPost params);


}
