package org.jeecg.modules.job.ums.service;

import org.jeecg.modules.job.ums.entity.UmsParamLimit;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 用户阈值
 * @Author: qingkonglan
 * @Date:   2023-11-20
 * @Version: V1.0
 */
public interface IUmsParamLimitService extends IService<UmsParamLimit> {

    /**
     * 查询用户额度
     * @param userId 用户ID
     * @return 额度信息
     */
    UmsParamLimit getParamLimit(String userId);

}
