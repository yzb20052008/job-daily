package org.jeecg.modules.job.integral.service.impl;

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
import org.jeecg.modules.job.integral.mapper.IntegralLogMapper;
import org.jeecg.modules.job.integral.service.IIntegralLogService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 积分日志
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
@Service
public class IntegralLogServiceImpl extends ServiceImpl<IntegralLogMapper, IntegralLog> implements IIntegralLogService {

    @Resource
    private IBaseConfigService configService;
    @Resource
    private ISysBaseAPI sysBaseAPI;

    @Transactional
    @Override
    public void updateIntegral(String userId, int integral) {
        LoginUser sysUser = sysBaseAPI.getUserById(userId);
        if(sysUser==null) {
            throw new RuntimeException("参数错误");
        }else {
            sysUser.setIntegral(sysUser.getIntegral()+integral);
            sysUser.setTotalIntegral(sysUser.getTotalIntegral()+integral);
            sysBaseAPI.updateUserInfo(sysUser);
            //添加积分日志
            IntegralLog log=new IntegralLog();
            log.setIntegral(new BigDecimal(integral));
            log.setIntegralResource(4);
            log.setIfAdd(integral>0?1:0);
            log.setUserId(sysUser.getId());
            log.setRemark("后台积分充值："+integral);
            this.save(log);
        }
    }

    @Override
    public IPage<IntegralLog> getIntegralLogList(Page<IntegralLog> page, IntegralLog params) {
        return baseMapper.getIntegralLogList(page,params);
    }

    @Override
    public boolean addIntegralLog(String memberId, String jfConfigCode, String dataId, String remark) {
        //查询积分阈值
        BaseConfig max=configService.getConfigByCode(BizConstants.JF_DAY_MAX);
        int dayMax=Integer.parseInt(max.getConfigValue());
        //获取用户今日积分总数
        String day= DateUtils.getCurrentTime("yyyy-MM-dd");
        String nextDay= DateUtils.formatDate(DateUtil.offsetDay(new Date(),1),"yyyy-MM-dd");
        int dayTotal=this.getTotalIntegral(memberId,day,nextDay);
        //查询积分
        BaseConfig config=configService.getConfigByCode(jfConfigCode);
        int base=Integer.parseInt(config.getConfigValue());
        int integral=Integer.parseInt(config.getConfigValue());
        if (dayTotal + base >dayMax){
            integral=dayMax-dayTotal;//达到积分上限
        }
        //更新用户积分
        LoginUser user=sysBaseAPI.getUserById(memberId);
        user.setIntegral(user.getIntegral()+integral);
        user.setTotalIntegral(user.getTotalIntegral() + integral);
        sysBaseAPI.updateUserInfo(user);
        //积分记录
        IntegralLog integralLog=new IntegralLog();
        integralLog.setUserId(memberId);
        integralLog.setIfAdd(1);
        integralLog.setIntegral(new BigDecimal(integral));
        integralLog.setIntegralResource(BizConstants.INTEGRAL_RESOURCE_RECHARGE);
        integralLog.setDataId(dataId);
        if (integral < base){
            integralLog.setRemark(remark+integral+",达到每日上限");
        }else{
            integralLog.setRemark(remark+integral);
        }
        return this.save(integralLog);
    }

    @Override
    public boolean addIntegralLogForReduce(String memberId, String jfConfigCode, String dataId, String remark) {
        //查询积分
        BaseConfig config=configService.getConfigByCode(jfConfigCode);
        int integral=Integer.parseInt(config.getConfigValue());
        //更新用户积分
        LoginUser user=sysBaseAPI.getUserById(memberId);
        if(user.getIntegral() < integral){
            throw new RuntimeException("操作失败,积分不足");
        }
        user.setIntegral(user.getIntegral()-integral);
//        user.setTotalIntegral(user.getTotalIntegral() - integral);
        sysBaseAPI.updateUserInfo(user);
        //积分记录
        IntegralLog integralLog=new IntegralLog();
        integralLog.setUserId(memberId);
        integralLog.setIfAdd(0);
        integralLog.setIntegral(new BigDecimal(integral));
        integralLog.setIntegralResource(BizConstants.INTEGRAL_RESOURCE_RECHARGE);
        integralLog.setDataId(dataId);
        integralLog.setRemark(remark+integral);
        return this.save(integralLog);
    }

    @Override
    public int getTotalIntegral(String memberId, String dayStart, String dayEnd) {
        return baseMapper.getTotalIntegral(memberId,dayStart,dayEnd);
    }
}
