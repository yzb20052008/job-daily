package org.jeecg.modules.job.job.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.job.job.entity.JobCompany;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.job.ums.entity.UmsRealnameAuth;

/**
 * @Description: 企业认证
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
public interface IJobCompanyService extends IService<JobCompany> {

    /**
     * 添加或更新企业认证
     * @param company
     * @return
     */
    boolean addOrUpdateCompanyAuth(JobCompany company);

    /**
     * 查询用户企业情况
     * @param userId
     * @return
     */
    JobCompany getCompanyAuth(String userId);

    /**
     * 修改状态
     * @param id  ID
     * @param status  状态 ：0-待审核，1-通过，2-失败
     * @return
     */
    boolean updateStatus(String id,int status,String reason);

    /**
     *  分页查询
     * @param page
     * @param params
     * @return
     */
    IPage<JobCompany> getCompanyAuthList(Page<JobCompany> page, JobCompany params);


}
