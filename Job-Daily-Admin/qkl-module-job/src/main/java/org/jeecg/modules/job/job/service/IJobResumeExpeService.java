package org.jeecg.modules.job.job.service;

import org.jeecg.modules.job.job.entity.JobResumeExpe;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 项目经验
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
public interface IJobResumeExpeService extends IService<JobResumeExpe> {

    /**
     * 添加项目经验
     * @param resumeExp
     * @return
     */
    boolean addResumeExp(JobResumeExpe resumeExp);

    /**
     * 更新项目经验
     * @param resumeExp
     * @return
     */
    boolean updateResumeExp(JobResumeExpe resumeExp);

    /**
     * 查询用户项目经验
     * @param userId
     * @return
     */
    List<JobResumeExpe> getResumeExp(String userId);



}
