package org.jeecg.modules.job.integral.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.job.integral.entity.IntegralLog;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 积分日志
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
public interface IIntegralLogService extends IService<IntegralLog> {

    /**
     * 更新用户积分
     * @param userId
     * @param integral
     */
    void updateIntegral(String userId,int integral);


    /**
     * 积分日志
     * @param page
     * @param params 参数信息
     * @return
     */
    IPage<IntegralLog> getIntegralLogList(Page<IntegralLog> page, IntegralLog params);


    /**
     * 获得积分
     * @param memberId
     * @param jfConfigCode
     * @param dataId
     * @param remark
     * @return
     */
    boolean addIntegralLog(String memberId,String jfConfigCode,String dataId,String remark);


    /**
     * 消费积分
     * @param memberId
     * @param jfConfigCode
     * @param dataId
     * @param remark
     * @return
     */
    boolean addIntegralLogForReduce(String memberId,String jfConfigCode,String dataId,String remark);


    /**
     * 查询用户指定日期获取积分数
     * @param memberId
     * @param dayStart 开始日期（包含），如2023-11-11
     * @param dayEnd  结束日期（不包含），如2023-11-12
     * @return
     */
    int getTotalIntegral(String memberId,String dayStart,String dayEnd);

}
