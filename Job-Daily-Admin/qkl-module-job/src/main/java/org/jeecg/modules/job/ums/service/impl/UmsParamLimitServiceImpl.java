package org.jeecg.modules.job.ums.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.modules.job.ums.entity.UmsParamLimit;
import org.jeecg.modules.job.ums.mapper.UmsParamLimitMapper;
import org.jeecg.modules.job.ums.service.IUmsParamLimitService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 用户阈值
 * @Author: qingkonglan
 * @Date:   2023-11-20
 * @Version: V1.0
 */
@Service
public class UmsParamLimitServiceImpl extends ServiceImpl<UmsParamLimitMapper, UmsParamLimit> implements IUmsParamLimitService {


    @Override
    public boolean addAi(String userId, int num) {
        UmsParamLimit limit=this.getParamLimit(userId);
        limit.setAiNum(limit.getAiNum() + num);
        return this.updateById(limit);
    }

    @Override
    public UmsParamLimit getParamLimit(String userId) {
        UmsParamLimit limit=this.getOne(new QueryWrapper<>(new UmsParamLimit().setUserId(userId)));
        if (limit==null){
            limit=new UmsParamLimit();
            limit.setUserId(userId);
            limit.setAiNum(3);//默认3个
            limit.setApplyNum(100);//默认100次
            this.save(limit);
        }
        return limit;
    }
}
