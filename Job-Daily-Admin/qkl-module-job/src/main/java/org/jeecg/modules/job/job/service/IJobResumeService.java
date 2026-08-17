package org.jeecg.modules.job.job.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.job.api.model.UserLocation;
import org.jeecg.modules.job.job.entity.*;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.job.job.vo.JobResumeVo;

import java.util.Map;

/**
 * @Description: 简历信息
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
public interface IJobResumeService extends IService<JobResume> {

    /**
     * 创建默认简历
     * @param user
     * @return
     */
    boolean createDefaultResume(LoginUser user,UserLocation userLocation);

    /**
     * 更新简历完善度
     * @param userId
     * @param num
     */
    void updateResumePercentage(String userId,int num);

    /**
     * 添加/更新简历信息
     * @param resume
     * @return
     */
    boolean updateResume(JobResume resume);

    /**
     * 查询简历信息
     * @param userId
     * @return
     */
    JobResumeVo getResumeInfo(String userId);

    /**
     * 查询简历信息
     * @param id
     * @return
     */
    JobResumeVo getResumeById(String id);

    /**
     * 分页查询简历列表
     * @param page
     * @param params
     * @return
     */
    IPage<Map<String,Object>> getResumeList(Page<JobResume> page, JobResume params);

    /**
     * 分页查询简历列表
     * @param page
     * @param params
     * @return
     */
    IPage<Map<String,Object>> getResumeListForAdmin(Page<JobResume> page, JobResume params);

}
