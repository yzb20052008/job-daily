package org.jeecg.modules.job.job.service;

import org.jeecg.modules.job.job.entity.JobResumeCert;
import org.jeecg.modules.job.job.entity.JobResumeIntention;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 求职意向
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
public interface IJobResumeIntentionService extends IService<JobResumeIntention> {

    /**
     * 更新求职意向
     * @param intention
     * @return
     */
    boolean updateResumeIntention(JobResumeIntention intention);

    /**
     * 查询用户求职意向
     * @param userId
     * @return
     */
    JobResumeIntention getResumeIntention(String userId);


}
