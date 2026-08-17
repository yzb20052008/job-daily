package org.jeecg.modules.job.ums.service.impl;

import org.jeecg.modules.job.ums.entity.UmsAccountRecords;
import org.jeecg.modules.job.ums.mapper.UmsAccountRecordsMapper;
import org.jeecg.modules.job.ums.service.IUmsAccountRecordsService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.math.BigDecimal;

/**
 * @Description: 账号流水
 * @Author: qingkonglan
 * @Date:   2022-12-23
 * @Version: V1.0
 */
@Service
public class UmsAccountRecordsServiceImpl extends ServiceImpl<UmsAccountRecordsMapper, UmsAccountRecords> implements IUmsAccountRecordsService {

    @Override
    public UmsAccountRecords getStatistics(UmsAccountRecords paramCondition) {
        return this.baseMapper.getStatistics(paramCondition);
    }

    @Override
    public void addAccountRecords(String userId, BigDecimal money, String tradeType,String addSign, String note) {
        UmsAccountRecords records=new UmsAccountRecords();
        records.setUserId(userId);
        records.setMoney(money);
        records.setTradeType(Integer.parseInt(tradeType));
        records.setAddSign(addSign);
        records.setContent(note);
        this.save(records);
    }
}
