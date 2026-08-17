package org.jeecg.modules.job.job.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.job.job.entity.JobCollect;
import org.jeecg.modules.job.job.entity.JobPostContact;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 拨号记录
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
public interface JobPostContactMapper extends BaseMapper<JobPostContact> {


    /**
     * 分页查询拨号记录
     * @param page
     * @param params 参数信息
     * @return
     */
    IPage<Map<String,Object>> getContactList(IPage page, @Param("params") JobPostContact params);

    /**
     * 分页查询拨号记录
     * @param page
     * @param params 参数信息
     * @return
     */
    IPage<Map<String,Object>> getContactListForAdmin(IPage page, @Param("params") JobPostContact params);


}
