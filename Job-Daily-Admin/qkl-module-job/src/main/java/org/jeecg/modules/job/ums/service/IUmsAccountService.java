package org.jeecg.modules.job.ums.service;

import org.jeecg.modules.job.ums.entity.UmsAccount;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;

/**
 * @Description: 会员账户
 * @Author: qingkonglan
 * @Date:   2022-12-23
 * @Version: V1.0
 */
public interface IUmsAccountService extends IService<UmsAccount> {

    /**
     * 账户添加余额
     */
    void addMemberBalance(BigDecimal money,String tradeType, String userId,String note);

    /**
     * 提现结果操作
     * @param userId
     * @param balance
     * @param withdrawStatus 1-通过，2-拒绝
     */
    void balanceWithdrawResult(String userId,BigDecimal balance,int withdrawStatus);

    /**
     * 扣除余额
     * @param money
     * @param userId
     */
    void subMemberBalance(BigDecimal money,String tradeType,String userId,String note);

    /**
     * 根据会员id查询账户
     */
    UmsAccount findByMemberId(String userId);

}
