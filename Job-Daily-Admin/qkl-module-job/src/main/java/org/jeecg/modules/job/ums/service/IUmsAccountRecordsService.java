package org.jeecg.modules.job.ums.service;

import org.jeecg.modules.job.ums.entity.UmsAccountRecords;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;

/**
 * @Description: 账号流水
 * @Author: qingkonglan
 * @Date:   2022-12-23
 * @Version: V1.0
 */
public interface IUmsAccountRecordsService extends IService<UmsAccountRecords> {

    /**
     * 查询统计
     * @param paramCondition
     * @return
     */
    UmsAccountRecords getStatistics(UmsAccountRecords paramCondition);

    /**
     * 添加余额记录
     * @param userId
     * @param money
     * @param addSign
     * @param note
     */
    void addAccountRecords(String userId, BigDecimal money,String tradeType,String addSign,String note);

}
