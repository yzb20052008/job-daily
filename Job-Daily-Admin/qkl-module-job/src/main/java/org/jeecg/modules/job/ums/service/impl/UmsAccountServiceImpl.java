package org.jeecg.modules.job.ums.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.common.exception.JeecgBootException;
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addMemberBalance(BigDecimal money,String tradeType, String userId,String note) {
        UmsAccount account = this.lockByUserId(userId);
        account.setBalance(nvl(account.getBalance()).add(money));
        account.setBalanceWithdraw(nvl(account.getBalanceWithdraw()).add(money));
        if (BizConstants.TRADE_TYPE_RECHARGE_BALANCE.equals(tradeType)) {
            account.setTotalRecharge(nvl(account.getTotalRecharge()).add(money));
        }
        this.updateById(account);
        accountRecordsService.addAccountRecords(userId,money,tradeType,"+",note);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void balanceWithdrawResult(String userId, BigDecimal balance, int withdrawStatus) {
        UmsAccount account = this.lockByUserId(userId);
        if (withdrawStatus== BizConstants.WITHDRAW_STATUS_SUCCESS){
            if (nvl(account.getBalanceFrozen()).compareTo(balance)<0){
                throw new JeecgBootException("账户冻结余额异常");
            }
            account.setBalanceFrozen(account.getBalanceFrozen().subtract(balance));
            account.setBalance(nvl(account.getBalance()).subtract(balance));
            account.setTotalWithdraw(nvl(account.getTotalWithdraw()).add(balance));
            this.updateById(account);
            accountRecordsService.addAccountRecords(userId,balance,BizConstants.TRADE_TYPE_WITHDRAW,"-","提现审核通过到账");
        }else if(withdrawStatus== BizConstants.WITHDRAW_STATUS_FAILURE){
            if (nvl(account.getBalanceFrozen()).compareTo(balance)<0){
                throw new JeecgBootException("账户冻结余额异常");
            }
            account.setBalanceFrozen(account.getBalanceFrozen().subtract(balance));
            account.setBalanceWithdraw(nvl(account.getBalanceWithdraw()).add(balance));
            this.updateById(account);
            accountRecordsService.addAccountRecords(userId,balance,BizConstants.TRADE_TYPE_WITHDRAW,"","提现失败，解冻提现金额");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void subMemberBalance(BigDecimal money,String tradeType, String userId, String note) {
        UmsAccount account = this.lockByUserId(userId);
        if (nvl(account.getBalance()).compareTo(money)<0){
            throw new JeecgBootException("余额不足");
        }
        account.setBalance(account.getBalance().subtract(money));
        this.updateById(account);
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
            account.setTotalRecharge(BigDecimal.ZERO);
            account.setTotalWithdraw(BigDecimal.ZERO);
            account.setTotalConsume(BigDecimal.ZERO);
            this.save(account);
        }
        return account;
    }

    @Override
    public UmsAccount lockByUserId(String userId) {
        // 确保账户存在后再行锁
        this.findByMemberId(userId);
        UmsAccount account = baseMapper.selectOne(new QueryWrapper<UmsAccount>()
                .eq("user_id", userId)
                .last("FOR UPDATE"));
        if (account == null) {
            throw new JeecgBootException("账户不存在");
        }
        return account;
    }

    private BigDecimal nvl(BigDecimal val) {
        return val == null ? BigDecimal.ZERO : val;
    }
}
