package org.jeecg.modules.job.job.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.job.job.entity.JobCompany;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.job.ums.entity.UmsRealnameAuth;

/**
 * @Description: 企业认证
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
public interface JobCompanyMapper extends BaseMapper<JobCompany> {

    /**
     *  分页查询
     * @param page
     * @param params
     * @return
     */
    IPage<JobCompany> getCompanyAuthList(IPage page, @Param("paramCondition")JobCompany params);
}
