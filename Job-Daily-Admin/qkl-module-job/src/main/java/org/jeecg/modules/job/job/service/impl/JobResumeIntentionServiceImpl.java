package org.jeecg.modules.job.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.job.job.entity.JobResume;
import org.jeecg.modules.job.job.entity.JobResumeIntention;
import org.jeecg.modules.job.job.mapper.JobResumeIntentionMapper;
import org.jeecg.modules.job.job.mapper.JobResumeMapper;
import org.jeecg.modules.job.job.service.IJobResumeIntentionService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @Description: 求职意向
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
@Service
public class JobResumeIntentionServiceImpl extends ServiceImpl<JobResumeIntentionMapper, JobResumeIntention> implements IJobResumeIntentionService {

    @Resource
    private JobResumeMapper resumeMapper;
    @Resource
    private ISysBaseAPI sysBaseAPI;

    @Transactional
    @Override
    public boolean updateResumeIntention(JobResumeIntention intention) {
        //判断是否已存在
        JobResumeIntention result=this.getOne(new QueryWrapper<>(new JobResumeIntention().setUserId(intention.getUserId())));
        if (result!=null){
            intention.setId(result.getId());
            this.updateById(intention);
        }else{
            this.save(intention);
        }
        //判断简历是否存在
        JobResume resume=resumeMapper.selectOne(new QueryWrapper<>(new JobResume().setUserId(intention.getUserId())));
        if (resume==null){
            resume=new JobResume();
            LoginUser user=sysBaseAPI.getUserById(intention.getUserId());
            resume.setUserId(intention.getUserId());
            resume.setPhone(user.getPhone());
            resume.setName(user.getNickname());
            resume.setSex(user.getSex());
            resume.setPercentage(30);
            resumeMapper.insert(resume);
        }else{
            resume.setPercentage(resume.getPercentage()+20);
            if (resume.getPercentage() > 100){
                resume.setPercentage(100);
            }
            resumeMapper.updateById(resume);
        }
        return true;
    }

    @Override
    public JobResumeIntention getResumeIntention(String userId) {
        return getOne(new QueryWrapper<>(new JobResumeIntention().setUserId(userId)));
    }
}
