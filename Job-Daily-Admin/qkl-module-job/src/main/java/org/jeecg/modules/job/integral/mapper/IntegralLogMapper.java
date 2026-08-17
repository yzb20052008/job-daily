package org.jeecg.modules.job.integral.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.job.integral.entity.IntegralLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 积分日志
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
public interface IntegralLogMapper extends BaseMapper<IntegralLog> {


    /**
     * 查询用户指定日期获取积分数
     * @param memberId
     * @param dayStart 开始日期（包含），如2023-11-11
     * @param dayEnd  结束日期（不包含），如2023-11-12
     * @return
     */
    int getTotalIntegral(@Param("memberId") String memberId, @Param("dayStart")String dayStart, @Param("dayEnd")String dayEnd);

    /**
     *  分页查询签到情况
     * @param page
     * @param params
     * @return
     */
    IPage<IntegralLog> getIntegralLogList(IPage page, @Param("paramCondition")IntegralLog params);

}
