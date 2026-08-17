package org.jeecg.modules.job.ums.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.job.base.entity.BaseConfig;
import org.jeecg.modules.job.base.service.IBaseConfigService;
import org.jeecg.modules.job.constant.BizConstants;
import org.jeecg.modules.job.integral.service.IIntegralLogService;
import org.jeecg.modules.job.ums.entity.UmsSign;
import org.jeecg.modules.job.ums.mapper.UmsSignMapper;
import org.jeecg.modules.job.ums.service.IUmsSignService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 签到记录
 * @Author: qingkonglan
 * @Date:   2023-11-15
 * @Version: V1.0
 */
@Service
public class UmsSignServiceImpl extends ServiceImpl<UmsSignMapper, UmsSign> implements IUmsSignService {

    @Resource
    private IBaseConfigService configService;
    @Resource
    private IIntegralLogService integralLogService;


    @Override
    public boolean addSign(String userId) {
        String day = DateUtils.getCurrentTime("yyyy-MM-dd");
        //判断是否已签到
        if (getSign(userId, day) != null) {
            throw new RuntimeException("今日已签到,无需重复");
        }
        UmsSign sign = new UmsSign();
        sign.setSignDate(day);
        sign.setCreateTime(new Date());
        sign.setUserId(userId);
        BaseConfig config=configService.getConfigByCode(BizConstants.JF_SIGN);//签到积分
        int baseIntegral=Integer.parseInt(config.getConfigValue());
        //查询今天是连续第几天
        String lastDay = DateUtils.formatDate(DateUtil.offsetDay(new Date(),-1),"yyyy-MM-dd");
        UmsSign lastSign = getSign(userId, lastDay);
        if (lastSign != null) {
            //计算积分
            sign.setIntegral(baseIntegral);
            if (lastSign.getSeriesDays() == 7) {
                //重新开始计算
                sign.setSeriesDays(1);
            } else {
                //前一天有签到，签到连续天数为上一次的连续天数加1
                sign.setSeriesDays(lastSign.getSeriesDays() + 1);
                if (lastSign.getSeriesDays() == 6) {
                    //第七天，获取最大积分
                    BaseConfig max=configService.getConfigByCode(BizConstants.JF_SIGN_SEVEN);//7天签到积分
                    int maxIntegral=Integer.parseInt(max.getConfigValue());
                    sign.setIntegral(maxIntegral);

                }
            }
        } else {
            //前一天没有签到，签到连续天数为1，积分为基础积分
            sign.setSeriesDays(1);
            sign.setIntegral(baseIntegral);
        }
        if (lastSign != null && lastSign.getSeriesDays() == 6){
            integralLogService.addIntegralLog(userId,BizConstants.JF_SIGN_SEVEN,"","每日签到(连续七日)获得积分：");
        }else{
            integralLogService.addIntegralLog(userId,BizConstants.JF_SIGN,"","每日签到获得积分：");
        }
        return this.save(sign);
    }

    @Override
    public boolean addReSign(String userId, String signDate) {
        return false;
    }

    @Override
    public UmsSign getSign(String userId, String date) {
        UmsSign sign = this.getOne(new QueryWrapper<UmsSign>().eq("sign_date", date).eq("user_id", userId));
        return sign;
    }

    @Override
    public Map<String, Object> getLeftReSignCount(String userId) {
        Map<String,Object> map=new HashMap<>();
        BaseConfig config=configService.getConfigByCode(BizConstants.JF_SIGN);//签到积分
        int baseIntegral=Integer.parseInt(config.getConfigValue());
        BaseConfig seven=configService.getConfigByCode(BizConstants.JF_SIGN_SEVEN);//七天签到积分
        int sevenIntegral=Integer.parseInt(seven.getConfigValue());
        map.put("baseIntegral",baseIntegral);
        map.put("sevenIntegral",sevenIntegral);
        String lastDay = DateUtils.formatDate(DateUtil.offsetDay(new Date(),-1),"yyyy-MM-dd");
        UmsSign lastSign = getSign(userId, lastDay);
        int todaySign=baseIntegral;
        if (lastSign != null) {
            //计算积分
            if (lastSign.getSeriesDays() == 6) {
                //第七天，获取最大积分
                todaySign=sevenIntegral;
            }
        }
        map.put("todaySign",todaySign);
        return map;
    }

    @Override
    public IPage<UmsSign> getSignList(Page<UmsSign> page, UmsSign params) {
        return baseMapper.getSignList(page,params);
    }
}
