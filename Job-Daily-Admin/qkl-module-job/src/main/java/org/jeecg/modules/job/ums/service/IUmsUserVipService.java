package org.jeecg.modules.job.ums.service;

import org.jeecg.modules.job.ums.entity.UmsUserVip;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.job.ums.entity.UmsVip;

/**
 * @Description: 用户会员
 * @Author: qingkonglan
 * @Date:   2023-09-23
 * @Version: V1.0
 */
public interface IUmsUserVipService extends IService<UmsUserVip> {

    /**
     * 添加或更新用户会员信息
     * @return
     */
    boolean addOrUpdateVip(UmsVip vip,String userId);


    /**
     * 查询用户会员信息
     * @param userId
     * @param roleCode
     * @return
     */
    UmsUserVip getUserVip(String userId,String roleCode);

}
