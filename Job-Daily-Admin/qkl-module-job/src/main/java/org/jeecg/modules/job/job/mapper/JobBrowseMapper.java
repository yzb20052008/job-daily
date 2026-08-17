package org.jeecg.modules.job.job.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.job.job.entity.JobBrowse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.job.job.entity.JobPostContact;

/**
 * @Description: 浏览记录
 * * @Author: qingkonglan
 * @Date:   2024-08-22
 * @Version: V1.0
 */
public interface JobBrowseMapper extends BaseMapper<JobBrowse> {

    /**
     * 分页查询记录
     * @param page
     * @param params 参数信息
     * @return
     */
    IPage<Map<String,Object>> getBrowseList(IPage page, @Param("params") JobBrowse params);

}
