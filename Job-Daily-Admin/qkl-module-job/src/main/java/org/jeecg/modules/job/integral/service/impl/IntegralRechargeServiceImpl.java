package org.jeecg.modules.job.integral.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.zxing.client.result.BizcardResultParser;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.job.base.entity.BaseConfig;
import org.jeecg.modules.job.base.service.IBaseConfigService;
import org.jeecg.modules.job.constant.BizConstants;
import org.jeecg.modules.job.integral.entity.IntegralLog;
import org.jeecg.modules.job.integral.entity.IntegralRecharge;
import org.jeecg.modules.job.integral.mapper.IntegralRechargeMapper;
import org.jeecg.modules.job.integral.service.IIntegralLogService;
import org.jeecg.modules.job.integral.service.IIntegralRechargeService;
import org.jeecg.modules.job.ums.entity.UmsAccount;
import org.jeecg.modules.job.ums.service.IUmsAccountRecordsService;
import org.jeecg.modules.job.ums.service.IUmsAccountService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * @Description: 积分充值记录
 * * @Author: qingkonglan
 * @Date:   2024-08-29
 * @Version: V1.0
 */
@Service
public class IntegralRechargeServiceImpl extends ServiceImpl<IntegralRechargeMapper, IntegralRecharge> implements IIntegralRechargeService {

    @Resource
    private IBaseConfigService configService;
    @Resource
    private IIntegralLogService integralLogService;
    @Resource
    private ISysBaseAPI sysBaseAPI;
    @Resource
    private IUmsAccountService accountService;
    @Resource
    private IUmsAccountRecordsService accountRecordsService;


    @Transactional
    @Override
    public boolean yePayIntegral(String userId, BigDecimal money) {
        UmsAccount account=accountService.findByMemberId(userId);
        //判断用户余额是否足够
        if (account.getBalanceWithdraw().compareTo(money)<0){
            throw new RuntimeException("账户余额不足");
        }
        //添加订单记录
        String orderSn= DateUtils.formatDate(new Date(),"yyyyMMddHHmmss")+ RandomUtil.randomNumbers(5);
        this.createRechargeOrder(orderSn,userId,money.toString(),BizConstants.PAY_TYPE_YE);
        //扣除账户余额
        account.setBalance(account.getBalance().subtract(money));//金额扣除
        account.setBalanceWithdraw(account.getBalanceWithdraw().subtract(money));//可提余额
        accountService.updateById(account);
        //添加余额操作记录
        accountRecordsService.addAccountRecords(userId,money,BizConstants.TRADE_TYPE_RECHARGE_INTEGRAL,"-","积分充值支付余额："+money+"元");
        return true;
    }


    @Override
    public void createRechargeOrder(String orderSn, String userId, String money, String payType) {
        IntegralRecharge recharge=new IntegralRecharge();
        recharge.setUserId(userId);
        recharge.setOrderSn(orderSn);
        recharge.setMoney(new BigDecimal(money));
        recharge.setPayType(payType);
        if (payType.equals(BizConstants.PAY_TYPE_YE)){
            recharge.setRechargeStatus(BizConstants.RECHARGE_STATUS_SUCCESS);
        }else{
            recharge.setRechargeStatus(BizConstants.RECHARGE_STATUS_DEFAULT);
        }
        //计算转换积分
        BaseConfig config=configService.getConfigByCode(BizConstants.JF_RECHARGE_RATIO);
        String ratio=config.getConfigValue();
        recharge.setIntegral(new BigDecimal(money).multiply(new BigDecimal(ratio)));
        this.save(recharge);
        //充值成功积分操作
//        this.updateRechargeOrder(orderSn);
    }

    @Transactional
    @Override
    public void updateRechargeOrder(String orderSn) {
        //查询订单
        IntegralRecharge order=getOne(new QueryWrapper<>(new IntegralRecharge().setOrderSn(orderSn)));
        if (order==null){
            System.err.println("充值订单不存在");
            return;
        }
        order.setRechargeStatus(BizConstants.RECHARGE_STATUS_SUCCESS);//充值成功
        this.updateById(order);
        //更新用户积分
        LoginUser user=sysBaseAPI.getUserById(order.getUserId());
        user.setIntegral(user.getIntegral()+order.getIntegral().intValue());
        user.setTotalIntegral(user.getTotalIntegral() + order.getIntegral().intValue());
        sysBaseAPI.updateUserInfo(user);
        //积分记录
        IntegralLog integralLog=new IntegralLog();
        integralLog.setUserId(order.getUserId());
        integralLog.setIfAdd(1);
        integralLog.setIntegral(order.getIntegral());
        integralLog.setRemark("积分充值："+order.getIntegral());
        integralLog.setIntegralResource(BizConstants.INTEGRAL_RESOURCE_RECHARGE);
        integralLog.setDataId(order.getId());
        integralLogService.save(integralLog);
    }

    @Override
    public IPage<Map<String, Object>> getRechargeOrderList(Page<IntegralRecharge> page, IntegralRecharge params) {
        return baseMapper.getRechargeOrderList(page,params);
    }
}
