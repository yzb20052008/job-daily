package org.jeecg.modules.job.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.modules.job.home.service.WechatApiService;
import org.jeecg.modules.job.job.entity.JobResumeCert;
import org.jeecg.modules.job.job.mapper.JobResumeCertMapper;
import org.jeecg.modules.job.job.service.IJobResumeCertService;
import org.jeecg.modules.job.utils.JsonUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 技能证书
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
@Service
public class JobResumeCertServiceImpl extends ServiceImpl<JobResumeCertMapper, JobResumeCert> implements IJobResumeCertService {
    @Resource
    private WechatApiService wechatApiService;

    @Override
    public boolean addResumeCert(JobResumeCert resumeCert) {
        //敏感字校验
        boolean result = wechatApiService.checkText(JsonUtils.objectToJson(resumeCert));
        if (result==false){
            throw new RuntimeException("内容存在违规信息");
        }
        return this.save(resumeCert);
    }

    @Override
    public boolean updateResumeCert(JobResumeCert resumeCert) {
        //敏感字校验
        boolean result = wechatApiService.checkText(JsonUtils.objectToJson(resumeCert));
        if (result==false){
            throw new RuntimeException("内容存在违规信息");
        }
        return this.updateById(resumeCert);
    }

    @Override
    public List<JobResumeCert> getResumeCert(String userId) {
        return this.list(new QueryWrapper<>(new JobResumeCert().setUserId(userId)));
    }
}
