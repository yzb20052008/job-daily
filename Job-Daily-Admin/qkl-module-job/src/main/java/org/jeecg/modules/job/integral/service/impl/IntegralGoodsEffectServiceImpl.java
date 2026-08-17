package org.jeecg.modules.job.integral.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.modules.job.constant.BizConstants;
import org.jeecg.modules.job.integral.entity.IntegralGoodsEffect;
import org.jeecg.modules.job.integral.mapper.IntegralGoodsEffectMapper;
import org.jeecg.modules.job.integral.service.IIntegralGoodsEffectService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Date;
import java.util.List;

/**
 * @Description: 道具时效
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
@Service
public class IntegralGoodsEffectServiceImpl extends ServiceImpl<IntegralGoodsEffectMapper, IntegralGoodsEffect> implements IIntegralGoodsEffectService {

    @Override
    public boolean addOrUpdateEffect(IntegralGoodsEffect effect) {
        //根据岗位ID和道具ID判断是否已存在
        IntegralGoodsEffect result=this.getOne(new QueryWrapper<>(new IntegralGoodsEffect().setDataId(effect.getDataId()).setGoodsId(effect.getGoodsId())));
        if (result!=null){
            //已存在，延期
            if (result.getStatus()==0){
                //已失效，则直接配置到期时间
                result.setStatus(1);//启用
                result.setStartTime(new Date());
                result.setEndTime(DateUtil.offsetHour(result.getStartTime(),effect.getPeriod()));
            }else{
                //未结束，直接延长结束时间
                result.setEndTime(DateUtil.offsetHour(result.getEndTime(),effect.getPeriod()));
            }
            return this.updateById(result);
        }else{
            return this.save(effect);
        }
    }

    @Override
    public boolean autoFinishOrder() {
        //查询已过期的
        LambdaQueryWrapper<IntegralGoodsEffect> query=new LambdaQueryWrapper();
        query.eq(IntegralGoodsEffect::getStatus,1);
        query.lt(IntegralGoodsEffect::getEndTime,new Date());
        List<IntegralGoodsEffect> list=this.list(query);
        for (IntegralGoodsEffect effect:list){
            //更新状态
            effect.setStatus(0);
            this.updateById(effect);
            //更新职位状态
            if (effect.getGoodsCode().equals(BizConstants.JF_CODE_TOPPING)){//置顶
//                positionMapper.updateById(new RmsCompanyPosition().setId(effect.getDataId()).setIfTopping(0));
            }else if (effect.getGoodsCode().equals(BizConstants.JF_CODE_EYE)){//加粗
//                positionMapper.updateById(new RmsCompanyPosition().setId(effect.getDataId()).setIfBold(0));
            }
        }
        return true;
    }
}
