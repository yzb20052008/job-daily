package org.jeecg.modules.job.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.job.constant.BizConstants;
import org.jeecg.modules.job.job.entity.JobCollect;
import org.jeecg.modules.job.job.entity.JobOrder;
import org.jeecg.modules.job.job.entity.JobPostContact;
import org.jeecg.modules.job.job.mapper.JobCollectMapper;
import org.jeecg.modules.job.job.mapper.JobPostContactMapper;
import org.jeecg.modules.job.job.service.IJobCollectService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 我的收藏
 * * @Author: qingkonglan
 * @Date:   2024-08-22
 * @Version: V1.0
 */
@Service
public class JobCollectServiceImpl extends ServiceImpl<JobCollectMapper, JobCollect> implements IJobCollectService {

    @Resource
    private JobPostContactMapper contactMapper;

    @Override
    public boolean updateCollect(JobCollect param) {
        JobCollect result=this.getOne(new QueryWrapper<>(new JobCollect().setUserId(param.getUserId()).setDataId(param.getDataId()).setRoleCode(param.getRoleCode())));
        if (result!=null){
            return this.removeById(result.getId());
        }else{
            return this.save(param);
        }
    }

    @Override
    public JobCollect getCollect(String userId, String dataId, String roleCode) {
        return getOne(new QueryWrapper<>(new JobCollect().setUserId(userId).setDataId(dataId).setRoleCode(roleCode)));
    }

    @Override
    public IPage<Map<String, Object>> getCollectList(Page<JobCollect> page, JobCollect params) {
        if (BizConstants.ROLE_CODE_MEMBER.equals(params.getRoleCode())){
            //工人
            return baseMapper.getCollectList(page,params);
        }else{
            //老板
            return baseMapper.getCollectListForCom(page,params);
        }
    }

    @Override
    public Map<String, Object> getMyStatistics(String userId,String roleCode) {
        Map<String,Object> map=new HashMap<>();
        //我的关注数量
        long collect=this.count(new QueryWrapper<>(new JobCollect().setUserId(userId).setRoleCode(roleCode)));
        //我的联系数量
        long contact=0;
        if(BizConstants.ROLE_CODE_COMPANY.equals(roleCode)){
            //企业端
             contact=contactMapper.selectCount(new QueryWrapper<>(new JobPostContact().setPostUserId(userId)));
        }else{
            contact=contactMapper.selectCount(new QueryWrapper<>(new JobPostContact().setUserId(userId)));
        }
        map.put("collect",collect);
        map.put("contact",contact);
        return map;
    }
}
