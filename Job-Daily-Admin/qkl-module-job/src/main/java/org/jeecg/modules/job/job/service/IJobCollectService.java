package org.jeecg.modules.job.job.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.job.job.entity.JobCollect;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * @Description: 我的收藏
 * * @Author: qingkonglan
 * @Date:   2024-08-22
 * @Version: V1.0
 */
public interface IJobCollectService extends IService<JobCollect> {

    /**
     *  添加或更新收藏
     * @param param
     * @return
     */
    boolean updateCollect(JobCollect param);


    /**
     * 查询收藏信息
     * @param userId
     * @param dataId
     * @return
     */
    JobCollect getCollect(String userId,String dataId,String roleCode);


    /**
     * 分页查询收藏记录列表
     * @param page
     * @param params 参数信息
     * @return
     */
    IPage<Map<String,Object>> getCollectList(Page<JobCollect> page, JobCollect params);

    /**
     * 查询指定用户数量统计
     * @param userId 参数信息
     * @return
     */
    Map<String, Object> getMyStatistics(String userId,String roleCode);



}
