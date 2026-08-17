package org.jeecg.modules.job.job.service;

import org.jeecg.modules.job.job.entity.JobResumeCert;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.job.job.entity.JobResumeExpe;

import java.util.List;

/**
 * @Description: 技能证书
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
public interface IJobResumeCertService extends IService<JobResumeCert> {

    /**
     * 添加证书
     * @param resumeCert
     * @return
     */
    boolean addResumeCert(JobResumeCert resumeCert);

    /**
     * 更新证书
     * @param resumeCert
     * @return
     */
    boolean updateResumeCert(JobResumeCert resumeCert);

    /**
     * 查询用户证书
     * @param userId
     * @return
     */
    List<JobResumeCert> getResumeCert(String userId);

}
