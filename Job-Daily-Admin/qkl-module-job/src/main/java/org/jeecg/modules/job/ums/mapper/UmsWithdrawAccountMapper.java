package org.jeecg.modules.job.ums.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.job.ums.entity.UmsReferrerLog;
import org.jeecg.modules.job.ums.entity.UmsWithdrawAccount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 提现账号
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
public interface UmsWithdrawAccountMapper extends BaseMapper<UmsWithdrawAccount> {

    /**
     * 分页查询提现账号
     * @param page
     * @param paramCondition 参数信息
     * @return
     */
    IPage<UmsWithdrawAccount> getWithdrawAccountList(IPage page, @Param("paramCondition") UmsWithdrawAccount paramCondition);

}
