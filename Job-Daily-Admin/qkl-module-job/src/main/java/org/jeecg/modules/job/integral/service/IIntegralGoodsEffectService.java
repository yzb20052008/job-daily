package org.jeecg.modules.job.integral.service;

import org.jeecg.modules.job.integral.entity.IntegralGoodsEffect;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 道具时效
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
public interface IIntegralGoodsEffectService extends IService<IntegralGoodsEffect> {

    /**
     * 添加或更新
     * @param effect
     * @return
     */
    boolean addOrUpdateEffect(IntegralGoodsEffect effect);


    /**
     * 自动结束积分道具效果
     * @return
     */
    boolean autoFinishOrder();

}
