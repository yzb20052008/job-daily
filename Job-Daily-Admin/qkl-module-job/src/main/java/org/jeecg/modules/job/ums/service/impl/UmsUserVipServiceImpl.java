package org.jeecg.modules.job.ums.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.modules.job.ums.entity.UmsUserVip;
import org.jeecg.modules.job.ums.entity.UmsVip;
import org.jeecg.modules.job.ums.mapper.UmsUserVipMapper;
import org.jeecg.modules.job.ums.service.IUmsUserVipService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Date;

/**
 * @Description: 用户会员
 * @Author: qingkonglan
 * @Date:   2023-09-23
 * @Version: V1.0
 */
@Service
public class UmsUserVipServiceImpl extends ServiceImpl<UmsUserVipMapper, UmsUserVip> implements IUmsUserVipService {

    @Override
    public boolean addOrUpdateVip(UmsVip vip, String userId) {
        //查询vip是否存在
        UmsUserVip userVip=this.getUserVip(userId,vip.getRoleCode());
        if (userVip !=null){
            userVip.setVipLevel(vip.getVipType());
            if (userVip.getVipEndTime()==null || userVip.getVipEndTime().compareTo(new Date())<0){
                //当前不是是会员
                userVip.setVipEndTime(DateUtil.offset(new Date(), DateField.DAY_OF_MONTH, vip.getDays()));
            }else{
                //当前是会员
                userVip.setVipEndTime(DateUtil.offset(userVip.getVipEndTime(), DateField.DAY_OF_MONTH, vip.getDays()));
            }
            return this.updateById(userVip);
        }else{
            userVip=new UmsUserVip();
            userVip.setUserId(userId);
            userVip.setRoleCode(vip.getRoleCode());
            userVip.setVipLevel(vip.getVipType());
            userVip.setVipEndTime(DateUtil.offset(new Date(), DateField.DAY_OF_MONTH, vip.getDays()));
            return this.save(userVip);
        }
    }

    @Override
    public UmsUserVip getUserVip(String userId, String roleCode) {
        return this.getOne(new QueryWrapper<>(new UmsUserVip().setUserId(userId).setRoleCode(roleCode)));
    }


}
