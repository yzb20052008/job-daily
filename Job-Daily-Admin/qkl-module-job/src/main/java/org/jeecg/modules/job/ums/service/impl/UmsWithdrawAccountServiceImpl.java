package org.jeecg.modules.job.ums.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.job.ums.entity.UmsWithdrawAccount;
import org.jeecg.modules.job.ums.mapper.UmsWithdrawAccountMapper;
import org.jeecg.modules.job.ums.service.IUmsWithdrawAccountService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 提现账号
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
@Service
public class UmsWithdrawAccountServiceImpl extends ServiceImpl<UmsWithdrawAccountMapper, UmsWithdrawAccount> implements IUmsWithdrawAccountService {


    @Override
    public void updateAccount(String userId, int accountType, String name, String account) {
        //判断用户账号是否存在
        UmsWithdrawAccount withdrawAccount=this.getWithdrawAccount(userId,accountType);
        if (withdrawAccount==null){
            //创建账号
            withdrawAccount=new UmsWithdrawAccount();
            withdrawAccount.setUserId(userId);
            withdrawAccount.setAccountType(accountType);
            withdrawAccount.setRealname(name);
            withdrawAccount.setAccount(account);
            this.save(withdrawAccount);
        }else{
            withdrawAccount.setAccount(account);
            withdrawAccount.setRealname(name);
            this.updateById(withdrawAccount);
        }
    }

    @Override
    public UmsWithdrawAccount getWithdrawAccount(String userId, int accountType) {
        return this.getOne(new QueryWrapper<>(new UmsWithdrawAccount().setUserId(userId).setAccountType(accountType)));
    }

    @Override
    public IPage<UmsWithdrawAccount> getWithdrawAccountList(Page<UmsWithdrawAccount> page, UmsWithdrawAccount params) {
        return this.baseMapper.getWithdrawAccountList(page,params);
    }
}
