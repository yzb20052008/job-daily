package org.jeecg.modules.job.ums.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.job.ums.entity.UmsWithdraw;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 用户提现
 * @Author: qingkonglan
 * @Date:   2023-03-30
 * @Version: V1.0
 */
public interface UmsWithdrawMapper extends BaseMapper<UmsWithdraw> {

    /**
     * 分页查询提现记录
     * @param page
     * @param paramCondition 参数信息
     * @return
     */
    IPage<UmsWithdraw> getWithdrawPageList(IPage page, @Param("paramCondition") UmsWithdraw paramCondition);

}
