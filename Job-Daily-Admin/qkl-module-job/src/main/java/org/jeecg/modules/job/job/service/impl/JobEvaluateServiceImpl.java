package org.jeecg.modules.job.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.job.job.entity.JobEvaluate;
import org.jeecg.modules.job.job.mapper.JobEvaluateMapper;
import org.jeecg.modules.job.job.service.IJobEvaluateService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @Description: 用户评分
 * * @Author: qingkonglan
 * @Date:   2024-08-22
 * @Version: V1.0
 */
@Service
public class JobEvaluateServiceImpl extends ServiceImpl<JobEvaluateMapper, JobEvaluate> implements IJobEvaluateService {

    @Override
    public void updateUserEvaluate(String userId, String roleCode, int score) {
        //判断用户评分是否存在
        JobEvaluate evaluate=this.getOne(new LambdaQueryWrapper<JobEvaluate>().eq(JobEvaluate::getUserId,userId).eq(JobEvaluate::getRoleCode,roleCode));
        if (evaluate==null){
            evaluate=new JobEvaluate();
            evaluate.setUserId(userId);
            evaluate.setRoleCode(roleCode);
            evaluate.setTotalScore(new BigDecimal(score));
            evaluate.setNum(1);
            evaluate.setScore(new BigDecimal(score));
            this.save(evaluate);
        }else{
            evaluate.setTotalScore(evaluate.getTotalScore().add(new BigDecimal(score)));
            evaluate.setNum(evaluate.getNum()+1);
            evaluate.setScore(evaluate.getTotalScore().divide(new BigDecimal(evaluate.getNum())).setScale(1));
            this.updateById(evaluate);
        }

    }

    @Override
    public BigDecimal getUserEvaluate(String userId, String roleCode) {
        //判断用户评分是否存在
        JobEvaluate evaluate=this.getOne(new LambdaQueryWrapper<JobEvaluate>().eq(JobEvaluate::getUserId,userId).eq(JobEvaluate::getRoleCode,roleCode));
        if (evaluate==null){
            evaluate=new JobEvaluate();
            evaluate.setUserId(userId);
            evaluate.setRoleCode(roleCode);
            evaluate.setTotalScore(BigDecimal.ZERO);
            evaluate.setNum(0);
            evaluate.setScore(BigDecimal.ZERO);
            this.save(evaluate);
        }
        return evaluate.getScore();
    }

    @Override
    public IPage<Map<String, Object>> getEvaluateList(Page<JobEvaluate> page, JobEvaluate params) {
        return baseMapper.getEvaluateList(page,params);
    }
}
