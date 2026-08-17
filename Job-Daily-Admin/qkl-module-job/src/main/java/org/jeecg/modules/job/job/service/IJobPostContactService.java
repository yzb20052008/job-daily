package org.jeecg.modules.job.job.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.job.job.entity.JobPostContact;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * @Description: 拨号记录
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
public interface IJobPostContactService extends IService<JobPostContact> {

    /**
     * 添加联系记录
     * @param contact
     * @return
     */
    boolean addContact(JobPostContact contact);

    /**
     * 更新合作意向
     * @param id
     * @param agreeState
     * @return
     */
    boolean updateAgreeState(String id,int agreeState,String userId);

    /**
     * 查询用户最新拨号记录
     * @param userId
     * @param postId
     * @return
     */
    JobPostContact getLastContact(String userId,String postId,String roleCode);

    /**
     * 分页查询拨号记录列表
     * @param page
     * @param params 参数信息
     * @return
     */
    IPage<Map<String,Object>> getContactList(Page<JobPostContact> page, JobPostContact params);


    /**
     * 分页查询拨号记录列表
     * @param page
     * @param params 参数信息
     * @return
     */
    IPage<Map<String,Object>> getContactListForAdmin(Page<JobPostContact> page, JobPostContact params);

}
