package org.jeecg.modules.job.ums.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.job.ums.entity.UmsReferrerLog;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 邀请记录
 * @Author: qingkonglan
 * @Date:   2023-09-03
 * @Version: V1.0
 */
public interface IUmsReferrerLogService extends IService<UmsReferrerLog> {


    /**
     * 添加邀请记录
     * @param role 注册角色
     * @param userId 新注册用户
     * @param refererId  邀请人
     * @return
     */
    boolean addReferrerLog(String role,String userId,String refererId);


    /**
     * 邀请日志
     * @param page
     * @param params 参数信息
     * @return
     */
    IPage<UmsReferrerLog> getReferrerPageList(Page<UmsReferrerLog> page, UmsReferrerLog params);

    /**
     *   统计邀请数量,包含两端
     * @param dayStart
     * @param dayEnd
     * @return
     */
    UmsReferrerLog getReferrerCount(String memberId,String dayStart,String dayEnd);

}
