package org.jeecg.modules.job.ums.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.job.base.entity.BaseConfig;
import org.jeecg.modules.job.base.service.IBaseConfigService;
import org.jeecg.modules.job.constant.BizConstants;
import org.jeecg.modules.job.integral.entity.IntegralLog;
import org.jeecg.modules.job.integral.service.IIntegralLogService;
import org.jeecg.modules.job.ums.entity.UmsReferrerLog;
import org.jeecg.modules.job.ums.mapper.UmsReferrerLogMapper;
import org.jeecg.modules.job.ums.service.IUmsReferrerLogService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 邀请记录
 * @Author: qingkonglan
 * @Date:   2023-09-03
 * @Version: V1.0
 */
@Service
public class UmsReferrerLogServiceImpl extends ServiceImpl<UmsReferrerLogMapper, UmsReferrerLog> implements IUmsReferrerLogService {

    @Resource
    private ISysBaseAPI sysBaseAPI;
    @Resource
    private IIntegralLogService integralLogService;
    @Resource
    private IBaseConfigService configService;

    @Transactional
    @Override
    public boolean addReferrerLog(String role,String userId, String refererId) {
        BaseConfig config=null;
        String remark="";
        if ("company".equals(role)){
            config=configService.getConfigByCode(BizConstants.JF_REFER_COMPANY);
            remark="邀请企业注册获得积分:";
        }else{
            config=configService.getConfigByCode(BizConstants.JF_REFER_USER);
            remark="邀请用户注册获得积分:";
        }
        UmsReferrerLog log=new UmsReferrerLog();
        log.setMemberId(userId);
        log.setReferrerId(refererId);
        log.setIntegral(Integer.parseInt(config.getConfigValue()));//邀请新用户得积分
        int base=Integer.parseInt(config.getConfigValue());
        int integral=base;
        this.save(log);
        //更新用户积分(邀请人)
        LoginUser user=sysBaseAPI.getUserById(refererId);
        if (user!=null){
            //查询积分阈值
            BaseConfig max=configService.getConfigByCode(BizConstants.JF_DAY_MAX);
            int dayMax=Integer.parseInt(max.getConfigValue());
//            获取用户今日积分总数
            String day= DateUtils.getCurrentTime("yyyy-MM-dd");
            String nextDay= DateUtils.formatDate(DateUtil.offsetDay(new Date(),1),"yyyy-MM-dd");
            int dayTotal=integralLogService.getTotalIntegral(refererId,day,nextDay);
            if (dayTotal + base >dayMax){
                integral=dayMax-dayTotal;//达到积分上限
            }
//            更新用户积分
            user.setIntegral(user.getIntegral()+integral);
            user.setTotalIntegral(user.getTotalIntegral() + integral);
            sysBaseAPI.updateUserInfo(user);
            //积分记录
            IntegralLog integralLog=new IntegralLog();
            integralLog.setUserId(refererId);
            integralLog.setIfAdd(1);
            integralLog.setIntegral(new BigDecimal(integral));
            integralLog.setIntegralResource(BizConstants.INTEGRAL_RESOURCE_RECHARGE);
            integralLog.setDataId(log.getId());
            if (integral < base){
                integralLog.setRemark(remark+integral+",达到每日上限");
            }else{
                integralLog.setRemark(remark+integral);
            }
            integralLogService.save(integralLog);
        }
        return true;
    }

    @Override
    public IPage<UmsReferrerLog> getReferrerPageList(Page<UmsReferrerLog> page, UmsReferrerLog params) {
        return this.baseMapper.getReferrerPageList(page,params);
    }

    @Override
    public UmsReferrerLog getReferrerCount(String memberId,String dayStart, String dayEnd) {
        return this.baseMapper.getReferrerCount(memberId,dayStart,dayEnd);
    }
}
