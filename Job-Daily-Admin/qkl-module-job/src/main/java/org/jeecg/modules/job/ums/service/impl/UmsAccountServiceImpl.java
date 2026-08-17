package org.jeecg.modules.job.ums.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.modules.job.constant.BizConstants;
import org.jeecg.modules.job.ums.entity.UmsAccount;
import org.jeecg.modules.job.ums.mapper.UmsAccountMapper;
import org.jeecg.modules.job.ums.service.IUmsAccountRecordsService;
import org.jeecg.modules.job.ums.service.IUmsAccountService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @Description: 会员账户
 * @Author: qingkonglan
 * @Date:   2022-12-23
 * @Version: V1.0
 */
@Service
public class UmsAccountServiceImpl extends ServiceImpl<UmsAccountMapper, UmsAccount> implements IUmsAccountService {


    @Resource
    private IUmsAccountRecordsService accountRecordsService;

    @Transactional
    @Override
    public void addMemberBalance(BigDecimal money,String tradeType, String userId,String note) {
        UmsAccount account=this.findByMemberId(userId);
        account.setBalance(account.getBalance().add(money));
        account.setBalanceWithdraw(account.getBalanceWithdraw().add(money));//进入可提余额
        this.updateById(account);
        //添加余额操作记录
        accountRecordsService.addAccountRecords(userId,money,tradeType,"+",note);
    }

    @Override
    public void balanceWithdrawResult(String userId, BigDecimal balance, int withdrawStatus) {
        UmsAccount account=this.findByMemberId(userId);
        if (withdrawStatus== BizConstants.WITHDRAW_STATUS_SUCCESS){
            //扣除冻结余额
            if (account.getBalanceFrozen().compareTo(balance)<0){
                throw new RuntimeException("账户余额异常");
            }
            account.setBalanceFrozen(account.getBalanceFrozen().subtract(balance));//冻结扣除
            account.setBalance(account.getBalance().subtract(balance));//金额扣除
            account.setTotalWithdraw(account.getTotalWithdraw().add(balance));
            this.updateById(account);
            //添加余额操作记录
            accountRecordsService.addAccountRecords(userId,balance,BizConstants.TRADE_TYPE_WITHDRAW,"-","提现审核通过");
        }else if(withdrawStatus== BizConstants.WITHDRAW_STATUS_FAILURE){
            account.setBalanceFrozen(account.getBalanceFrozen().subtract(balance));//冻结扣除
            account.setBalanceWithdraw(account.getBalanceWithdraw().add(balance));//可提退回
            this.updateById(account);
            //添加余额操作记录
            accountRecordsService.addAccountRecords(userId,balance,BizConstants.TRADE_TYPE_WITHDRAW,"","提现审核不通过，解冻提现金额");
        }
    }

    @Transactional
    @Override
    public void subMemberBalance(BigDecimal money,String tradeType, String userId, String note) {
        UmsAccount account=this.findByMemberId(userId);
        //判断余额是否足够
        if (account.getBalance().compareTo(money)<0){
            throw new RuntimeException("余额不足");
        }
        account.setBalance(account.getBalance().subtract(money));
        this.updateById(account);
        //添加余额操作记录
        accountRecordsService.addAccountRecords(userId,money,tradeType,"-",note);
    }

    @Override
    public UmsAccount findByMemberId(String userId) {
        UmsAccount account= baseMapper.selectOne(new QueryWrapper(new UmsAccount().setUserId(userId)));
        if (account==null){
            account=new UmsAccount();
            account.setUserId(userId);
            account.setBalance(BigDecimal.ZERO);
            account.setBalanceFrozen(BigDecimal.ZERO);
            account.setBalanceWithdraw(BigDecimal.ZERO);
            account.setTotalMoney(BigDecimal.ZERO);
            this.save(account);
        }
        return account;
    }
}
