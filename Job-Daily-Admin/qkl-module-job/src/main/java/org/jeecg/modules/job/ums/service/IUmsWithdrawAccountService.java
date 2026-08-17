package org.jeecg.modules.job.ums.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.job.ums.entity.UmsReferrerLog;
import org.jeecg.modules.job.ums.entity.UmsWithdrawAccount;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 提现账号
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
public interface IUmsWithdrawAccountService extends IService<UmsWithdrawAccount> {

    /**
     * 更新用户提现账号
     * @param userId
     * @param accountType
     * @param name
     * @param account
     */
    void updateAccount(String userId,int accountType,String name,String account);

    /**
     * 查询用户提现账号
     * @param userId
     * @param accountType
     * @return
     */
    UmsWithdrawAccount getWithdrawAccount(String userId,int accountType);


    /**
     * 查询提现账号列表
     * @param page
     * @param params 参数信息
     * @return
     */
    IPage<UmsWithdrawAccount> getWithdrawAccountList(Page<UmsWithdrawAccount> page, UmsWithdrawAccount params);


}
