package org.jeecg.modules.job.ums.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.job.ums.entity.UmsAccountRecords;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.job.ums.entity.UmsWithdraw;

/**
 * @Description: 账号流水
 * @Author: qingkonglan
 * @Date:   2022-12-23
 * @Version: V1.0
 */
public interface UmsAccountRecordsMapper extends BaseMapper<UmsAccountRecords> {


    /**
     * 查询统计
     * @param paramCondition
     * @return
     */
    UmsAccountRecords getStatistics(@Param("paramCondition") UmsAccountRecords paramCondition);


    /**
     * 分页查询账号流水
     * @param page
     * @param paramCondition 参数信息
     * @return
     */
    IPage<UmsAccountRecords> getAccountRecordsPageList(IPage page, @Param("paramCondition") UmsAccountRecords paramCondition);

}
