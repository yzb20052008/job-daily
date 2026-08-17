package org.jeecg.modules.job.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.modules.job.home.service.WechatApiService;
import org.jeecg.modules.job.job.entity.JobResumeExpe;
import org.jeecg.modules.job.job.mapper.JobResumeExpeMapper;
import org.jeecg.modules.job.job.service.IJobResumeExpeService;
import org.jeecg.modules.job.utils.JsonUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 项目经验
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
@Service
public class JobResumeExpeServiceImpl extends ServiceImpl<JobResumeExpeMapper, JobResumeExpe> implements IJobResumeExpeService {

    @Resource
    private WechatApiService wechatApiService;

    @Override
    public boolean addResumeExp(JobResumeExpe resumeExp) {
        //敏感字校验
        boolean result = wechatApiService.checkText(JsonUtils.objectToJson(resumeExp));
        if (result==false){
            throw new RuntimeException("内容存在违规信息");
        }
        return this.save(resumeExp);
    }

    @Override
    public boolean updateResumeExp(JobResumeExpe resumeExp) {
        //敏感字校验
        boolean result = wechatApiService.checkText(JsonUtils.objectToJson(resumeExp));
        if (result==false){
            throw new RuntimeException("内容存在违规信息");
        }
        return this.updateById(resumeExp);
    }

    @Override
    public List<JobResumeExpe> getResumeExp(String userId) {
        return this.list(new QueryWrapper<>(new JobResumeExpe().setUserId(userId)));
    }
}
