package org.jeecg.modules.job.job.vo;

import lombok.Data;
import org.jeecg.modules.job.job.entity.JobResume;
import org.jeecg.modules.job.job.entity.JobResumeCert;
import org.jeecg.modules.job.job.entity.JobResumeExpe;
import org.jeecg.modules.job.job.entity.JobResumeIntention;

import java.util.List;

/**
 * 简历信息
 */
@Data
public class JobResumeVo extends JobResume {
    //求职意向
    private JobResumeIntention intention;
    //经验列表
    private List<JobResumeExpe> ExpList;
    //证书列表
    private List<JobResumeCert> certList;
    //是否实名
    private boolean ifRealName;
    //是否收藏
    private boolean ifCollected;
}
